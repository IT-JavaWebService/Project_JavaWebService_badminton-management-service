package com.badminton.controller;

import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.CloudinaryService;
import com.badminton.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final CloudinaryService cloudinaryService;

    public FileController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);
        }

        String url = cloudinaryService.uploadFile(file);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .success(true)
                .message("File uploaded successfully")
                .data(url)
                .build();

        return ResponseEntity.ok(response);
    }
}
