package com.neptum.service

import com.neptum.data.vo.v1.PersonVO
import com.neptum.exceptions.ResourceNotFoundException
import com.neptum.mapper.DozerMapper
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

    fun findAll() : List<PersonVO> {
        logger.info("Finding all people")
        val persons = personRepository.findAll()
        return DozerMapper.parseListObjects(persons, PersonVO::class.java)
    }

    fun findById(id: Long) : PersonVO {
        logger.info("Finding one person")
        val person = personRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records foudn for this ID") }
        return DozerMapper.parseObject(person, PersonVO::class.java)
    }

    fun create(personVO: PersonVO) : PersonVO {
        logger.info("Creating one person with name ${personVO.firstName}")
        val entity = DozerMapper.parseObject(personVO, Person::class.java)
        return DozerMapper.parseObject(personRepository.save(entity), PersonVO::class.java)
    }

    fun update(personVO: PersonVO): PersonVO {
        logger.info("Updating a person with id = ${personVO.id}")
        val entity = personRepository.findById(personVO.id)
            .orElseThrow { ResourceNotFoundException("No records foudn for this ID") }

        entity.firstName = personVO.firstName
        entity.lastName = personVO.lastName
        entity.address = personVO.address
        entity.gender = personVO.gender
        return DozerMapper.parseObject(personRepository.save(entity), personVO::class.java)
    }


    fun delete(id: Long) {
        logger.info("Deleting a person with id = $id")
        val person = personRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("No records foudn for this ID") }
        personRepository.delete(person)
    }
}