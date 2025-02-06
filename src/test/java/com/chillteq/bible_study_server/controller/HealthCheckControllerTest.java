package com.chillteq.bible_study_server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthCheck() {
        HealthCheckController controller = new HealthCheckController();
        assertEquals("UP", controller.healthCheck().getBody());
    }

    /**
     * Verifies authentication is working
     */
    @Test
    public void testHealthCheck_mockMvc() {
        try {
            ResultActions response = mockMvc.perform(get("/health"));
            response.andExpect(status().isOk());
        } catch (Exception e) {
            fail();
        }
    }
}