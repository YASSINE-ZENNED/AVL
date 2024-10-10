package com.avl.authentification;

import com.avl.authentification.Models.UserInfo;
import com.avl.authentification.Models.UserLoginRecord;
import com.avl.authentification.Models.UserRegistrationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")

public class AuthController {

    @Autowired
    private KeycloakUserSerivceImpl keycloakUserService1;


    @GetMapping("/test")
    public String authC() {
        return "Hello World";
    }

    //    @GetMapping("/Admin")
//    @PreAuthorize("hasRole('client-admin')")
//    public String authA() {
//        return "Hello World";
//    }
    @CrossOrigin(origins = "*")

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginRecord userLoginRecord) {

        return keycloakUserService1.getUserTokens(userLoginRecord);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakUserService1.createUser(userRegistrationRecord);
    }


    @PostMapping("/userinfo")
    public String getUserInfo(@RequestBody String accessToken) {


        return keycloakUserService1.getUser(accessToken);
    }

    @GetMapping("/getUser")
    public UserInfo getUser(@RequestBody String token) throws JsonProcessingException {

        log.info("token :{}", token);
        return keycloakUserService1.getUserInfo(token);

    }

    @GetMapping("/getUsers")
    public Object getUsers(@RequestBody String token) {

        token = token.replace("token=", "");

        return keycloakUserService1.getUsers(token);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String userId = body.get("userId");
        if (token != null && userId != null) {
            token = token.replace("token:", "");
            userId = userId.replace("userId:", "");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token or userID is missing");
        }

        try {
            // Assuming keycloakUserService1.deleteUserById(user, token) exists and works as intended
            keycloakUserService1.deleteUserById(userId, token);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            // Handle exceptions appropriately (e.g., log the error, return a specific error message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete user: " + e.getMessage());
        }
    }

}
