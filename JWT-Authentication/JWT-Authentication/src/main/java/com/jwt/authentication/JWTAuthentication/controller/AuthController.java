package com.jwt.authentication.JWTAuthentication.controller;

import com.jwt.authentication.JWTAuthentication.entity.Times;
import com.jwt.authentication.JWTAuthentication.entity.User;
import com.jwt.authentication.JWTAuthentication.model.BlackListToken;
import com.jwt.authentication.JWTAuthentication.model.JwtRequest;
import com.jwt.authentication.JWTAuthentication.model.JwtResponce;
import com.jwt.authentication.JWTAuthentication.repositories.TimesRepository;
import com.jwt.authentication.JWTAuthentication.repositories.UserRepository;
import com.jwt.authentication.JWTAuthentication.security.JwtHelper;
import com.jwt.authentication.JWTAuthentication.serviceImpl.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TimesRepository timesRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtHelper helper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BlackListToken blackListToken;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    private String contry = "Asia/Calcutta";
    private final String WORLD_CLOCK_API_URL = "https://timeapi.io/api/Time/current/zone?timeZone="+contry;

    @PostMapping("/login")
    public ResponseEntity<JwtResponce> login(@RequestBody JwtRequest request) {

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(WORLD_CLOCK_API_URL, Map.class);

        List<Object> valuesList = new ArrayList<>(response.values());

        int index = 8 & 9; // The index you want to access
        User user1=null;
        if (index >= 0 && index < valuesList.size()) {

            Object dateIndex = valuesList.get(8);
            Object timeIndex = valuesList.get(9);

            String timeString = (String) dateIndex; // Assuming valueAtIndex is a string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate loginDate = LocalDate.parse(timeString, formatter);

            String timeString1 = (String) timeIndex;// Adjust the pattern according to the actual format
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime loginTime = LocalTime.parse(timeString1, formatter1);

            Optional<User> user = userRepository.findByEmail(request.getEmail());
             user1 = user.get();

            Times loginTimeEntry = new Times();
            loginTimeEntry.setDate(loginDate);
            loginTimeEntry.setLoginTime(loginTime);
            loginTimeEntry.setUser(user1);


            timesRepository.save(loginTimeEntry);

            System.out.println(" Date: " + dateIndex);
            System.out.println(" Time: " + timeIndex);
        } else {
            System.out.println("The Object is not a LocalDateTime or compatible type.");
        }

        this.doAuthenticate(request.getEmail(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());


       JwtResponce responce= generateAuthenticationResponse(user1);

       return new ResponseEntity<>(responce, HttpStatus.OK);
    }

    public JwtResponce generateAuthenticationResponse(User user) {
        var jwtToken = helper.generateToken(user.getUsername(), user.getRole());
        var role = user.getRole();
        var userName = user.getName();
        return JwtResponce.builder()
                .jwtToken(jwtToken)
                .username(userName)
                .role(role)
                .build();
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }
    @PostMapping("/save")
    public User saveUser(@RequestBody User user) throws IOException {
        return userService.createUser(user);
    }
}
