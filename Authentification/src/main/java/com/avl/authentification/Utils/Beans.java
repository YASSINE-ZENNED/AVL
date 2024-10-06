package com.avl.authentification.Utils;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Value("${keycloak.urls.auth}")
    private String keycloakDomain;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.adminClientId}")
    private String keycloakClientId;

    @Value("${keycloak.adminClientSecret}")
    private String keycloakClientSecret;

    @Value("${keycloak.client}")
    private String keycloakClient;

    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(keycloakDomain)
                .realm(keycloakRealm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakClientId)
                .clientSecret(keycloakClientSecret)
                .build();
    }
}
