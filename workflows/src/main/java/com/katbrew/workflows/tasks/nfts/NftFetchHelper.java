package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.helper.FilesystemHelper;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.pojos.nft.IpfsCollectionFolderData;
import com.katbrew.services.helper.ImageService;
import com.katbrew.services.helper.IpfsHelper;
import com.katbrew.services.tables.NFTCollectionEntryService;
import com.katbrew.services.tables.NFTCollectionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class NftFetchHelper {

    @Value("${data.fetchNFT.baseDirectory}")
    private String basePath;
    @Value("${filesystem.krc721-static-path}")
    private String krc721staticPath;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    private final NFTCollectionEntryService nftCollectionEntryService;
    private final NFTCollectionInfoService nftCollectionInfoService;
    private final ImageService imageService;

    private final IpfsHelper ipfsHelper;
    private final NftHelper nftHelper = new NftHelper();
    private IpfsCollectionFolderData folderData;

    public void fetch(final NftCollection single) {
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
            folderData = ipfsHelper.sendLS(single);
            if (!Paths.get(tarFile + ".tar").toFile().exists()) {
                fetchMetadata(single, tarFile, tarParent);
            }

            extractTar(metadataDirExtract, tarFile, true);

            final List<NftCollectionEntry> entries = generateCollectionEntries(single);
            final String imgsBuri = entries.get(entries.size() - 1).getImage().split("/")[0];

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
            log.error("Exception Raised " + e.getMessage());
        }
    }


    private List<NftCollectionEntry> generateCollectionEntries(final NftCollection collection) throws IOException {
        final Path path = Paths.get(basePath, "metadata", collection.getBuri());

        final File dir = path.toFile();

        NftCollectionInfo collectionInfo;
        if (folderData.getHasCollectionFile()) {
            List<File> infos;
            if (folderData.getIsSingleFileCollection()){
                infos = List.of(dir.listFiles()[0]);
            }else{
                infos = Arrays.stream(Objects.requireNonNullElse(dir.listFiles((directory, name) -> name.contains(folderData.getCollectionFileName())), new File[]{})).toList();
            }

            final NftCollectionInfo info = nftHelper.convertInfoToDbEntry(mapper.readValue(infos.get(0), NFTCollectionInfoInternal.class));
            info.setFkCollection(collection.getId());
            collectionInfo = nftCollectionInfoService.insert(info);
        } else {
            collectionInfo = null;
        }
        List<NftCollectionEntry> list;
        if (!folderData.getIsSingleFileCollection()) {
            final List<File> files = new ArrayList<>(Arrays.stream(Objects.requireNonNullElse(dir.listFiles((directory, name) -> !name.contains("collection") && !name.contains("DS_Store") && !name.contains("metadata")), new File[]{})).toList());
            list = new ArrayList<>(files.stream().map(file -> {
                try {
                    final NftCollectionEntry entry = nftHelper.convertEntryToDbEntry(mapper.readValue(file, NFTCollectionEntryInternal.class));
                    entry.setFkCollection(collection.getId());
                    if (entry.getEdition() == null) {
                        try {
                            final String editionNumber = file.getName().replaceAll("\\D+$", "");
                            entry.setEdition(Integer.parseInt(editionNumber));
                        } catch (final Exception e) {
                            log.error("failed to parse the edition number for image url " + entry.getImage());
                        }
                    }
                    return entry;
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }).toList());
            list.sort(Comparator.comparingInt(NftCollectionEntry::getEdition));
        } else {
            list = IntStream.range(1, collection.getMax().intValue() + 1).mapToObj(single -> {
                final NftCollectionEntry entry = new NftCollectionEntry();
                if (collectionInfo != null) {
                    entry.setImage(collectionInfo.getImage());
                } else {
                    log.error("Something is wrong with the collection info for " + collection.getTick());
                }
                entry.setEdition(single);
                entry.setFkCollection(collection.getId());
                return entry;
            }).toList();
        }
        nftCollectionEntryService.batchInsert(list);
        return list;
    }

    private void generateThumbnails(final NftCollection collection) throws InterruptedException {
        log.info("Starting to generate the images");
        final Path path = Paths.get(krc721staticPath, "full", collection.getTick());
        final List<File> images = new ArrayList<>(Arrays.stream(Objects.requireNonNullElse(path.toFile().listFiles((directory, name) -> !name.contains("DS_Store")), new File[]{})).toList());

        final Path savePathThumbnails = Paths.get(krc721staticPath, "thumbnails", collection.getTick());
        final Path savePathSized = Paths.get(krc721staticPath, "sized", collection.getTick());
        if (!savePathThumbnails.toFile().exists()) {
            savePathThumbnails.toFile().mkdirs();
        }
        if (!savePathSized.toFile().exists()) {
            savePathSized.toFile().mkdirs();
        }
        if (
                Objects.requireNonNullElse(savePathThumbnails.toFile().listFiles(), new File[]{}).length != collection.getMax().intValue() ||
                        Objects.requireNonNullElse(savePathSized.toFile().listFiles(), new File[]{}).length != collection.getMax().intValue()
        ) {
            //100 Threads are too much
            final ExecutorService executor = Executors.newFixedThreadPool(50);
            Lists.partition(images, 50).forEach(batch -> executor.submit(() -> {
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
        } else {
            log.info("Skipping the image generation, thumbnails and sized images exists");
        }
    }

    private void fetchMetadata(final NftCollection collection, final Path tarFile, final Path tarParent) {
        //this path is to /metadata/compressed/tick
        tarFile.toFile().mkdirs();
        try {
            log.info("Starting to download the metadata for " + collection.getTick());
            final ExecutorService executorService = Executors.newFixedThreadPool(4);

            if (!folderData.getIsSingleFileCollection()) {
                List<List<String>> subSets = Lists.partition(folderData.getImageDataNames(), (int) Math.ceil((collection.getMax().intValue() / 4.0)));
                IntStream.range(0, subSets.size()).forEach(i -> executorService.submit(() -> {
                    subSets.get(i).forEach(single -> {
                        try {
                            ipfsHelper.sendGet(i, collection.getBuri() + "/" + single, Paths.get(tarFile.toString(), single).toString());
                        } catch (InterruptedException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }));
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.HOURS);
            } else {
                ipfsHelper.sendGet(collection.getBuri(), Paths.get(tarFile.toString(), "collection").toString());
            }
            if (folderData.getHasCollectionFile() && !folderData.getIsSingleFileCollection()){
                ipfsHelper.sendGet(1, collection.getBuri() + "/" + folderData.getCollectionFileName(), Paths.get(tarFile.toString(), folderData.getCollectionFileName()).toString());
            }
            packTar(Paths.get(tarParent.toString(), collection.getTick()), Paths.get(tarParent.toString(), collection.getTick() + ".tar"));
            FilesystemHelper.deleteAll(tarFile);

            log.info("Finished the metadata download for " + collection.getTick());
        } catch (Exception e) {
            log.error("fetching on metadata for " + collection.getTick() + ", " + e.getMessage());
            FilesystemHelper.deleteAll(tarFile);
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
                if (!folderData.getIsSingleFileCollection()) {
                    final ExecutorService executorService = Executors.newFixedThreadPool(4);
                    IntStream.range(0, subSets.size()).forEach(i -> {
                        executorService.submit(() -> {
                            final String scriptPath = Paths.get(basePath, "ipfs" + (i + 1) + ".sh").toString();
                            subSets.get(i).forEach(single -> {

                                final String splitted = single.getImage().split("/")[1];
                                final String img = imagePath + "/" + splitted;
                                try {
                                    ipfsHelper.sendGet(
                                            single.getImage(),
                                            img
                                    );
                                } catch (IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        });
                    });
                    executorService.shutdown();
                    executorService.awaitTermination(1, TimeUnit.HOURS);
                } else {
                    final String[] splitted = entries.get(0).getImage().split("/");
                    String img;
                    if (splitted.length > 1) {
                        img = imagePath + "/" + splitted[splitted.length -1];
                    } else {
                        img = imagePath + "/" + entries.get(0).getImage().split("\\?filename=")[1];
                    }
                    try {
                        ipfsHelper.sendGet(
                                entries.get(0).getImage(),
                                img
                        );
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                packTar(imagePath, Paths.get(imagePath + ".tar"));

                //deleting the unpacked folder in compressed directory
                FilesystemHelper.deleteAll(imagePath);

                log.info("Finished the image download for " + imageBuri);
            }
        } catch (Exception e) {
            log.error("fetching on images for " + imageBuri + ", " + e.getMessage());
        }
    }

    private void packTar(final Path folder, final Path tar) {
        try (final FileOutputStream fos = new FileOutputStream(tar.toString())) {
            try (final TarArchiveOutputStream tarOs = new TarArchiveOutputStream(fos)) {
                final File[] files = folder.toFile().listFiles();
                if (files != null) {
                    for (final File file : files) {
                        if (file.isFile()) {
                            TarArchiveEntry entry = new TarArchiveEntry(file, file.getName());
                            tarOs.putArchiveEntry(entry);
                            try (final FileInputStream fis = new FileInputStream(file)) {
                                IOUtils.copy(fis, tarOs);
                                tarOs.closeArchiveEntry();
                            }
                        }
                    }
                }
            }
        } catch (final IOException ex) {
            log.error("error on creating the tar " + ex.getMessage());
        }
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
