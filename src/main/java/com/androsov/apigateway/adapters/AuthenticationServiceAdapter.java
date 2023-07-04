package com.androsov.apigateway.adapters;

import com.androsov.apigateway.dto.JwtRequest;
import com.androsov.apigateway.dto.UsernameAuthorities;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationServiceAdapter {
    @Autowired
    private EurekaClient eurekaClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validate(String jwt) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("AUTHENTICATION-SERVICE", false);
        String url = instanceInfo.getHomePageUrl() + "validate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JwtRequest> requestEntity = new HttpEntity<>(new JwtRequest(jwt), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST,
                requestEntity,
                String.class);

        String response = responseEntity.getBody();

        if (response == null) {
            throw new RuntimeException("Null response from authentication-service when trying to validate jwt");
        }

        return response.equals("ok");
    }

    public UsernameAuthorities parse(String jwt) {
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("AUTHENTICATION-SERVICE", false);
        String url = instanceInfo.getHomePageUrl() + "parse";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JwtRequest> requestEntity = new HttpEntity<>(new JwtRequest(jwt), headers);

        ResponseEntity<UsernameAuthorities> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST,
                requestEntity,
                UsernameAuthorities.class);

        UsernameAuthorities response = responseEntity.getBody();

        if (response == null) {
            throw new RuntimeException("Null response from authentication-service when trying to validate jwt");
        }

        return response;
    }


}
