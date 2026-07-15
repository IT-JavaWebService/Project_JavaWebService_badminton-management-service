package com.badminton.controller;

import com.badminton.dto.TimeSlotDTO;
import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.TimeSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<TimeSlotDTO>>> getAllTimeSlots() {
        List<TimeSlotDTO> slots = timeSlotService.getAllTimeSlots();
        ResponseDTO<List<TimeSlotDTO>> response = ResponseDTO.<List<TimeSlotDTO>>builder()
                .success(true)
                .message("Fetched time slots successfully")
                .data(slots)
                .build();
        return ResponseEntity.ok(response);
    }
}
