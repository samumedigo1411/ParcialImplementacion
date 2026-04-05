package com.name.peopleservice.service;

import com.name.peopleservice.config.KafkaConfig;
import com.name.peopleservice.dto.PersonRequestDTO;
import com.name.peopleservice.dto.PersonResponseDTO;
import com.name.peopleservice.exception.PersonNotFoundException;
import com.name.peopleservice.model.Person;
import com.name.peopleservice.model.PersonRole;
import com.name.peopleservice.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PersonResponseDTO createPerson(PersonRequestDTO request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setRole(request.getRole());
        person.setCaseId(request.getCaseId());
        person.setDescription(request.getDescription());

        Person saved = personRepository.save(person);

        String eventJson = String.format(
                "{\"event\":\"PersonAdded\",\"personId\":%d,\"caseId\":%d,\"role\":\"%s\"}",
                saved.getId(), saved.getCaseId(), saved.getRole()
        );
        kafkaTemplate.send(KafkaConfig.PERSON_ADDED_TOPIC,
                String.valueOf(saved.getId()), eventJson);
        log.info("Evento PersonAdded publicado: {}", eventJson);

        return toResponse(saved);
    }

    public List<PersonResponseDTO> getAllPeople() {
        return personRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PersonResponseDTO getPersonById(Long id) {
        Person found = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Persona no encontrada con id: " + id));
        return toResponse(found);
    }

    public List<PersonResponseDTO> getPeopleByCaseId(Long caseId) {
        return personRepository.findByCaseId(caseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PersonResponseDTO> getPeopleByCaseIdAndRole(Long caseId, PersonRole role) {
        return personRepository.findByCaseIdAndRole(caseId, role)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PersonResponseDTO updatePerson(Long id, PersonRequestDTO request) {
        Person found = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Persona no encontrada con id: " + id));

        found.setFirstName(request.getFirstName());
        found.setLastName(request.getLastName());
        found.setEmail(request.getEmail());
        found.setPhone(request.getPhone());
        found.setRole(request.getRole());
        found.setCaseId(request.getCaseId());
        found.setDescription(request.getDescription());

        Person updated = personRepository.save(found);
        return toResponse(updated);
    }

    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException("Persona no encontrada con id: " + id);
        }
        personRepository.deleteById(id);
    }

    private PersonResponseDTO toResponse(Person p) {
        PersonResponseDTO response = new PersonResponseDTO();
        response.setId(p.getId());
        response.setFirstName(p.getFirstName());
        response.setLastName(p.getLastName());
        response.setEmail(p.getEmail());
        response.setPhone(p.getPhone());
        response.setRole(p.getRole());
        response.setCaseId(p.getCaseId());
        response.setDescription(p.getDescription());
        response.setCreatedAt(p.getCreatedAt());
        response.setUpdatedAt(p.getUpdatedAt());
        return response;
    }
}