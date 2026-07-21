package com.badminton.controller;

import com.badminton.config.JwtRequestFilter;
import com.badminton.config.JwtUtil;
import com.badminton.config.SecurityConfig;
import com.badminton.dto.CourtDTO;
import com.badminton.exception.CustomException;
import com.badminton.service.CourtService;
import com.badminton.service.RedisTokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourtController.class)
@Import({SecurityConfig.class, JwtRequestFilter.class})
class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourtService courtService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private RedisTokenBlacklistService redisTokenBlacklistService;

    @Test
    void getAllCourts_ShouldReturnListOfCourts() throws Exception {
        CourtDTO court1 = CourtDTO.builder().id(1L).name("Court A").address("Address A").pricePerHour(100.0).build();
        CourtDTO court2 = CourtDTO.builder().id(2L).name("Court B").address("Address B").pricePerHour(120.0).build();

        when(courtService.getAllCourts()).thenReturn(List.of(court1, court2));

        mockMvc.perform(get("/api/v1/courts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Court A"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Court B"));
    }

    @Test
    void getCourtById_WhenCourtExists_ShouldReturnCourtDetails() throws Exception {
        CourtDTO court = CourtDTO.builder()
                .id(1L)
                .name("Court A")
                .address("Address A")
                .pricePerHour(100.0)
                .images(List.of("http://example.com/image1.jpg"))
                .build();

        when(courtService.getCourtById(1L)).thenReturn(court);

        mockMvc.perform(get("/api/v1/courts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Court A"))
                .andExpect(jsonPath("$.data.images[0]").value("http://example.com/image1.jpg"));
    }

    @Test
    void getCourtById_WhenCourtDoesNotExist_ShouldReturn404() throws Exception {
        when(courtService.getCourtById(999L)).thenThrow(new CustomException("Court not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/courts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Court not found"));
    }
}
