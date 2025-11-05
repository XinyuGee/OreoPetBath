package com.example.demo.handler;

import com.example.demo.exception.BookingConflictException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {GlobalExceptionHandlerTest.TestController.class},
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.example.demo.handler")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test-exception")
    static class TestController {
        @GetMapping("/booking-conflict")
        public void throwBookingConflict() {
            throw new BookingConflictException("Test booking conflict");
        }

        @GetMapping("/entity-not-found")
        public void throwEntityNotFound() {
            throw new EntityNotFoundException("Entity not found");
        }

        @GetMapping("/generic-exception")
        public void throwGenericException() {
            throw new RuntimeException("Generic error");
        }
    }

    @Test
    void testHandleBookingConflict_ShouldReturn409() throws Exception {
        // When & Then
        mockMvc.perform(get("/test-exception/booking-conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Test booking conflict"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleEntityNotFound_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/test-exception/entity-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Requested resource not found."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testHandleGenericException_ShouldReturn500() throws Exception {
        // When & Then
        mockMvc.perform(get("/test-exception/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected error occurred."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

