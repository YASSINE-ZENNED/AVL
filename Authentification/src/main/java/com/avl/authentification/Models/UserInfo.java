package com.avl.authentification.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("preferred_username")
    private String preferred_username;

    @JsonProperty("family_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    // getters and setters omitted for brevity
}
