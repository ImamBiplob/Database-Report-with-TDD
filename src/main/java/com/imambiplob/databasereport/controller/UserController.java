package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.LoginRequest;
import com.imambiplob.databasereport.dto.LoginResponse;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.dto.UserDTO;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.TimeUnit;

@Controller
//@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login/view")
    public ModelAndView login() {

        createSysRoot();

        ModelAndView mav = new ModelAndView("login-form");
        LoginRequest loginRequest = new LoginRequest();
        mav.addObject(loginRequest);

        return mav;

    }

    @PostMapping("/login")
    public String loginAndRedirect(@Valid @ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {

            Cookie cookie = new Cookie("token", jwtService.generateToken(loginRequest.getUsername()));
            int maxAgeInSeconds = (int) TimeUnit.MINUTES.toSeconds(10);
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/api");
            cookie.setDomain("report.jotno.dev");
            response.addCookie(cookie);

            return "redirect:/api/reports/view";
        }

        else throw new UsernameNotFoundException("Invalid User Request!!!");
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/api");
                    cookie.setDomain("report.jotno.dev");
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return "redirect:/api/users/login/view";
    }

    /* REST APIs Start from Here... */

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateAndGetToken(@Valid @RequestBody LoginRequest loginRequest) {

        createSysRoot();

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(loginRequest.getUsername()));

        if(authentication.isAuthenticated())
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);

        else throw new UsernameNotFoundException("Invalid User Request!!!");

    }

    @PostMapping("/addUser")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public User addUser(@Valid @RequestBody UserDTO userDTO) {

        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRoles(userDTO.getRoles());

        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        if(userRepository.findById(id).isPresent())
            return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("User with ID: " + id + " doesn't exist"), HttpStatus.NOT_FOUND);
    }

    public void createSysRoot() {
        if(userRepository.findUserByUsername("imambiplob") == null) {
            User user = new User();
            user.setName("Imam Hossain");
            user.setUsername("imambiplob");
            user.setPassword(passwordEncoder.encode("imam123"));
            user.setEmail("imamhbiplob@gmail.com");
            user.setPhone("01521559190");
            user.setRoles("SYS_ROOT,DEVELOPER");

            userRepository.save(user);
        }
    }
}
