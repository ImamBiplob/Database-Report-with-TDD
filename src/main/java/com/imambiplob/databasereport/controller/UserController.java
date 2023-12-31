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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Controller
//@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final String DOMAIN = "report.jotno.dev";

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/create/view")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ModelAndView createUser() {
        ModelAndView mav = new ModelAndView("create-user-form");
        UserDTO user = new UserDTO();
        mav.addObject("user", user);
        mav.addObject("roles", Arrays.asList("SYS_ROOT", "DEVELOPER", "USER"));

        return mav;
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public String createUser(@Valid @ModelAttribute UserDTO userDTO) {

        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRoles(String.join(",", userDTO.getRoles()));

        userRepository.save(user);

        return "redirect:/api/reports/view";
    }

    @GetMapping("/login/view")
    public ModelAndView login() {

        createSysRoot();
        createDeveloper();
        createEndUser();

        ModelAndView mav = new ModelAndView("login-form");
        LoginRequest loginRequest = new LoginRequest();
        mav.addObject(loginRequest);

        return mav;

    }

    @PostMapping("/login")
    public String loginAndRedirect(@Valid @ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername().toLowerCase(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {

            Cookie cookie = new Cookie("token", jwtService.generateToken(loginRequest.getUsername().toLowerCase()));
            int maxAgeInSeconds = (int) TimeUnit.MINUTES.toSeconds(10);
            cookie.setMaxAge(maxAgeInSeconds);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/api");
            cookie.setDomain(DOMAIN);
            response.addCookie(cookie);

            return "redirect:/api/reports/view";
        } else throw new UsernameNotFoundException("Invalid User Request!!!");
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/api");
                    cookie.setDomain(DOMAIN);
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
        createDeveloper();
        createEndUser();

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername().toLowerCase(), loginRequest.getPassword()));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(loginRequest.getUsername().toLowerCase()));

        if (authentication.isAuthenticated())
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);

        else throw new UsernameNotFoundException("Invalid User Request!!!");

    }

    @PostMapping("/addUser")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public User addUser(@Valid @RequestBody UserDTO userDTO) {

        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setRoles(String.join(",", userDTO.getRoles()));

        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        if (userRepository.findById(id).isPresent())
            return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
        return new ResponseEntity<>(new ResponseMessage("User with ID: " + id + " doesn't exist"), HttpStatus.NOT_FOUND);
    }

    public void createSysRoot() {
        if (userRepository.findUserByUsername("imambiplob") == null) {
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

    public void createDeveloper() {
        if (userRepository.findUserByUsername("developer") == null) {
            User user = new User();
            user.setName("Developer");
            user.setUsername("developer");
            user.setPassword(passwordEncoder.encode("dev123"));
            user.setEmail("developer@jotno.net");
            user.setPhone("01xxxxxxxxx");
            user.setRoles("DEVELOPER");

            userRepository.save(user);
        }
    }

    public void createEndUser() {
        if (userRepository.findUserByUsername("user") == null) {
            User user = new User();
            user.setName("user");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@jotno.net");
            user.setPhone("01xxxxxxxxx");
            user.setRoles("USER");

            userRepository.save(user);
        }
    }
}
