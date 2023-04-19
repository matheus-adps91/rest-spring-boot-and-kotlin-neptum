package com.neptum.service

import com.neptum.exceptions.ResourceNotFoundException
import com.neptum.model.Person
import com.neptum.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class PersonService {

    @Autowired
    private lateinit var personRepository: PersonRepository

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll() : List<Person> {
        logger.info("Finding all people")
        return personRepository.findAll()
    }

    fun findById(id: Long) : Person {
        logger.info("Finding one person")
        return personRepository.findById(id)
            .orElseThrow {ResourceNotFoundException("No records foudn for this ID")}
    }

    fun create(person: Person) : Person {
        logger.info("Creating one person with name ${person.firstName}")
        return personRepository.save(person)
    }

    fun update(person: Person): Person {
        logger.info("Updating a person with id = ${person.id}")
        val entity = personRepository.findById(person.id)
            .orElseThrow { ResourceNotFoundException("No records foudn for this ID") }

        entity.firstName = person.firstName
        entity.lastName = person.lastName
        entity.address = person.address
        entity.gender = person.gender
        return personRepository.save(entity)
    }


    fun delete(id: Long) {
        logger.info("Deleting a person with id = ${id}")
        val person = personRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records foudn for this ID") }
        personRepository.delete(person)
    }
}