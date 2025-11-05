package com.example.demo.service;

import com.example.demo.dto.ServiceOfferingDTO;
import com.example.demo.model.ServiceOffering;
import com.example.demo.repo.ServiceOfferingRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOfferingServiceTest {

  @Mock
  private ServiceOfferingRepo repo;

  @InjectMocks
  private ServiceOfferingService service;

  private ServiceOffering testService;

  @BeforeEach
  void setUp() {
    testService = ServiceOffering.builder()
        .id(1L)
        .code("BATH")
        .name("Bath Service")
        .description("Full bath service for pets")
        .allowedDays("MONDAY,TUESDAY,WEDNESDAY")
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(17, 0))
        .build();
  }

  @Test
  void testFindAll_ShouldReturnListOfServiceOfferingDTOs() {
    // Given
    ServiceOffering service2 = ServiceOffering.builder()
        .id(2L)
        .code("GROOM")
        .name("Grooming Service")
        .description("Full grooming service")
        .allowedDays("MONDAY,TUESDAY")
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(16, 0))
        .build();

    List<ServiceOffering> services = Arrays.asList(testService, service2);
    when(repo.findAll()).thenReturn(services);

    // When
    List<ServiceOfferingDTO> result = service.findAll();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("BATH", result.get(0).code());
    assertEquals("GROOM", result.get(1).code());
    assertEquals("Bath Service", result.get(0).name());
    verify(repo, times(1)).findAll();
  }

  @Test
  void testFindAll_WhenEmpty_ShouldReturnEmptyList() {
    // Given
    when(repo.findAll()).thenReturn(Arrays.asList());

    // When
    List<ServiceOfferingDTO> result = service.findAll();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(repo, times(1)).findAll();
  }

  @Test
  void testFindById_WhenServiceExists_ShouldReturnServiceOfferingDTO() {
    // Given
    when(repo.findById(1L)).thenReturn(Optional.of(testService));

    // When
    ServiceOfferingDTO result = service.findById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("BATH", result.code());
    assertEquals("Bath Service", result.name());
    assertEquals("Full bath service for pets", result.description());
    verify(repo, times(1)).findById(1L);
  }

  @Test
  void testFindById_WhenServiceDoesNotExist_ShouldThrowResponseStatusException() {
    // Given
    when(repo.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.findById(999L));
    assertEquals(404, exception.getStatusCode().value());
    assertTrue(exception.getMessage().contains("Service with id 999 not found"));
    verify(repo, times(1)).findById(999L);
  }
}
