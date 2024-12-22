package com.nacho.rest.base;

import com.nacho.entities.jooq.db.tables.pojos.Announcements;
import com.nacho.exceptions.NotValidException;
import com.nacho.helper.UploadAnnouncements;
import com.nacho.services.tables.AnnouncementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static com.nacho.rest.base.StaticVariables.ADMIN_URL_PREFIX;


@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/upload")
@RequiredArgsConstructor
public class FileUploadController {
    @Value("${filesystem.static-path}")
    private String root;

    private final AnnouncementsService announcementsService;

    @RequestMapping("/announcements")
    @PostMapping
    public void uploadAnnouncements(@ModelAttribute UploadAnnouncements upload) {
        try {
            final MultipartFile image = upload.getImage();
            String[] nameSplit = image.getOriginalFilename().split("\\.");
            if (nameSplit.length > 2) {
                throw new NotValidException("to much dots in filename");
            }
            final Announcements announcements = new Announcements();
            announcements.setText(upload.getText());
            announcements.setTitle(upload.getTitle());
            announcements.setTimestamp(LocalDateTime.now());
            announcementsService.insert(announcements);
            image.transferTo(Paths.get(root + "/announcements", image.getOriginalFilename()));

        } catch (IOException e) {
            throw new NotValidException("Fehler beim Hochladen der Datei");
        }
    }
}