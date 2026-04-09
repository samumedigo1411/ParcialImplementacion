package com.name.caseservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.name.caseservice.dto.CaseRequestDTO;
import com.name.caseservice.model.CasePriority;
import com.name.caseservice.model.CaseStatus;
import com.name.caseservice.service.CaseService;
import com.name.caseservice.controller.dto.CaseResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseService caseService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private CaseResponseDTO testResponse;
    private CaseRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testResponse = new CaseResponseDTO();
        testResponse.setId(1L);
        testResponse.setTitle("Caso Aris");
        testResponse.setDescription("Encontrado en su estudio");
        testResponse.setPriority(CasePriority.High);
        testResponse.setStatus(CaseStatus.Open);
        testResponse.setAssignedDetective("Luis R");
        testRequest = new CaseRequestDTO();
        testRequest.setTitle("Caso Aris");
        testRequest.setDescription("Encontrado en su estudio");
        testRequest.setPriority(CasePriority.High);
        testRequest.setAssignedDetective("Luis R");
    }

    @Test
    void POST_cases_ShouldReturn201_WhenValidRequest() throws Exception {
        when(caseService.createCase(any())).thenReturn(testResponse);
        mockMvc.perform(post("/cases").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testRequest))).andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Caso Aris")).andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void GET_cases_ShouldReturn200_WithListOfCases() throws Exception {
        when(caseService.getAllCases()).thenReturn(List.of(testResponse));
        mockMvc.perform(get("/cases")).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Caso Aris"));
    }

    @Test
    void GET_cases_id_ShouldReturn200_WhenExists() throws Exception {
        when(caseService.getCaseById(1L)).thenReturn(testResponse);
        mockMvc.perform(get("/cases/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.title").value("Caso Aris"));
    }

    @Test
    void PUT_cases_id_ShouldReturn200_WhenUpdated() throws Exception {
        when(caseService.updateCase(anyLong(), any())).thenReturn(testResponse);
        mockMvc.perform(put("/cases/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Caso Aris"));
    }

    @Test
    void DELETE_cases_id_ShouldReturn204_WhenDeleted() throws Exception {
        mockMvc.perform(delete("/cases/1")).andExpect(status().isNoContent());
    }

    @Test
    void POST_cases_ShouldReturn400_WhenTitleIsNull() throws Exception {
        testRequest.setTitle(null);
        mockMvc.perform(post("/cases").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testRequest))).andExpect(status().isBadRequest());
    }
}