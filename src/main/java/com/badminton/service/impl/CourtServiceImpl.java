package com.badminton.service.impl;

import com.badminton.dto.CourtDTO;
import com.badminton.entity.Court;
import com.badminton.repository.CourtRepository;
import com.badminton.service.CourtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;

    public CourtServiceImpl(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDTO> getAllCourts() {
        List<Court> courts = courtRepository.findAll();
        return courts.stream()
                .map(c -> CourtDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .address(c.getAddress())
                        .pricePerHour(c.getPricePerHour())
                        .managerId(c.getManager() != null ? c.getManager().getId() : null)
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
