package com.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtDTO {
    private Long id;
    private String name;
    private String address;
    private Double pricePerHour;
    private Long managerId;
    private LocalDateTime createdAt;
}
