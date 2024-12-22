package com.nacho.rest.base;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/app")
public class AppDownloadRestController {
    @Value("${filesystem.root-path}")
    private String root;

    @RequestMapping("/release")
    @GetMapping
    public void getApp(HttpServletResponse response) throws IOException {
        final Path file = Paths.get(root + "/app/app-release.apk");
        String contentType = Files.probeContentType(file);
        response.setContentType(contentType);
        response.setContentLengthLong(Files.size(file));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(file.getFileName().toString(), StandardCharsets.UTF_8)
                .build()
                .toString());
        Files.copy(file, response.getOutputStream());
    }
}
