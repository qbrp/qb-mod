package org.qbrp.core.resources.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.GsonBuilder

abstract class YamlData: Data() {
    companion object {
        val mapper = ObjectMapper(YAMLFactory()).apply { registerKotlinModule() }
    }
}