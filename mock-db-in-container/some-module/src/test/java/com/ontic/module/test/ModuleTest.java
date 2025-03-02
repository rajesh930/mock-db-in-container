package com.ontic.module.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontic.framework.db.es.ESService;
import com.ontic.framework.db.mongo.MongoService;
import com.ontic.module.HelloWorldController.HelloResponse;
import com.ontic.test.base.BaseTestFramework;
import com.ontic.test.base.RequireES;
import com.ontic.test.base.RequireMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author rajesh
 * @since 02/03/25 13:48
 */
@RequireMongo
@RequireES
@SpringBootTest
@AutoConfigureMockMvc
public class ModuleTest extends BaseTestFramework {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoService mongoService;
    @Autowired
    private ESService esService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturnDefaultMessageAndRetRecordedInDb() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/hello-world").param("name", "World")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World"))).andReturn();

        String json = result.getResponse().getContentAsString();
        HelloResponse response = objectMapper.readValue(json, HelloResponse.class);
        HelloResponse mongoResponse = mongoService.get("hellodb", "hello", response.getId(), HelloResponse.class);
        HelloResponse esResponse = esService.read("hello", response.getId(), HelloResponse.class);
        Assertions.assertEquals(response, mongoResponse);
        Assertions.assertEquals(response, esResponse);
    }
}
