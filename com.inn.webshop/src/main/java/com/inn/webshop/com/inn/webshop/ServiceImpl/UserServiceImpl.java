package com.inn.webshop.com.inn.webshop.ServiceImpl;

import com.inn.webshop.com.inn.webshop.JWT.JwtService;
import com.inn.webshop.com.inn.webshop.ServiceImpl.service.UserService;
import com.inn.webshop.com.inn.webshop.constents.Constants;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import com.inn.webshop.com.inn.webshop.data.repository.UserRepository;
import com.inn.webshop.com.inn.webshop.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Lazy
    PasswordEncoder encoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    @Lazy
    private AuthenticationManager manager;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUp(requestMap)) {
                UserEntity userEntity = userRepository.findByEmail(requestMap.get("email"));
                if (Objects.isNull(userEntity)) {
                    // Saving the user in the database
                    UserEntity savedUser = userRepository.save(getUserFromMap(requestMap));

                    // Generate the token after saving the user
                    String token = jwtService.generateToken(savedUser, savedUser.getEmail(), savedUser.getRole());

                    // Returning success message along with the token
                    return Utils.getResponseEntity("Successfully Registered. Token: " + token, HttpStatus.OK);
                } else {
                    return Utils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            String email = requestMap.get("email");
            String password = requestMap.get("password");

            // Authenticate the user
            manager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            // Find user by email
            var user = userRepository.findByEmail(email);

            if (Objects.isNull(user)) {
                return Utils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
            }

            // Generate the JWT token
            String token = jwtService.generateToken(user, user.getEmail(), user.getRole());

            // Return the response with the token
            return Utils.getResponseEntity("Login successful. Token: " + token, HttpStatus.OK);
        } catch (Exception ex) {
            return Utils.getResponseEntity("Invalid credentials or something went wrong", HttpStatus.UNAUTHORIZED);
        }
    }


        @Override
        public ResponseEntity<String> login(LoginDto dto) {
            try {
                // Authenticate the user using UsernamePasswordAuthenticationToken
                manager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmailAddress(), dto.getPassword()));

                // Find user by email
                var user = userRepository.findByEmail(dto.getEmailAddress());

                // Check if the user exists
                if (Objects.isNull(user)) {
                    return Utils.getResponseEntity("User not found", HttpStatus.BAD_REQUEST);
                }

                // Generate the JWT token
                String token = jwtService.generateToken(user, user.getEmail(), user.getRole());

                // Return the response with the token
                return Utils.getResponseEntity("Login successful. Token:  " + token, HttpStatus.OK);
            } catch (Exception ex) {
                // Handle exceptions (e.g., invalid credentials or any other error)
                return Utils.getResponseEntity("Invalid credentials or something went wrong", HttpStatus.UNAUTHORIZED);
            }
        }


    private boolean validateSignUp(Map<String, String> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password");
    }

    private UserEntity getUserFromMap(Map<String, String> requestMap) {
        UserEntity user = new UserEntity();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(encoder.encode(requestMap.get("password")));  // Encrypting password
        user.setRole("user");
        return user;
    }
}
