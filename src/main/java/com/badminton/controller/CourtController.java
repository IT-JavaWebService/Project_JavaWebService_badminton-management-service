package com.badminton.controller;

import com.badminton.dto.CourtDTO;
import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.CourtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<CourtDTO>>> getAllCourts() {
        List<CourtDTO> courts = courtService.getAllCourts();
        ResponseDTO<List<CourtDTO>> response = ResponseDTO.<List<CourtDTO>>builder()
                .success(true)
                .message("Fetched courts successfully")
                .data(courts)
                .build();
        return ResponseEntity.ok(response);
    }
}
