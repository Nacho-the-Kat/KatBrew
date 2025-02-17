package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.rest.admin.helper.UploadAnnouncements;
import com.katbrew.services.helper.ImageService;
import com.katbrew.services.tables.AnnouncementsService;
import com.katbrew.services.tables.TokenService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;


@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/upload")
@RequiredArgsConstructor
public class FileUploadController {
    @Value("${filesystem.static-path}")
    private String root;

    private final AnnouncementsService announcementsService;
    private final TokenService tokenService;
    private final ImageService imageService;

    @PostMapping("/announcements")
    public void uploadAnnouncements(@ModelAttribute UploadAnnouncements upload) throws IOException {

        final Announcements announcements = new Announcements();
        announcements.setText(upload.getText());
        announcements.setTitle(upload.getTitle());
        announcements.setTimestamp(LocalDateTime.now());
        final Announcements newOne = announcementsService.insert(announcements);
        final String path = imageService.uploadFile(upload.getImage(), "/announcements/", String.valueOf(newOne.getId()));
        newOne.setImageUrl(path);
        announcementsService.update(newOne);
    }

    @PostMapping(value = "/logo/{tick}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadLogo(@RequestParam("file") final MultipartFile upload, @PathVariable final String tick) throws NotFoundException, IOException {
        final Token token = tokenService.findByTick(tick.toUpperCase());

        if (token == null) {
            throw new NotFoundException("Token tick not found");
        }
        final String filePath = imageService.uploadFile(upload, Paths.get("krc20", "logos").toString(), token.getTick());
        imageService.generateThumbnail(Paths.get(root, filePath), Paths.get("krc20", "thumbnails").toString(), 100);
        token.setLogo(filePath);
        tokenService.update(token);
    }
}