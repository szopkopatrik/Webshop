package com.inn.webshop.com.inn.webshop.Controller;

import com.inn.webshop.com.inn.webshop.DTO.UserDto;
import com.inn.webshop.com.inn.webshop.data.entity.RoleEntity;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import com.inn.webshop.com.inn.webshop.data.repository.RoleRepository;
import com.inn.webshop.com.inn.webshop.data.repository.UserRepository;
import com.inn.webshop.com.inn.webshop.service.UserService;
import com.inn.webshop.com.inn.webshop.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/webshop/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService service;

    @PreAuthorize("hasRole('admin') or hasRole('user')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id) {

            UserEntity user = userRepository.findById(id).orElse(null);

            // Convert UserEntity to a DTO or send the entity itself (best to use DTOs in production)
            UserDto userDto = new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getPassword(),
                    user.getContactNumber(),
                    user.getEmail(),
                    user.getBirthDate(),
                    user.getRole().getRoleName()
            );

            // Return user details
            return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping()
    public UserEntity saveUser(@RequestBody UserEntity user){
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/getAll")
    public List<UserEntity> getUsers(){
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id){
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('admin') or @userService.hasId(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") int id, @RequestBody UserDto profileDto) {
        // Find user by ID, throw exception if not found
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user properties from the provided DTO
        user.setName(profileDto.getName());
        user.setEmail(profileDto.getEmail());
        user.setPassword(profileDto.getPassword());
        user.setContactNumber(profileDto.getContactNumber());
        user.setBirthDate(profileDto.getBirthDate());

        // If the role is provided in the DTO, update the user's role
        if (profileDto.getRole() != null) {
            // Convert role string to RoleEntity and handle Optional
            RoleEntity roleEntity = roleRepository.findByRoleName(profileDto.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            // Set the role if found
            user.setRole(roleEntity);
        }

        // Save the updated user and return the response
        UserEntity updatedUser = userRepository.save(user);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping(path = "/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMap) {
        return service.changePassword(requestMap);
    }


}
