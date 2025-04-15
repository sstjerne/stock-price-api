package com.sstjerne.stockapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
    "polygon.api.base-url=https://api.polygon.io"
})
class WebClientConfigTest {

    @Autowired
    private WebClient webClient;

    @Test
    void webClientBeanShouldBeCreated() {
        assertNotNull(webClient, "WebClient bean should be created");
    }
} 