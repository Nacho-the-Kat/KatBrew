package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.AnnouncementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/announcements")
@RequiredArgsConstructor
public class AnnouncementsRestController {

    private final AnnouncementsService announcementsService;

    @GetMapping()
    public ApiResponse<List<Announcements>> getAll() {
        return new ApiResponse<>(announcementsService.findAll());
    }

}
