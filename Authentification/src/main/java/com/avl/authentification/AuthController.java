package com.avl.authentification;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/Client")
    @PreAuthorize("hasRole('client-user')")
    public String authC() {
        return "Hello World";
    }

    @GetMapping("/Admin")
    @PreAuthorize("hasRole('client-admin')")
    public String authA() {
        return "Hello World";
    }

}
