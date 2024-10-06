package com.avl.authentification;

import com.avl.authentification.Models.UserInfo;
import com.avl.authentification.Models.UserLoginRecord;
import com.avl.authentification.Models.UserRegistrationRecord;
import com.avl.authentification.Models.userLoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.keycloak.admin.client.resource.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class KeycloakUserSerivceImpl implements KeycloakUserService {

    private static final String BASE_URL = "http://localhost:8088/realms/AVL/protocol/openid-connect/token";
    private static final String BASE_URL1 = "http://localhost:8088/realms/AVL/protocol/openid-connect/token/introspect";


    @Autowired
    private RestTemplate restTemplate;

    private  Keycloak keycloak;

//    private String keycloakRealm;

    // The RestTemplate object to make HTTP requests

    public UserInfo getUserInfo(String token) throws JsonProcessingException {

        // Create a map of request parameters

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.put("token", Collections.singletonList(token.toString()));

        params.put("client_secret", Collections.singletonList("PkVyO0vifkCNEQXGeDTFtc9WTvoP5Zwi"));

        params.put("client_id", Collections.singletonList("AVL"));

        // Create a HttpEntity object with the parameters and headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Object> response = restTemplate.exchange(BASE_URL1, HttpMethod.POST, request, Object.class);

        // Call the API using the exchange method and get the response as a Ticket object
        // Get the userLoginResponse object from the response
        String json = mapper.writeValueAsString(response.getBody());

        log.info("response: {}", json);

//        UserInfo user = new UserInfo();

        UserInfo user = mapper.readValue(json, UserInfo.class);

//        user.setUserName(json.g);
        log.info("userLogin: {}", user.getName());
        log.info("userLogin: {}", user.getLastName());
        log.info("userLogin: {}", user.getGivenName());
        log.info("userLogin: {}", user.getEmail());
//        user.setUserName(json.get("userName").toString());
// Get the token and refresh token from the userLoginResponse object
//        String username = userLogin.getUserName();
//        String refreshToken = userLogin.getRefresh_token();

// Display the token and refresh token
//        System.out.println("Token: " + token);
//        System.out.println("Refresh Token: " + refreshToken);
        return  user;
    }

    // A method that takes a username and password and returns a Ticket object
    public Object getUserTokens(UserLoginRecord userLoginRecord) {

        // Create a map of request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("username", Collections.singletonList(userLoginRecord.username()));
        params.put("password", Collections.singletonList(userLoginRecord.password()));
        params.put("client_secret", Collections.singletonList("oB9cpN0oAvIsZcbiQEZUeRcv1ZDUIjKP"));
        params.put("grant_type", Collections.singletonList("password"));
        params.put("client_id", Collections.singletonList("Ticket-App"));
        // Create a HttpEntity object with the parameters and headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<userLoginResponse> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, request, userLoginResponse.class);

        // Call the API using the exchange method and get the response as a Ticket object
        // Get the userLoginResponse object from the response
        userLoginResponse userLogin = response.getBody();

// Get the token and refresh token from the userLoginResponse object
        String token = userLogin.getAccess_token();
        String refreshToken = userLogin.getRefresh_token();

// Display the token and refresh token
        System.out.println("Token: " + token);

        System.out.println("Refresh Token: " + refreshToken);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        String jsonString = jsonObject.toString();
        System.out.println("JSON String: " + jsonString);

        return  jsonObject;
    }

    @Override
    public ResponseEntity<String> createUser(UserRegistrationRecord userRegistrationRecord) {

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegistrationRecord.password());

        List<CredentialRepresentation> credentials = new ArrayList<>();
        credentials.add(credential);
        user.setCredentials(credentials);

        System.out.println("credentials = " + credentials);

        UsersResource userResource = getUsersResource();

        System.out.println("user = " + user);

        Response response = userResource.create(user);

        log.info("response: {}",response.getStatus());

        if(Objects.equals(201,response.getStatus())){
            log.info(" login works but not verfy email");

            List<UserRepresentation> representationList = userResource.searchByUsername(userRegistrationRecord.username(), true);

            UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);

            emailVerification(userRepresentation1.getId());

            return   ResponseEntity.status(HttpStatus.OK).body("user have been created and email sent to verify email address");
        }else{
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not created");

        }

    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm("AVL");
        return realm1.users();
    }


    @Override
    public UserRepresentation getUserById(String userId) {
//        return  getUsersResource().get(userId).toRepresentation();
        return  getUsersResource().get(userId).toRepresentation();
    }

    @Override
    public void deleteUserById(String userId) {
        Response response =  getUsersResource().delete(userId);

        log.info("response: {}",response.getStatus());

        if(Objects.equals(201,response.getStatus())){

            ResponseEntity.status(HttpStatus.OK).body("user have been deleted");
        }else{
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not deleted");

        }

    }



    public void emailVerification(String userId){

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }
//   public void userijnf(String userId){
//
//        UsersResource usersResource = getUsersResource();
//        usersResource.get(userId).
//    }

    public UserResource getUserResource(String userId){
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

//    @Override
//    public void updatePassword(String userId) {
//
//        UserResource userResource = getUserResource(userId);
//        List<String> actions= new ArrayList<>();
//        actions.add("UPDATE_PASSWORD");
//        userResource.executeActionsEmail(actions);
//
//    }



}