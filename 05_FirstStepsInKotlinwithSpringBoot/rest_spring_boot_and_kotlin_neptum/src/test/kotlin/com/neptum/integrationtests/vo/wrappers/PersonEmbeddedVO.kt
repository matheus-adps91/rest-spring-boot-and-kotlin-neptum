package com.neptum.integrationtests.vo.wrappers

import com.fasterxml.jackson.annotation.JsonProperty
import com.neptum.integrationtests.vo.PersonVO

class PersonEmbeddedVO {

    @JsonProperty("personVOList")
    var persons: List<PersonVO>? = null
}