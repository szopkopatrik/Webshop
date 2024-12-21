package com.inn.webshop.com.inn.webshop.service;

import com.inn.webshop.com.inn.webshop.DTO.UserDto;
import com.inn.webshop.com.inn.webshop.constents.Constants;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import com.inn.webshop.com.inn.webshop.data.repository.UserRepository;
import com.inn.webshop.com.inn.webshop.utils.Utils;
import lombok.Data;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Service
public class UserService {
    @Autowired
    private UserRepository repository;


    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                // Find user by email address
                UserEntity user = repository.findByEmailAddress(email);

                // If user not found, throw exception
                if (user == null) {
                    throw new UsernameNotFoundException("User not found with email: " + email);
                }

                // Add authorities (roles) to the user
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));

                // Return UserDetails object with email, password, and authorities
                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),          // Username is email
                        user.getPassword(),       // Password
                        authorities               // User roles/authorities
                );
            }
        };
    }


    public boolean hasId(int id){
        String username =  ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        UserEntity user = repository.findByEmailAddress(username);
        return user.getId() == id;

    }

    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            // Validate input data
            if (validateChangePassword(requestMap)) {
                // Extract current user email from SecurityContext
                String currentUserEmail = ((org.springframework.security.core.userdetails.User)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

                // Fetch the user from the repository
                UserEntity userEntity = repository.findByEmailAddress(currentUserEmail);
                if (Objects.nonNull(userEntity)) {
                    // Check if the old password matches
                    if (userEntity.getPassword().equals(requestMap.get("oldPassword"))) {
                        // Update the password
                        userEntity.setPassword(requestMap.get("newPassword"));
                        repository.save(userEntity);
                        return Utils.getResponseEntity("Password changed successfully.", HttpStatus.OK);
                    } else {
                        return Utils.getResponseEntity("Old password is incorrect.", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return Utils.getResponseEntity("User not found.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return Utils.getResponseEntity(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Utils.getResponseEntity(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean validateChangePassword(Map<String, String> requestMap) {
        return requestMap.containsKey("oldPassword") &&
                requestMap.containsKey("newPassword") &&
                !requestMap.get("oldPassword").isEmpty() &&
                !requestMap.get("newPassword").isEmpty();
    }
}
