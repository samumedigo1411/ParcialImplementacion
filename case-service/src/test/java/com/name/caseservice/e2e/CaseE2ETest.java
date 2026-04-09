package com.name.caseservice.e2e;

import com.name.caseservice.dto.CaseRequestDTO;
import com.name.caseservice.controller.dto.CaseResponseDTO;
import com.name.caseservice.model.CasePriority;
import com.name.caseservice.repository.CaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CaseE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CaseRepository caseRepository;
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    @BeforeEach
    void setUp() {
        caseRepository.deleteAll();
    }
    // Desactivar la seguriad para test
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        @Order(1)
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
    private CaseRequestDTO buildRequest() {
        CaseRequestDTO dto = new CaseRequestDTO();
        dto.setTitle("Caso E2E");
        dto.setDescription("Descripción E2E");
        dto.setPriority(CasePriority.High);
        dto.setAssignedDetective("Luis R");
        return dto;
    }
    @Test
    void fullFlow_CreateAndGetCase() {
        //  Crear
        ResponseEntity<CaseResponseDTO> createResponse = restTemplate.postForEntity("/cases", buildRequest(), CaseResponseDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Long id = createResponse.getBody().getId();

        //  Consultar
        ResponseEntity<CaseResponseDTO> getResponse = restTemplate.getForEntity("/cases/" + id, CaseResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Caso E2E");
    }

    @Test
    void fullFlow_CreateUpdateAndDelete() {
        // Crear
        CaseResponseDTO created = restTemplate.postForEntity("/cases", buildRequest(), CaseResponseDTO.class).getBody();

        // Actualizar
        CaseRequestDTO update = buildRequest();
        update.setTitle("Caso Actualizado");
        ResponseEntity<CaseResponseDTO> updateResponse = restTemplate.exchange("/cases/" + created.getId(),  HttpMethod.PUT, new HttpEntity<>(update), CaseResponseDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getTitle()).isEqualTo("Caso Actualizado");

        // Eliminar
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/cases/" + created.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verificar eliminacion
        ResponseEntity<String> getResponse = restTemplate.getForEntity("/cases/" + created.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllCases_ShouldReturnList() {
        restTemplate.postForEntity("/cases", buildRequest(), CaseResponseDTO.class);
        ResponseEntity<CaseResponseDTO[]> response = restTemplate.getForEntity("/cases", CaseResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void createCase_Invalid_ShouldReturn400() {
        CaseRequestDTO invalid = new CaseRequestDTO();
        ResponseEntity<String> response = restTemplate.postForEntity("/cases", invalid, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}