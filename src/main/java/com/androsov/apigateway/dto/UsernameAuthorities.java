package com.androsov.apigateway.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernameAuthorities {
    public Long id;
    public String username;
    public String authorities;

}
