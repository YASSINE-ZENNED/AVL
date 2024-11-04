package com.avl.authentification.Models;

public record UserProfile(
        String username,
        String firstName,
        String lastName,
        String email
) {}
