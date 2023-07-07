package com.neptum.integrationtests.controller.withyml

import com.neptum.integrationtests.TestConfigs
import com.neptum.integrationtests.controller.withyml.mapper.YMLMapper
import com.neptum.integrationtests.testcontainers.AbstractIntegrationTest
import com.neptum.integrationtests.vo.AccountCredentialsVO
import com.neptum.integrationtests.vo.TokenVO
import io.restassured.RestAssured.given
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerYmlTest: AbstractIntegrationTest() {

    private lateinit var tokenVo: TokenVO
    private lateinit var objetMapper: YMLMapper

    @BeforeAll
    fun setUp() {
        tokenVo = TokenVO()
        objetMapper = YMLMapper()
    }

    @Test
    @Order(0)
    fun testLogin() {
        val user = AccountCredentialsVO(
            username = "leandro",
            password = "admin123"
        )

        tokenVo = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .basePath("/auth/signin")
            .port(TestConfigs.SERVER_PORT)
            .accept(TestConfigs.CONTENT_TYPE_YML)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(user, objetMapper)
            .`when`()
            .post()
            .then()
            .statusCode(200)
                .extract()
                .body()
            .`as`(TokenVO::class.java, objetMapper)

        assertNotNull(tokenVo.accessToken)
        assertNotNull(tokenVo.refreshToken)
    }

    @Test
    @Order(1)
    fun tokenRefreshToken() {

        tokenVo = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .basePath("/auth/refresh")
            .port(TestConfigs.SERVER_PORT)
            .accept(TestConfigs.CONTENT_TYPE_YML)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
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
                        .`as`(TokenVO::class.java, objetMapper)

        assertNotNull(tokenVo.accessToken)
        assertNotNull(tokenVo.refreshToken)
    }
}