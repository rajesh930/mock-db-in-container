package com.ontic.module;

import com.ontic.framework.db.es.ESService;
import com.ontic.framework.db.mongo.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author rajesh
 * @since 02/03/25 14:39
 */
@Controller
public class HelloWorldController {

    private static final String template = "Hello, %s %d!";
    private final AtomicLong counter = new AtomicLong();
    private final MongoService mongoService;
    private final ESService esService;

    @Autowired
    public HelloWorldController(MongoService mongoService, ESService esService) {
        this.mongoService = mongoService;
        this.esService = esService;
    }

    @GetMapping("/hello-world")
    @ResponseBody
    public HelloResponse sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
        long id = counter.incrementAndGet();
        HelloResponse helloResponse = new HelloResponse().withId("req_" + id).withName(String.format(template, name, id));
        mongoService.save("hellodb", "hello", helloResponse);
        esService.index("hello", helloResponse.getId(), helloResponse);
        return helloResponse;
    }

    public static class HelloResponse {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HelloResponse withId(String id) {
            this.id = id;
            return this;
        }

        public HelloResponse withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HelloResponse that = (HelloResponse) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }
}