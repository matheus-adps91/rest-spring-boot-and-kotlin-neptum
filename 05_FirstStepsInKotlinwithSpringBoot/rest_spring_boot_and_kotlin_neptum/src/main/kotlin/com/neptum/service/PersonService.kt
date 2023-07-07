package com.neptum.service

import com.neptum.controller.PersonController
import com.neptum.data.vo.v1.PersonVO
import com.neptum.exceptions.RequiredObjectIsNullException
import com.neptum.exceptions.ResourceNotFoundException
import com.neptum.mapper.DozerMapper
import com.neptum.model.Person
import com.neptum.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class PersonService {

    @Autowired
    private lateinit var personRepository: PersonRepository

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll() : List<PersonVO> {
        logger.info("Finding all people")
        val persons = personRepository.findAll()
        val personVOs = DozerMapper.parseListObjects(persons, PersonVO::class.java)
        for (person in personVOs){
            val withSelfRel = linkTo(PersonController::class.java).slash(person).withSelfRel()
            person.add(withSelfRel)
        }
        return personVOs
    }

    fun findById(id: Long) : PersonVO {
        logger.info("Finding one person with ID $id!")
        val person = personRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records found for this ID") }
        val personVO = DozerMapper.parseObject(person, PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    fun create(personVO: PersonVO?) : PersonVO {
        if (personVO == null) throw RequiredObjectIsNullException()
        logger.info("Creating one person with name ${personVO.firstName}")
        val entity = DozerMapper.parseObject(personVO, Person::class.java)
        val personVO = DozerMapper.parseObject(personRepository.save(entity), PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    fun update(personVO: PersonVO?): PersonVO {
        if (personVO == null) throw RequiredObjectIsNullException()
        logger.info("Updating a person with id = ${personVO.key}")
        val entity = personRepository.findById(personVO.key)
            .orElseThrow { ResourceNotFoundException("No records found for this ID") }

        entity.firstName = personVO.firstName
        entity.lastName = personVO.lastName
        entity.address = personVO.address
        entity.gender = personVO.gender
        val personVO = DozerMapper.parseObject(personRepository.save(entity), personVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }


    fun delete(id: Long) {
        logger.info("Deleting a person with id = $id")
        val person = personRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records found for this ID") }
        personRepository.delete(person)
    }
}