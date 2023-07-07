package com.neptum.repository

import com.neptum.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User?, Long?> {

    //@Query("SELECT u FROM User u WHERE u.userName = userName")
    //@Query("SELECT * FROM users WHERE users.user_name = userName", nativeQuery = true)
    fun findByUserName(@Param("userName") userName: String?): User?
}