package com.badminton.service;

import com.badminton.dto.CourtDTO;
import java.util.List;

public interface CourtService {
    List<CourtDTO> getAllCourts();
    CourtDTO getCourtById(Long id);
}
