package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.scriptPath}")
    private String scriptPath;

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
                String[] execute = {
                        "bash",
                        scriptPath,
                        single.getTick(),
                        single.getBuri().replace("ipfs://", "")
                };
                try {
                    ProcessBuilder pb = new ProcessBuilder(execute);
                    Process process = pb.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            log.info(line); // Printing the script output
                        }
                    } catch (IOException e) {
                        log.info("Error on reading the script: " + e.getMessage());
                    }

                    generateCollectionEntries(single);
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

    private void generateCollectionEntries(final NftCollection collection) throws IOException {
        final Path path = Paths.get(basePath, "metadata", collection.getBuri());

        final File dir = path.toFile();
        final List<File> infos = Arrays.stream(Objects.requireNonNullElse(dir.listFiles(), new File[]{})).filter(file -> file.getName().contains("collection")).toList();
        if (!infos.isEmpty()) {
            final NftCollectionInfo info = nftHelper.convertInfoToDbEntry(mapper.readValue(infos.get(0), NFTCollectionInfoInternal.class));
            info.setFkCollection(collection.getId());
            nftCollectionInfoService.insert(info);
        }
        final List<NftCollectionEntry> list = Arrays.stream(Objects.requireNonNullElse(dir.listFiles(), new File[]{})).filter(file -> !file.getName().contains("collection")).map(file -> {
            try {
                final NftCollectionEntry entry = nftHelper.convertEntryToDbEntry(mapper.readValue(file, NFTCollectionEntryInternal.class));
                entry.setFkCollection(collection.getId());
                return entry;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        nftCollectionEntryService.batchInsert(list);
    }

    private void generateThumbnails(final NftCollection collection) throws IOException {
        final Path path = Paths.get(basePath, "images", collection.getTick());
        final List<File> images = Arrays.stream(Objects.requireNonNullElse(path.toFile().listFiles(), new File[]{})).toList();
        final Path savePath = Paths.get(krc721staticPath, "thumbnails", collection.getTick());
        if (!savePath.toFile().exists()) {
            savePath.toFile().mkdirs();
        }
        for (final File file : images) {
            final BufferedImage img = ImageIO.read(file);
            double ratio = (double) img.getWidth() / img.getHeight();
            imageService.generateThumbnail(file, savePath.toString(), file.getName(), FilenameUtils.getExtension(file.getName()), 403, (int) (403 / ratio));
        }
    }
}
