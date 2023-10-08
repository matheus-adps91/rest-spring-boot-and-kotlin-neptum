package com.neptum.repository

import com.neptum.model.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface PersonRepository : JpaRepository<Person, Long?> {

    @Query("SELECT p FROM Person p WHERE p.firstName LIKE LOWER(CONCAT('%',:firstName,'%'))")
    fun findPersonByName(@Param("firstName") firstName: String, pageable: Pageable): Page<Person>
}