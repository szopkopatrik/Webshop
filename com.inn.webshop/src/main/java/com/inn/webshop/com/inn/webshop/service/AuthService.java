package com.inn.webshop.com.inn.webshop.service;

import com.inn.webshop.com.inn.webshop.constents.Constants;
import com.inn.webshop.com.inn.webshop.data.entity.RoleEntity;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import com.inn.webshop.com.inn.webshop.data.repository.RoleRepository;
import com.inn.webshop.com.inn.webshop.data.repository.UserRepository;
import com.inn.webshop.com.inn.webshop.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    @Lazy
    PasswordEncoder encoder;
    @Autowired
    @Lazy
    AuthenticationManager manager;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtService jwtService;


    // Sign-up method
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            if (validateSignUp(requestMap)) {
                UserEntity userEntity = userRepository.findByEmailAddress(requestMap.get("email"));
                if (Objects.isNull(userEntity)) {
                    // Save user to database
                    UserEntity savedUser = userRepository.save(getUserFromMap(requestMap));

                    // Generate a JWT token for the user
                    String token = jwtService.generateToken(savedUser, savedUser.getId(), savedUser.getRole());

                    // Return success response with the token
                    return Utils.getResponseEntity("Successfully Registered. Token: " + token, HttpStatus.OK);
                } else {
                    return Utils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            String email = requestMap.get("email");
            String password = requestMap.get("password");
            System.out.println("Attempting login for email: " + email);

            manager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            UserEntity user = userRepository.findByEmailAddress(email);
            if (user == null) {
                System.out.println("User not found for email: " + email);
                return Utils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
            }

            if (!user.isAccountNonLocked()) {
                System.out.println("User account is locked: " + email);
                return Utils.getResponseEntity("Account is locked", HttpStatus.FORBIDDEN);
            }

            String token = jwtService.generateToken(user, user.getId(), user.getRole());
            System.out.println("Generated token for user: " + email);

            return Utils.getResponseEntity("Login successful. Token: " + token, HttpStatus.OK);

        } catch (BadCredentialsException ex) {
            System.out.println("Invalid credentials for email: " + requestMap.get("email"));
            return Utils.getResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            System.out.println("Unexpected error during login: " + ex.getMessage());
            return Utils.getResponseEntity("Something went wrong. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password");
    }


    private UserEntity getUserFromMap(Map<String, String> requestMap) {
        RoleEntity role = roleRepository.findById(2).orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = new UserEntity();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.getName();
        user.setPassword(encoder.encode(requestMap.get("password")));  // Encrypting password
        user.setRole(role);
        return user;
    }

}
