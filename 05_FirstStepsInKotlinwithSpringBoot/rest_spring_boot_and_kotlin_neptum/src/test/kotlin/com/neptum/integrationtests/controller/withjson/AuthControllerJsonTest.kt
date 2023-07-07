package com.neptum.integrationtests.controller.withjson

import com.neptum.integrationtests.TestConfigs
import com.neptum.integrationtests.testcontainers.AbstractIntegrationTest
import com.neptum.integrationtests.vo.AccountCredentialsVO
import com.neptum.integrationtests.vo.TokenVO
import io.restassured.RestAssured.given
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerJsonTest: AbstractIntegrationTest() {

    private lateinit var tokenVo: TokenVO

    @BeforeAll
    fun setUp() {
        tokenVo = TokenVO()
    }

    @Test
    @Order(0)
    fun testLogin() {
        val user = AccountCredentialsVO(
            username = "leandro",
            password = "admin123"
        )

        tokenVo = given()
            .basePath("/auth/signin")
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .body(user)
            .`when`()
            .post()
            .then()
            .statusCode(200)
                .extract()
                .body()
            .`as`(TokenVO::class.java)

        assertNotNull(tokenVo.accessToken)
        assertNotNull(tokenVo.refreshToken)
    }

    @Test
    @Order(1)
    fun tokenRefreshToken() {

        tokenVo = given()
            .basePath("/auth/refresh")
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .pathParam("username", tokenVo.username)
            .header(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer ${tokenVo.refreshToken}"
            )
            .`when`()
                .put("{username}")
                    .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .`as`(TokenVO::class.java)

        assertNotNull(tokenVo.accessToken)
        assertNotNull(tokenVo.refreshToken)
    }
}