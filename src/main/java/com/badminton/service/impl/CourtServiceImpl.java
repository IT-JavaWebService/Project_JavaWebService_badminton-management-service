package com.badminton.service.impl;

import com.badminton.dto.CourtDTO;
import com.badminton.entity.Court;
import com.badminton.entity.CourtImage;
import com.badminton.exception.CustomException;
import com.badminton.repository.CourtImageRepository;
import com.badminton.repository.CourtRepository;
import com.badminton.service.CourtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final CourtImageRepository courtImageRepository;

    public CourtServiceImpl(CourtRepository courtRepository, CourtImageRepository courtImageRepository) {
        this.courtRepository = courtRepository;
        this.courtImageRepository = courtImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtDTO> getAllCourts() {
        List<Court> courts = courtRepository.findAll();
        return courts.stream()
                .map(c -> {
                    List<String> imageUrls = courtImageRepository.findByCourtId(c.getId())
                            .stream()
                            .map(CourtImage::getImageUrl)
                            .collect(Collectors.toList());

                    return CourtDTO.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .address(c.getAddress())
                            .pricePerHour(c.getPricePerHour())
                            .managerId(c.getManager() != null ? c.getManager().getId() : null)
                            .images(imageUrls)
                            .createdAt(c.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourtDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        List<String> imageUrls = courtImageRepository.findByCourtId(id)
                .stream()
                .map(CourtImage::getImageUrl)
                .collect(Collectors.toList());

        return CourtDTO.builder()
                .id(court.getId())
                .name(court.getName())
                .address(court.getAddress())
                .pricePerHour(court.getPricePerHour())
                .managerId(court.getManager() != null ? court.getManager().getId() : null)
                .images(imageUrls)
                .createdAt(court.getCreatedAt())
                .build();
    }
}
