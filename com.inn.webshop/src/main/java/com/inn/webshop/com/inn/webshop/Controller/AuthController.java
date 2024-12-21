package com.inn.webshop.com.inn.webshop.Controller;

import com.inn.webshop.com.inn.webshop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    AuthService service;


    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@RequestBody Map<String, String> requestMap) {
        return service.signUp(requestMap);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestMap) {
        return service.login(requestMap);
    }




}
