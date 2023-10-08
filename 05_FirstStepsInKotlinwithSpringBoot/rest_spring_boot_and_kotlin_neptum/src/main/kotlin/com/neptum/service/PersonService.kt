package com.neptum.service

import com.neptum.controller.PersonController
import com.neptum.data.vo.v1.PersonVO
import com.neptum.exceptions.RequiredObjectIsNullException
import com.neptum.exceptions.ResourceNotFoundException
import com.neptum.mapper.DozerMapper
import com.neptum.model.Person
import com.neptum.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class PersonService {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var assembler: PagedResourcesAssembler<PersonVO>

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll(pageable: PageRequest): PagedModel<EntityModel<PersonVO>> {

        logger.info("Finding all people")

        val persons = personRepository.findAll(pageable)
        val vos = persons.map { p -> DozerMapper.parseObject(p, PersonVO::class.java) }
        vos.map { p -> p.add(linkTo(PersonController::class.java).slash(p.key).withSelfRel()) }
        return assembler.toModel(vos)
    }

    fun findPersonByName(firstName: String, pageable: Pageable): PagedModel<EntityModel<PersonVO>> {

        logger.info("Finding all people")

        val persons = personRepository.findPersonByName(firstName, pageable)
        val vos = persons.map { p -> DozerMapper.parseObject(p, PersonVO::class.java) }
        vos.map { p -> p.add(linkTo(PersonController::class.java).slash(p.key).withSelfRel()) }
        return assembler.toModel(vos)
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