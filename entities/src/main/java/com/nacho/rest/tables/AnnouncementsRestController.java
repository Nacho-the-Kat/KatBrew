package com.nacho.rest.tables;

import com.nacho.entities.jooq.db.tables.pojos.Announcements;
import com.nacho.helper.UploadAnnouncements;
import com.nacho.services.base.ApiResponse;
import com.nacho.services.tables.AnnouncementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.nacho.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/announcements")
@RequiredArgsConstructor
public class AnnouncementsRestController {

    private final AnnouncementsService announcementsService;

    @GetMapping("/all")
    public ApiResponse<List<Announcements>> getAll() {
        return new ApiResponse<>(announcementsService.findAll());
    }

    @PostMapping(path = "/create", consumes = { MediaType.ALL_VALUE })
    public void create(@RequestBody final MultipartFile upload) throws IOException {
        System.out.println("ad");
//        announcementsService.create(upload, image);
    }
}
