package com.neptum.service

import com.neptum.data.vo.v1.AccountCredentialsVO
import com.neptum.data.vo.v1.TokenVO
import com.neptum.repository.UserRepository
import com.neptum.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class AuthService {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var repository: UserRepository

    private val logger = Logger.getLogger(AuthService::class.java.name)

    fun signin(data: AccountCredentialsVO): ResponseEntity<*> {
        logger.info("Trying log user ${data.username}")
        return try {
            val username = data.username
            val password = data.password
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username,password))
            val user = repository.findByUserName(username)
            val tokenResponse: TokenVO = if(user != null) {
                tokenProvider.createAccessToken(username!!, user.roles)
            } else {
                throw UsernameNotFoundException("Username $username not found")
            }
            ResponseEntity.ok(tokenResponse)
        } catch (e: AuthenticationException ) {
            throw BadCredentialsException("Invalid username or password supplied")
        }
    }

    fun refreshToken(username: String, refreshToken: String): ResponseEntity<*> {
        logger.info("Trying get refreshtoken to  user $username")

        val user = repository.findByUserName(username)
        val tokenResponse: TokenVO = if(user != null) {
            tokenProvider.refreshToken(refreshToken)
        } else {
            throw UsernameNotFoundException("Username $username not found")
        }
        return ResponseEntity.ok(tokenResponse)
    }
}