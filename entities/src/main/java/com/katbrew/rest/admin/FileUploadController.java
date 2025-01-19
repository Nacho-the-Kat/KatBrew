package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.exceptions.NotValidException;
import com.katbrew.rest.admin.helper.UploadAnnouncements;
import com.katbrew.services.tables.AnnouncementsService;
import com.katbrew.services.tables.TokenService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

    @PostMapping("/announcements")
    public void uploadAnnouncements(@ModelAttribute UploadAnnouncements upload) throws IOException {

        final Announcements announcements = new Announcements();
        announcements.setText(upload.getText());
        announcements.setTitle(upload.getTitle());
        announcements.setTimestamp(LocalDateTime.now());
        final Announcements newOne = announcementsService.insert(announcements);
        final String path = uploadFile(upload.getImage(), "/announcements/", String.valueOf(newOne.getId()));
        newOne.setImageUrl(path);
        announcementsService.update(newOne);
    }

    @PostMapping(value = "/logo/{tick}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadLogo(@RequestParam("file") final MultipartFile upload, @PathVariable final String tick) throws NotFoundException, IOException {
        final Token token = tokenService.findByTick(tick.toUpperCase());

        if (token == null) {
            throw new NotFoundException("Token tick not found");
        }
        final String filePath = uploadFile(upload, "krc20-logos", token.getTick());
        generateThumbnail(Paths.get(root, filePath), "krc20-thumbnails", 100);
        token.setLogo(filePath);
        tokenService.update(token);
    }

    private String uploadFile(final MultipartFile file, final String path, final String filename) throws IOException {
        if (file != null && !file.isEmpty()) {
            String[] nameSplit = file.getOriginalFilename().split("\\.");
            if (nameSplit.length > 2) {
                throw new NotValidException("to much dots in filename");
            }

            final String filepath = "/" + path + "/" + filename + "." + nameSplit[1];
            file.transferTo(Paths.get(root, filepath));
            return filepath;
        }
        return null;
    }

    private void generateThumbnail(final Path file, final String path, final int thumbnailSize) throws IOException {
        if (file.toFile().exists()) {
            final BufferedImage originalImage = ImageIO.read(new File(file.toString()));
            final BufferedImage thumbnail = new BufferedImage(thumbnailSize, thumbnailSize, 1);
            final Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, thumbnailSize, thumbnailSize, null);
            g.dispose();

            final String extension = FilenameUtils.getExtension(file.toString());

            final String outputThumbnailPath = Paths.get(root, path, file.getFileName().toString()).toString();
            final File outputThumbnailFile = new File(outputThumbnailPath);
            ImageIO.write(thumbnail, extension, outputThumbnailFile);
        }
    }
}