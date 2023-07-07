package com.neptum.integrationtests.controller.withyml

import com.neptum.integrationtests.TestConfigs
import com.neptum.integrationtests.controller.withyml.mapper.YMLMapper
import com.neptum.integrationtests.testcontainers.AbstractIntegrationTest
import com.neptum.integrationtests.vo.AccountCredentialsVO
import com.neptum.integrationtests.vo.PersonVO
import com.neptum.integrationtests.vo.TokenVO
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonControllerYmlTest: AbstractIntegrationTest() {

    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: YMLMapper
    private lateinit var person: PersonVO

    @BeforeAll
    fun setUpTests() {
        objectMapper = YMLMapper()
        person = PersonVO()
    }

    @Test
    @Order(0)
    fun testLogin() {
        val user = AccountCredentialsVO(
            username = "leandro",
            password = "admin123"
        )

        val token = given()
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
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(user, objectMapper)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenVO::class.java, objectMapper)
            .accessToken

        specification = RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer $token")
                .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
                .addFilter(RequestLoggingFilter(LogDetail.ALL))
                .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()
    }

    @Test
    @Order(1)
    fun testCreate() {
        mockPerson()

        val item = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(person, objectMapper)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(PersonVO::class.java, objectMapper)

        person = item

        assertNotNull(item.id)
        assertTrue(item.id > 0)
        assertNotNull(item.firstName)
        assertNotNull(item.lastName)
        assertNotNull(item.address)
        assertNotNull(item.gender)
        assertEquals("Richard", item.firstName)
        assertEquals("Stallman", item.lastName)
        assertEquals("New York City, New York, US", item.address)
        assertEquals("Male", item.gender)
    }

    @Test
    @Order(2)
    fun testUpdate() {
        person.lastName = "Matthew Stalman"

        val item = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(person, objectMapper)
            .`when`()
            .put()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(PersonVO::class.java, objectMapper)

        person = item

        assertNotNull(item.id)
        assertNotNull(item.firstName)
        assertNotNull(item.lastName)
        assertNotNull(item.address)
        assertNotNull(item.gender)
        assertEquals(person.id, item.id)
        assertEquals("Richard", item.firstName)
        assertEquals("Matthew Stalman", item.lastName)
        assertEquals("New York City, New York, US", item.address)
        assertEquals("Male", item.gender)
    }

    @Test
    @Order(3)
    fun testFindById() {
        val item = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParam("id", person.id)
            .`when`()
            .get("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(PersonVO::class.java, objectMapper)

        person = item

        assertNotNull(item.id)
        assertNotNull(item.firstName)
        assertNotNull(item.lastName)
        assertNotNull(item.address)
        assertNotNull(item.gender)
        assertEquals(person.id, item.id)
        assertEquals("Richard", item.firstName)
        assertEquals("Matthew Stalman", item.lastName)
        assertEquals("New York City, New York, US", item.address)
        assertEquals("Male", item.gender)
    }

    @Test
    @Order(4)
    fun testDelete() {
        given()
            .spec(specification)
            .pathParam("id", person.id)
            .`when`()
            .delete("{id}")
            .then()
            .statusCode(204)
    }

    @Test
    @Order(5)
    fun testFindAll() {
        val people = given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(Array<PersonVO>::class.java, objectMapper)

        val item = people[0]

        assertNotNull(item.id)
        assertNotNull(item.firstName)
        assertNotNull(item.lastName)
        assertNotNull(item.address)
        assertNotNull(item.gender)
        assertEquals("Leonardo", item.firstName)
        assertEquals("Da Vinci", item.lastName)
        assertEquals("Anchiano - Italy", item.address)
        assertEquals("Male", item.gender)

        val item2 = people[3]

        assertNotNull(item2.id)
        assertNotNull(item2.firstName)
        assertNotNull(item2.lastName)
        assertNotNull(item2.address)
        assertNotNull(item2.gender)
        assertEquals("Indira", item2.firstName)
        assertEquals("Gandhi", item2.lastName)
        assertEquals("Porbandar - India", item2.address)
        assertEquals("Female", item2.gender)
    }

    private fun mockPerson() {
        person.firstName = "Richard"
        person.lastName = "Stallman"
        person.address = "New York City, New York, US"
        person.gender = "Male"
    }
}