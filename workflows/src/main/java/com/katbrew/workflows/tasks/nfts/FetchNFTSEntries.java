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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.baseDirectory}")
    private String basePath;
    @Value("${filesystem.krc721-static-path}")
    private String krc721staticPath;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

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

                final Path metadataDir = Paths.get(basePath, "metadata");
                final Path buriDir = Paths.get(basePath, "metadata", single.getBuri());
                if (!buriDir.toFile().exists()) {
                    buriDir.toFile().mkdirs();
                }
                final Path tarFile = Paths.get(basePath, "metadata", "compressed", single.getTick());
                try {
                    if (!Paths.get(tarFile + ".tar").toFile().exists()) {
                        fetchMetadata(single, tarFile);
                    }

                    extractTar(metadataDir, tarFile);
                    final String imgsBuri = generateCollectionEntries(single);

                    final Path imageCompressedDir = Paths.get(basePath, "images", "compressed");
                    if (!imageCompressedDir.toFile().exists()) {
                        imageCompressedDir.toFile().mkdirs();
                    }
                    fetchImageTar(imgsBuri, imageCompressedDir);
                    final Path imagesDir = Paths.get(krc721staticPath, "full");
                    if (!imagesDir.toFile().exists()) {
                        log.info(imagesDir.toString());
                        imagesDir.toFile().mkdirs();
                    }
                    extractTar(imagesDir, Paths.get(imageCompressedDir.toString(), imgsBuri));
                    Files.move(Paths.get(imagesDir.toString(), imgsBuri), Paths.get(imagesDir.toString(), single.getTick()));
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

    private String generateCollectionEntries(final NftCollection collection) throws IOException {
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
                return entry;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        nftCollectionEntryService.batchInsert(list);
        return list.get(files.size() - 1).getImage().replace("ipfs://", "").split("/")[0];
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

    private void fetchMetadata(final NftCollection collection, final Path tarFile) {

        try {
            log.info("Starting to download the metadata for " + collection.getTick());
            ProcessBuilder pb = new ProcessBuilder("ipfs",
                    "get",
                    collection.getBuri(),
                    "--output=" + tarFile.toString(),
                    "--archive=true"
            );
            final Process process = pb.start();
            readProcess(process);
            process.waitFor();
            log.info("Finished the metadata download for " + collection.getTick());
        } catch (Exception e) {
            log.error("fetching on metadata for " + collection.getTick() + ", " + e.getMessage());
        }
    }

    private void fetchImageTar(final String imageBuri, final Path compressedImageDir) {

        try {
            final Path imageTar = Paths.get(compressedImageDir.toString(), imageBuri);
            if (imageTar.toFile().exists()) {
                log.info("Tar already exist, no download");
            } else {
                log.info("Starting to download the images for " + imageBuri);
                ProcessBuilder pb = new ProcessBuilder("ipfs",
                        "get",
                        imageBuri,
                        "--output=" + imageTar,
                        "--archive=true"
                );
                final Process process = pb.start();
                readProcess(process);
                process.waitFor();
                log.info("Finished the image download for " + imageBuri);
            }
        } catch (Exception e) {
            log.error("fetching on images for " + imageBuri + ", " + e.getMessage());
        }
    }

    private void extractTar(final Path metadataDir, final Path tarFile) {
        log.info("Extracting tar " + tarFile.toString());
        try (final TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(tarFile.toString() + ".tar"))) {
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
            throw new RuntimeException(e);
        }
        log.info("Done extracting tar " + tarFile.toString());
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
