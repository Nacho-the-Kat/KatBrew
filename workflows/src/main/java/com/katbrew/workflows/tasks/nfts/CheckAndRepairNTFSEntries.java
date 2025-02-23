package com.katbrew.workflows.tasks.nfts;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.services.tables.NFTCollectionEntryService;
import com.katbrew.services.tables.NFTCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckAndRepairNTFSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.baseDirectory}")
    private String basePath;
    @Value("${filesystem.krc721-static-path}")
    private String krc721staticPath;
    private final NFTCollectionService nftCollectionService;
    private final NFTCollectionEntryService nftCollectionEntryService;
    private final DSLContext dsl;

    @Override
    public void execute(DelegateExecution execution) {
        final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                .where(Tables.NFT_COLLECTION.COMPLETED.eq(false))
                .fetchInto(NftCollection.class);

        final List<BigInteger> failedCollectionIds = new ArrayList<>();
        final List<NftCollection> toUpdate = new ArrayList<>();
        for (final NftCollection collection : nfts) {
            log.info("Verifying NFT entries for: " + collection.getTick());

            try {
                if (!verifyEntryCount(collection)) {
                    failedCollectionIds.add(collection.getId());
                    continue; // Überspringe weitere Prüfungen, wenn die Anzahl der Einträge nicht stimmt
                }

                verifyMetadataTar(collection);
                verifyMetadataFolder(collection);
                verifyImageTar(collection);
                verifyImageFolder(collection);

                log.info("Verification successful for: " + collection.getTick());
                collection.setCompleted(true);
                toUpdate.add(collection);
            } catch (VerificationException e) {
                log.error("Verification failed for: " + collection.getTick() + ", " + e.getMessage());
                failedCollectionIds.add(collection.getId());
            }
        }
        nftCollectionService.batchUpdate(toUpdate);
        execution.setVariable("newCollectionIds", failedCollectionIds);
    }

    private boolean verifyEntryCount(NftCollection collection) {
        final BigInteger entryCount = dsl.selectCount()
                .from(Tables.NFT_COLLECTION_ENTRY)
                .where(Tables.NFT_COLLECTION_ENTRY.FK_COLLECTION.eq(collection.getId()))
                .fetchOneInto(BigInteger.class);

        if (entryCount == null || entryCount.intValue() != collection.getMax().intValue()) {
            log.warn("Entry count mismatch for: " + collection.getTick() + ". Expected: " + collection.getMax() + ", Found: " + entryCount);
            log.warn("Dropping the collection entries from DB");
            final List<NftCollectionEntry> entries = dsl.selectFrom(Tables.NFT_COLLECTION_ENTRY)
                    .where(Tables.NFT_COLLECTION_ENTRY.FK_COLLECTION.eq(collection.getId()))
                    .fetchInto(NftCollectionEntry.class);
            nftCollectionEntryService.batchDelete(entries);
            return false;
        }
        return true;
    }

    private void verifyMetadataTar(NftCollection collection) throws VerificationException {
        final Path tarFile = Paths.get(basePath, "metadata", "compressed", collection.getTick() + ".tar");
        if (!Files.exists(tarFile)) {
            throw new VerificationException("Metadata TAR file not found: " + tarFile);
        }

        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(tarFile.toFile()))) {
            long entryCount = 0;
            while (tarIn.getNextTarEntry() != null) {
                entryCount++;
            }
            if (entryCount != collection.getMax().intValue()) {
                throw new VerificationException("Metadata TAR entry count mismatch for: " + collection.getTick() + ". Expected: " + collection.getMax() + ", Found: " + entryCount);
            }
        } catch (IOException e) {
            throw new VerificationException("Error reading metadata TAR file: " + e.getMessage());
        }
    }

    private void verifyMetadataFolder(NftCollection collection) throws VerificationException {
        final Path metadataFolder = Paths.get(basePath, "metadata", collection.getBuri());
        if (!Files.exists(metadataFolder)) {
            throw new VerificationException("Metadata folder not found: " + metadataFolder);
        }

        long fileCount = Arrays.stream(Objects.requireNonNullElse(metadataFolder.toFile().listFiles(), new File[]{}))
                .filter(file -> file.isFile() && !file.getName().contains("collection") && !file.getName().contains("DS_Store"))
                .count();

        if (fileCount != collection.getMax().intValue()) {
            throw new VerificationException("Metadata folder file count mismatch for: " + collection.getTick() + ". Expected: " + collection.getMax() + ", Found: " + fileCount);
        }
    }

    private void verifyImageTar(final NftCollection collection) throws VerificationException {
        final String imagesBuris = dsl.select(Tables.NFT_COLLECTION_ENTRY.IMAGE)
                .from(Tables.NFT_COLLECTION_ENTRY)
                .where(Tables.NFT_COLLECTION_ENTRY.FK_COLLECTION.eq(collection.getId()))
                .limit(1)
                .fetchOne(Tables.NFT_COLLECTION_ENTRY.IMAGE);

        if (imagesBuris == null || imagesBuris.isEmpty()) {
            throw new VerificationException("No images found for collection: " + collection.getTick());
        }

        final String imageBuri = imagesBuris.split("/")[0];
        final Path imageTar = Paths.get(basePath, "images", "compressed", imageBuri + ".tar");

        if (!Files.exists(imageTar)) {
            throw new VerificationException("Image TAR file not found: " + imageTar);
        }

        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(imageTar.toFile()))) {
            long entryCount = 0;
            while (tarIn.getNextTarEntry() != null) {
                entryCount++;
            }
            if (entryCount != collection.getMax().intValue()) {
                throw new VerificationException("Image TAR entry count mismatch for: " + collection.getTick() + ". Expected: " + collection.getMax() + ", Found: " + entryCount);
            }
        } catch (IOException e) {
            throw new VerificationException("Error reading image TAR file: " + e.getMessage());
        }
    }

    private void verifyImageFolder(NftCollection collection) throws VerificationException {
        final Path imageFolder = Paths.get(krc721staticPath, "full", collection.getTick());
        if (!Files.exists(imageFolder)) {
            throw new VerificationException("Image folder not found: " + imageFolder);
        }

        long fileCount = Arrays.stream(Objects.requireNonNullElse(imageFolder.toFile().listFiles(), new File[]{}))
                .filter(File::isFile)
                .count();

        if (fileCount != collection.getMax().intValue()) {
            throw new VerificationException("Image folder file count mismatch for: " + collection.getTick() + ". Expected: " + collection.getMax() + ", Found: " + fileCount);
        }
    }

    static class VerificationException extends Exception {
        public VerificationException(String message) {
            super(message);
        }
    }
}
