package com.inn.webshop.com.inn.webshop.ServiceImpl.service;

import com.inn.webshop.com.inn.webshop.ServiceImpl.LoginDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<String> login(LoginDto dto);
}
