package com.katbrew.services.helper;

import com.katbrew.exceptions.NotValidException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {
    @Value("${filesystem.static-path}")
    private String root;

    public void generateThumbnail(final Path file, final String path, final int thumbnailWidth) throws IOException {
        this.generateThumbnail(file, path, thumbnailWidth, thumbnailWidth);
    }

    public void generateThumbnail(final Path file, final String path, final int thumbnailWidth, final int thumbnailHeight) throws IOException {
        if (file.toFile().exists()) {
            final BufferedImage originalImage = ImageIO.read(new File(file.toString()));
            final BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, 1);
            final Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
            g.dispose();

            final String extension = FilenameUtils.getExtension(file.toString());

            final String outputThumbnailPath = Paths.get(root, path, file.getFileName().toString()).toString();
            final File outputThumbnailFile = new File(outputThumbnailPath);
            ImageIO.write(thumbnail, extension, outputThumbnailFile);
        }
    }

    public void generateThumbnail(final File file, final String savePath, final String filename, final String extension, final int thumbnailWidth, final int thumbnailHeight) throws IOException {
        if (file.exists()) {
            final BufferedImage originalImage = ImageIO.read(new File(file.toPath().toString()));
            generateThumbnailAndSave(originalImage, savePath, filename, extension, thumbnailWidth, thumbnailHeight);
        }
    }

    public String uploadFile(final MultipartFile file, final String path, final String filename) throws IOException {
        if (file != null && !file.isEmpty()) {
            String[] nameSplit = file.getOriginalFilename().split("\\.");
            if (nameSplit.length > 2) {
                throw new NotValidException("to much dots in filename");
            }

            final Path filepath = Paths.get(root, path, filename + "." + nameSplit[1]);
            file.transferTo(filepath);
            return filepath.toString();
        }
        return null;
    }

    private void generateThumbnailAndSave(final BufferedImage originalImage, final String savePath, final String filename, final String extension, final int thumbnailWidth, final int thumbnailHeight) throws IOException {
        final BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, 1);
        final Graphics2D g = thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        g.dispose();

        final String outputThumbnailPath = Paths.get(savePath, filename).toString();
        final File outputThumbnailFile = new File(outputThumbnailPath);
        ImageIO.write(thumbnail, extension, outputThumbnailFile);
    }
}
