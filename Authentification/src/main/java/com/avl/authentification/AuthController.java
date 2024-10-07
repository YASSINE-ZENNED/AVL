package com.avl.authentification;

import com.avl.authentification.Models.UserInfo;
import com.avl.authentification.Models.UserLoginRecord;
import com.avl.authentification.Models.UserRegistrationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private  KeycloakUserSerivceImpl keycloakUserService1;


//    @GetMapping("/Client")
//    @PreAuthorize("hasRole('client-user')")
//    public String authC() {
//        return "Hello World";
//    }
//
//    @GetMapping("/Admin")
//    @PreAuthorize("hasRole('client-admin')")
//    public String authA() {
//        return "Hello World";
//    }

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginRecord userLoginRecord) {
        log.info("userLoginRecord: {}", userLoginRecord.toString() );
        return        keycloakUserService1.getUserTokens(userLoginRecord);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return  keycloakUserService1.createUser(userRegistrationRecord);
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

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public String deleteById( @PathVariable String userId) {
        keycloakUserService1.deleteUserById(userId);

        return " user deleted";

    }

}
