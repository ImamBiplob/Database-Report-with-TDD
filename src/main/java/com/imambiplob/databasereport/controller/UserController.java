package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/addUser")
    public User addUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent())
            return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("User with ID: " + id + " doesn't exist"), HttpStatus.NOT_FOUND);
    }
}
