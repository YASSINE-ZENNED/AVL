package com.avl.authentification.Models;


import lombok.Getter;

public class userLoginResponse {
    @Getter
    private String access_token;
    private String token_type;
    private int expires_in;
    @Getter
    private String refresh_token;
    private String scope;


}
