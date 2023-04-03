package com.example.demo2022.rest;

import com.example.demo2022.domain.User;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient {

    public static void main(String[] args) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        HttpClient httpClient = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);

//        String content = restTemplate.getForObject("http://localhost:8888/json/user", String.class);

//        User user = restTemplate.getForObject("http://localhost:8888/json/user", User.class);

        User user = restTemplate.getForObject("http://localhost:8888/xml/user", User.class);

        System.out.println(user);

    }
}
