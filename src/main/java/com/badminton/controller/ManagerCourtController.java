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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

        if (file == null || file.isEmpty()) {
            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);
        }

        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (court.getManager() == null || !court.getManager().getEmail().equals(managerEmail)) {
            throw new CustomException("You do not have permission to manage this court", HttpStatus.FORBIDDEN);
        }

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

    @PostMapping("/{courtId}/images/multiple")
    public ResponseEntity<ResponseDTO<List<String>>> uploadMultipleCourtImages(
            @PathVariable Long courtId,
            @RequestParam("files") MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new CustomException("Files array is empty", HttpStatus.BAD_REQUEST);
        }

        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (court.getManager() == null || !court.getManager().getEmail().equals(managerEmail)) {
            throw new CustomException("You do not have permission to manage this court", HttpStatus.FORBIDDEN);
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String imageUrl = cloudinaryService.uploadFile(file);
            CourtImage courtImage = CourtImage.builder()
                    .court(court)
                    .imageUrl(imageUrl)
                    .build();
            courtImageRepository.save(courtImage);
            imageUrls.add(imageUrl);
        }

        ResponseDTO<List<String>> response = ResponseDTO.<List<String>>builder()
                .success(true)
                .message("Images uploaded successfully")
                .data(imageUrls)
                .build();

        return ResponseEntity.ok(response);
    }
}
