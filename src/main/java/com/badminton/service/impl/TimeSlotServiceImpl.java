package com.badminton.service.impl;

import com.badminton.dto.TimeSlotDTO;
import com.badminton.entity.TimeSlot;
import com.badminton.repository.TimeSlotRepository;
import com.badminton.service.TimeSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlots() {
        List<TimeSlot> slots = timeSlotRepository.findAll();
        return slots.stream()
                .map(s -> TimeSlotDTO.builder()
                        .id(s.getId())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .label(s.getLabel())
                        .build())
                .collect(Collectors.toList());
    }
}
