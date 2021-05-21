package io.amtech.projectflow.test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

public class TestUtils {

    public static MockHttpServletRequestBuilder createGet(String url) {
        return MockMvcRequestBuilders.get(url, new Object[0]).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name());
    }

    public static MockHttpServletRequestBuilder createPost(String url) {
        return MockMvcRequestBuilders.post(url, new Object[0]).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name());
    }

    public static MockHttpServletRequestBuilder createPut(String url) {
        return MockMvcRequestBuilders.put(url, new Object[0]).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name());
    }

    public static MockHttpServletRequestBuilder createDelete(String url) {
        return MockMvcRequestBuilders.delete(url, new Object[0]).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name());
    }

    private TestUtils() {
    }
}
