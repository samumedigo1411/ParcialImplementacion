package com.name.peopleservice.repository;

import com.name.peopleservice.model.Person;
import com.name.peopleservice.model.PersonRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Todas las personas de un caso
    List<Person> findByCaseId(Long caseId);

    // Personas de un caso filtradas por rol
    List<Person> findByCaseIdAndRole(Long caseId, PersonRole role);

    // Personas por rol
    List<Person> findByRole(PersonRole role);
}