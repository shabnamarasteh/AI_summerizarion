package com.project.ai.model.service;

import com.project.ai.model.entity.Person;
import com.project.ai.model.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findById(long id) {
        if(this.personRepository.existsById(id))
            return this.personRepository.getOne(id);
        else
            return null;
    }

    public Person save(Person person) {
        return this.personRepository.save(person);
    }

    public List<Person> findAll() {
        return this.personRepository.findAll();
    }
}
