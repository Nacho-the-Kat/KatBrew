package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.services.helper.ImageService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.NFTCollectionEntryService;
import com.katbrew.services.tables.NFTCollectionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.baseDirectory}")
    private String basePath;
    @Value("${filesystem.krc721-static-path}")
    private String krc721staticPath;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();
    private final WebClient client = KatBrewWebClient.createWebClient();
    private final NFTCollectionEntryService nftCollectionEntryService;
    private final NFTCollectionInfoService nftCollectionInfoService;
    private final ImageService imageService;
    private final DSLContext dsl;
    private final LastUpdateService lastUpdateService;
    private final NftHelper nftHelper = new NftHelper();

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        final List<BigInteger> newCollectionIds = mapper.convertValue(execution.getVariable("newCollectionIds"), new TypeReference<>() {
        });

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            log.info("Starting the nft entry sync:" + LocalDateTime.now());
            final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                    .where(Tables.NFT_COLLECTION.ID.in(newCollectionIds))
                    .fetch()
                    .into(NftCollection.class);

            for (final NftCollection single : nfts) {
                log.info("Starting the nft entry:" + single.getTick());

                //Generating Metadata

                final Path metadataDirExtract = Paths.get(basePath, "metadata", single.getBuri());
                final Path buriDir = Paths.get(basePath, "metadata", single.getBuri());
                if (!buriDir.toFile().exists()) {
                    buriDir.toFile().mkdirs();
                }
                final Path tarParent = Paths.get(basePath, "metadata", "compressed");
                final Path tarFile = Paths.get(tarParent.toString(), single.getTick());
                try {
                    if (!Paths.get(tarFile + ".tar").toFile().exists()) {
                        fetchMetadata(single, tarFile, tarParent);
                    }

                    extractTar(metadataDirExtract, tarFile, true);

                    final List<NftCollectionEntry> entries = generateCollectionEntries(single);
                    final String imgsBuri = entries.get(entries.size() - 1).getImage().replace("ipfs://", "").split("/")[0];

                    //Metadata done

                    //Starting Images
                    final Path imageCompressedDir = Paths.get(basePath, "images", "compressed");
                    if (!imageCompressedDir.toFile().exists()) {
                        imageCompressedDir.toFile().mkdirs();
                    }
                    final Path imgFolderPath = Paths.get(imageCompressedDir.toString(), imgsBuri);
                    imgFolderPath.toFile().mkdirs();
                    fetchImageTar(entries, imgsBuri, imageCompressedDir);

                    final Path imagesDir = Paths.get(krc721staticPath, "full");
                    if (!imagesDir.toFile().exists()) {
                        imagesDir.toFile().mkdirs();
                    }
                    final Path staticFullFolder = Paths.get(imagesDir.toString(), single.getTick());
                    staticFullFolder.toFile().mkdirs();
                    extractTar(staticFullFolder, imgFolderPath, true);
                    generateThumbnails(single);

                    log.info("done with nft info " + single.getTick());
                } catch (Exception e) {
                    System.out.println("Exception Raised" + e.getMessage());
                }
            }
            log.info("done with generating nft entry and collection infos");
            lastUpdateService.releaseTask("fetchNFT");
        });
        executorService.shutdown();
    }

    private List<NftCollectionEntry> generateCollectionEntries(final NftCollection collection) throws IOException {
        final Path path = Paths.get(basePath, "metadata", collection.getBuri());

        final File dir = path.toFile();
        final List<File> infos = Arrays.stream(Objects.requireNonNullElse(dir.listFiles((directory, name) -> name.contains("collection")), new File[]{})).toList();
        if (!infos.isEmpty()) {
            final NftCollectionInfo info = nftHelper.convertInfoToDbEntry(mapper.readValue(infos.get(0), NFTCollectionInfoInternal.class));
            info.setFkCollection(collection.getId());
            nftCollectionInfoService.insert(info);
        }
        final List<File> files = new ArrayList<>(Arrays.stream(Objects.requireNonNullElse(dir.listFiles((directory, name) -> !name.contains("collection") && !name.contains("DS_Store") && !name.contains("metadata")), new File[]{})).toList());
        files.sort(Comparator.comparingInt(single -> Integer.parseInt(single.getName().replace(".json", ""))));
        final List<NftCollectionEntry> list = files.stream().map(file -> {
            try {
                final NftCollectionEntry entry = nftHelper.convertEntryToDbEntry(mapper.readValue(file, NFTCollectionEntryInternal.class));
                entry.setFkCollection(collection.getId());
                if (entry.getEdition() == null) {
                    final String editionNumber = entry.getImage().split("/")[1].split("\\.")[0];
                    entry.setEdition(Integer.parseInt(editionNumber));
                }
                return entry;
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }).toList();

        nftCollectionEntryService.batchInsert(list);
        return list;
    }

    private void generateThumbnails(final NftCollection collection) throws InterruptedException {
        log.info("Starting to generate the images");
        final Path path = Paths.get(krc721staticPath, "full", collection.getTick());
        final List<File> images = new ArrayList<>(Arrays.stream(Objects.requireNonNullElse(path.toFile().listFiles((directory, name) -> !name.contains("DS_Store")), new File[]{})).toList());
        if (!images.isEmpty()) {
            final String extension = "." + FilenameUtils.getExtension(images.get(0).getName());
            images.sort(Comparator.comparingInt(single -> Integer.parseInt(single.getName().replace(extension, ""))));
        }

        final Path savePathThumbnails = Paths.get(krc721staticPath, "thumbnails", collection.getTick());
        final Path savePathSized = Paths.get(krc721staticPath, "sized", collection.getTick());
        if (!savePathThumbnails.toFile().exists()) {
            savePathThumbnails.toFile().mkdirs();
        }
        if (!savePathSized.toFile().exists()) {
            savePathSized.toFile().mkdirs();
        }
        final ExecutorService executor = Executors.newFixedThreadPool(100);
        Lists.partition(images, 100).forEach(batch -> executor.submit(() -> {
            for (final File file : batch) {
                try {
                    final BufferedImage img = ImageIO.read(file);
                    double ratio = (double) img.getWidth() / img.getHeight();
                    imageService.generateThumbnail(img, savePathThumbnails.toString(), file.getName(), FilenameUtils.getExtension(file.getName()), 360, (int) (360 / ratio));
                    imageService.generateThumbnail(img, savePathSized.toString(), file.getName(), FilenameUtils.getExtension(file.getName()), 720, (int) (720 / ratio));
                } catch (IOException e) {
                    log.error("Failed to generate Image " + file.getName() + " " + e.getMessage());
                }
            }
        }));
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.HOURS);
        log.info("Finished to generate the images");
    }

    private void fetchMetadata(final NftCollection collection, final Path tarFile, final Path tarParent) {
        //this path is to /metadata/compressed/tick
        tarFile.toFile().mkdirs();
        try {
            log.info("Starting to download the metadata for " + collection.getTick());
            List<List<Integer>> subSets = Lists.partition(IntStream.range(1, collection.getMax().intValue() + 1).boxed().toList(), (int) Math.ceil((collection.getMax().intValue() / 4.0)));
            final ExecutorService executorService = Executors.newFixedThreadPool(4);
            String prefix = "";
            try {
                //if this call fails, it needs a .json on the url
                client.post()
                        .uri("http://localhost:5001/api/v0/get?arg=" + collection.getBuri() + "/1")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (final Exception e) {
                prefix = ".json";
            }

            final String finalPrefix = prefix;
            IntStream.range(0, subSets.size()).forEach(i -> executorService.submit(() -> {
                final String scriptPath = Paths.get(basePath, "ipfs" + (i + 1) + ".sh").toString();
                subSets.get(i).forEach(single -> {
                    final Path dataPath = Paths.get(tarFile.toString(), single + finalPrefix);
//                    final ProcessBuilder pb = new ProcessBuilder(
//                            "curl",
//                            "-L",
//                            "-X",
//                            "POST",
//                            "http://localhost:" + port + "/api/v0/get?arg=" + collection.getBuri() + "/" + single + finalPrefix,
//                            "--output",
//                            tarPath.toString()
//                    );
                    final ProcessBuilder pb = new ProcessBuilder(
                            "bash",
                            scriptPath,
                            collection.getBuri() + "/" + single + finalPrefix,
                            dataPath.toString()
                    );
                    awaitProcess(pb);
//                    extractTar(tarFile, tarPath, false);

//                    try {
//                        Files.deleteIfExists(tarPath);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
                });
            }));
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            packTar(Paths.get(tarParent.toString(), collection.getTick()), Paths.get(tarParent.toString(), collection.getTick() + ".tar"));

            ProcessBuilder pbRemoveFolder = new ProcessBuilder(
                    "rm",
                    "-rf",
                    tarFile.toString()
            );
            awaitProcess(pbRemoveFolder);

            log.info("Finished the metadata download for " + collection.getTick());
        } catch (Exception e) {
            log.error("fetching on metadata for " + collection.getTick() + ", " + e.getMessage());
        }
    }

    private void fetchImageTar(final List<NftCollectionEntry> entries, final String imageBuri, final Path compressedImageDir) {
        //compressed img dir is /images/compressed
        try {
            final List<List<NftCollectionEntry>> subSets = Lists.partition(entries, (int) Math.ceil((entries.size() / 4.0)));
            final Path imagePath = Paths.get(compressedImageDir.toString(), imageBuri);

            if (Paths.get(compressedImageDir.toString(), imageBuri + ".tar").toFile().exists()) {
                log.info("Tar already exist, no download");
            } else {
                log.info("Starting to download the images for " + imageBuri);
                final ExecutorService executorService = Executors.newFixedThreadPool(4);
                IntStream.range(0, subSets.size()).forEach(i -> {
                    executorService.submit(() -> {
                        final String scriptPath = Paths.get(basePath, "ipfs" + (i + 1) + ".sh").toString();
                        subSets.get(i).forEach(single -> {

                            final String splitted = single.getImage().split("/")[1];
                            final String img = imagePath + "/" + splitted;
//                            final ProcessBuilder pb = new ProcessBuilder(
//                                    "curl",
//                                    "-L",
//                                    "-X",
//                                    "GET",
//                                    "http://localhost:" + port + "/ipfs/" + single.getImage(),
//                                    "--output",
//                                    img
//                            );
                            final ProcessBuilder pb = new ProcessBuilder(
                                    "bash",
                                    scriptPath,
                                    single.getImage(),
                                    img
                            );
                            awaitProcess(pb);
                        });
                    });
                });
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.HOURS);
                packTar(imagePath, Paths.get(imagePath + ".tar"));

                //deleting the unpacked folder in compressed directory
                ProcessBuilder pbRemove = new ProcessBuilder(
                        "rm",
                        "-rf",
                        imagePath.toString()
                );
                awaitProcess(pbRemove);

                log.info("Finished the image download for " + imageBuri);
            }
        } catch (Exception e) {
            log.error("fetching on images for " + imageBuri + ", " + e.getMessage());
        }
    }

    private void packTar(final Path folder, final Path tar) throws IOException {
        final FileOutputStream fos = new FileOutputStream(tar.toString());
        final TarArchiveOutputStream tarOs = new TarArchiveOutputStream(fos);

        final File[] files = folder.toFile().listFiles();

        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    TarArchiveEntry entry = new TarArchiveEntry(file, file.getName());
                    tarOs.putArchiveEntry(entry);
                    FileInputStream fis = new FileInputStream(file);
                    IOUtils.copy(fis, tarOs);
                    tarOs.closeArchiveEntry();
                    fis.close();
                }
            }
        }
        tarOs.close();
    }

    private void extractTar(final Path metadataDir, final Path tarFile, final Boolean withLog) {
        if (withLog) {
            log.info("Extracting tar " + tarFile);
        }
        try (final TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(tarFile + (tarFile.toString().contains(".tar") ? "" : ".tar")))) {
            TarArchiveEntry entry;

            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    final File directory = new File(metadataDir.toString(), entry.getName());
                    directory.mkdirs();
                } else {
                    final File file = new File(metadataDir.toString(), entry.getName());
                    try (final FileOutputStream outputStream = new FileOutputStream(file)) {
                        IOUtils.copy(tarIn, outputStream);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        if (withLog) {
            log.info("Done extracting tar " + tarFile);
        }
    }

    private void awaitProcess(final ProcessBuilder pb) {
        try {
            final Process process = pb.start();
//            readProcess(process);
            process.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void readProcess(final Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line); // Printing the script output
            }
        } catch (IOException e) {
            log.info("Error on reading the script: " + e.getMessage());
        }
    }
}
