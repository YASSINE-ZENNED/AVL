package com.avl.authentification.Models;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// A configuration class that defines a RestTemplate bean
@Configuration
public class RestTemplateConfig {

    // A method that returns a RestTemplate object
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
