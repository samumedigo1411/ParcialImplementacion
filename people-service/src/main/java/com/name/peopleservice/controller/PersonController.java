package com.name.peopleservice.controller;

import com.name.peopleservice.dto.PersonRequestDTO;
import com.name.peopleservice.dto.PersonResponseDTO;
import com.name.peopleservice.model.PersonRole;
import com.name.peopleservice.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonResponseDTO> createPerson(
            @Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personService.createPerson(request));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponseDTO>> getAllPeople() {
        return ResponseEntity.ok(personService.getAllPeople());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<PersonResponseDTO>> getPeopleByCaseId(
            @PathVariable Long caseId) {
        return ResponseEntity.ok(personService.getPeopleByCaseId(caseId));
    }

    @GetMapping("/case/{caseId}/role/{role}")
    public ResponseEntity<List<PersonResponseDTO>> getPeopleByCaseIdAndRole(
            @PathVariable Long caseId,
            @PathVariable PersonRole role) {
        return ResponseEntity.ok(personService.getPeopleByCaseIdAndRole(caseId, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonRequestDTO request) {
        return ResponseEntity.ok(personService.updatePerson(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}