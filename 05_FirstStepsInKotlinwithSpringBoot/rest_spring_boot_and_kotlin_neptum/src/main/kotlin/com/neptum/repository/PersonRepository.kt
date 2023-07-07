package com.neptum.repository

import com.neptum.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PersonRepository : JpaRepository<Person, Long?> {

    @Modifying
    @Query("UPDATE Person p SET p.enabled = false WHERE p.id = :id")
    fun disablePerson(@Param("id") id: Long?)
}