package com.neptum.controller

import com.neptum.data.vo.v1.PersonVO
import com.neptum.service.PersonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/person/v1")
class PersonController {

    @Autowired
    private lateinit var service: PersonService
    @GetMapping
    fun findAll(): List<PersonVO> {
        return service.findAll()
    }
    @GetMapping("/{id}")
    fun findById(@PathVariable(value = "id") id: Long ): PersonVO {
        return service.findById(id)
    }
    @PostMapping
    fun create(@RequestBody personVO : PersonVO): PersonVO {
        return service.create(personVO)
    }

    @PutMapping
    fun update(@RequestBody personVO: PersonVO) : PersonVO {
        return service.update(personVO)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(value = "id") id : Long) : ResponseEntity<*>{
        service.delete(id)
        return ResponseEntity.noContent().build<Any>()
    }
}