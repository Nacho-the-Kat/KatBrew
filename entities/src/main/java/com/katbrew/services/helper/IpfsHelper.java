package com.katbrew.services.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.nft.IpfsCollectionFolderData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpfsHelper {
    @Value("${data.fetchNFT.baseDirectory}")
    private String basePath;

    Pattern pattern = Pattern.compile("\\d+");

    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    //this is a general script, there you can pass the "function" you want to use
    private String lsScript;
    private String[] getScripts;

    @PostConstruct
    private void init() {
        this.getScripts = new String[]{
                Paths.get(basePath, "ipfs1.sh").toString(),
                Paths.get(basePath, "ipfs2.sh").toString(),
                Paths.get(basePath, "ipfs3.sh").toString(),
                Paths.get(basePath, "ipfs4.sh").toString()
        };
        this.lsScript = Paths.get(basePath, "ipfs-general.sh").toString();
    }

    public IpfsCollectionFolderData sendLS(final NftCollection collection) throws IOException {
        final ProcessBuilder pb = new ProcessBuilder(
                "bash",
                lsScript,
                collection.getBuri()
        );

        final Process process = pb.start();
        List<String> filenames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                filenames.add(line); // Printing the script output
            }
        } catch (IOException e) {
            log.info("Error on reading the script: " + e.getMessage());
            throw new IOException("Error reading the script outcome");
        }
        final IpfsCollectionFolderData data = new IpfsCollectionFolderData();
        if (!filenames.isEmpty()) {
            filenames = filenames.stream().filter(single -> pattern.matcher(single).find() || single.contains("collection")).toList();
            if (filenames.size() != collection.getMax().intValue()) {
                data.setIsSingleFileCollection(true);
            }
            filenames.forEach(single -> {
                if (pattern.matcher(single).find()) {
                    data.getImageDataNames().add(single);
                } else {
                    if (single.equals("collection")) {
                        data.setHasCollectionFile(true);
                    }
                }
            });
        }

        return data;
//        return mapper.readValue(string.toString(), IpfsBuriData.class);
    }

    public void sendGet(final String buri, final String savePath) throws IOException, InterruptedException {
        sendGet(1, buri, savePath);
    }

    public void sendGet(final Integer daemon, final String buri, final String savePath) throws InterruptedException, IOException {
        final ProcessBuilder pb = new ProcessBuilder(
                "bash",
                getScripts[daemon],
                buri,
                savePath
        );
        final Process process = pb.start();
        process.waitFor();
    }

}
