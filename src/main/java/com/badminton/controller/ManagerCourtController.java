package com.badminton.controller;

import com.badminton.dto.response.ResponseDTO;
import com.badminton.entity.Court;
import com.badminton.entity.CourtImage;
import com.badminton.exception.CustomException;
import com.badminton.repository.CourtImageRepository;
import com.badminton.repository.CourtRepository;
import com.badminton.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/manager/courts")
public class ManagerCourtController {

    private final CloudinaryService cloudinaryService;
    private final CourtRepository courtRepository;
    private final CourtImageRepository courtImageRepository;

    public ManagerCourtController(CloudinaryService cloudinaryService,
                                  CourtRepository courtRepository,
                                  CourtImageRepository courtImageRepository) {
        this.cloudinaryService = cloudinaryService;
        this.courtRepository = courtRepository;
        this.courtImageRepository = courtImageRepository;
    }

    @PostMapping("/{courtId}/images")
    public ResponseEntity<ResponseDTO<String>> uploadCourtImage(
            @PathVariable Long courtId,
            @RequestParam("file") MultipartFile file) {

        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        String imageUrl = cloudinaryService.uploadFile(file);

        CourtImage courtImage = CourtImage.builder()
                .court(court)
                .imageUrl(imageUrl)
                .build();
        courtImageRepository.save(courtImage);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .success(true)
                .message("Image uploaded successfully")
                .data(imageUrl)
                .build();

        return ResponseEntity.ok(response);
    }
}
