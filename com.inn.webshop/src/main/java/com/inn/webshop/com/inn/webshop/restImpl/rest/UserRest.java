package com.inn.webshop.com.inn.webshop.restImpl.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/auth")
public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signup(@RequestBody(required = true)Map<String, String> requestMap);

    @PostMapping(path = "login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);
}
