package com.badminton.service;

import com.badminton.dto.CourtDTO;
import com.badminton.entity.Court;
import com.badminton.entity.CourtImage;
import com.badminton.entity.User;
import com.badminton.exception.CustomException;
import com.badminton.repository.CourtImageRepository;
import com.badminton.repository.CourtRepository;
import com.badminton.service.impl.CourtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CourtImageRepository courtImageRepository;

    @InjectMocks
    private CourtServiceImpl courtService;

    private Court court;

    @BeforeEach
    void setUp() {
        User manager = User.builder().id(2L).email("manager@test.com").build();
        court = Court.builder()
                .id(1L)
                .name("Court 1")
                .address("123 Street")
                .pricePerHour(100.0)
                .manager(manager)
                .build();
    }

    @Test
    void getCourtById_WhenCourtExists_ShouldReturnCourtDTOWithImages() {
        CourtImage image = CourtImage.builder().id(10L).court(court).imageUrl("http://img.com/1.jpg").build();

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(courtImageRepository.findByCourtId(1L)).thenReturn(List.of(image));

        CourtDTO result = courtService.getCourtById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Court 1", result.getName());
        assertEquals(2L, result.getManagerId());
        assertEquals(1, result.getImages().size());
        assertEquals("http://img.com/1.jpg", result.getImages().get(0));
    }

    @Test
    void getCourtById_WhenCourtDoesNotExist_ShouldThrowCustomException() {
        when(courtRepository.findById(99L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> courtService.getCourtById(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Court not found", exception.getMessage());
    }
}
