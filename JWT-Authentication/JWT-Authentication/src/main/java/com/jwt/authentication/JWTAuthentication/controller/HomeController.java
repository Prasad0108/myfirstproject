package com.jwt.authentication.JWTAuthentication.controller;

import com.jwt.authentication.JWTAuthentication.entity.Times;
import com.jwt.authentication.JWTAuthentication.entity.User;
import com.jwt.authentication.JWTAuthentication.model.BlackListToken;
import com.jwt.authentication.JWTAuthentication.model.JwtRequest;
import com.jwt.authentication.JWTAuthentication.repositories.TimesRepository;
import com.jwt.authentication.JWTAuthentication.repositories.UserRepository;
import com.jwt.authentication.JWTAuthentication.serviceImpl.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlackListToken blackListToken;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimesRepository timesRepository;
    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    @GetMapping("/current-user")
    public String getLoggedInUser(Principal principal){
        return principal.getName();
    }

    @GetMapping("/user")
    public List<User> getUser(){
        System.out.println("getting users");
        return  this.userService.getUsers();
    }
    @GetMapping("/get/{email}")
    public User getUserByEmail(@PathVariable String email){
        return userService.getByEmail(email);
    }

    @GetMapping("/getid/{id}")
    public User getUserById(@PathVariable("id") long userid){
        return userService.getUserById(userid);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@RequestBody User user, @PathVariable("id") long userid){
        return userService.upadateUser(user,userid);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long userid){
        userService.daleteUser(userid);
        return "user deleted sucessfully";
    }

    @GetMapping("/pagination")
    public Page<User> studentPagination(
            @RequestParam(value = "email",required = false)String email,
            @RequestParam(defaultValue = "0", required = false) int pagenumber,
            @RequestParam(defaultValue = "5", required = false) int pagesize) {
        return userService.getByFilter(email,pagenumber, pagesize);
    }

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    private String contry = "Asia/Calcutta";
    private final String WORLD_CLOCK_API_URL = "https://timeapi.io/api/Time/current/zone?timeZone="+contry;

    private String extractUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret) // Set your secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Extract the "email" claim from the JWT (assuming "email" is the claim name)
            String email = claims.get("sub", String.class);
            logger.info("email  from token{} ",email);
            return email;
        } catch (Exception e) {
            // Handle exceptions such as token validation failure
            // Return null or throw an exception as needed
            return "user not found";
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String token = extractTokenFromRequest(request);
        String email=null;
//        System.out.println(token);
        if (token != null) {
             email = extractUsernameFromToken(token);
            logger.info("email From Token :{}",email);
            blackListToken.addToBlacklist(token);
        }

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
            LocalTime logoutTime = LocalTime.parse(timeString1, formatter1);

            Optional<User> user = userRepository.findByEmail(email);

//            String token = extractTokenFromRequest(request);
//            String email1 = extractUsernameFromToken(token);

                  if(!ObjectUtils.isEmpty(user))
            user1 = user.get();
                  else
                      logger.info("user not found with email :",email);

//            Times loginTimeEntry = new Times();
            List<Times> times=timesRepository.findByUserOrderByIdDesc(user1);
            if(!ObjectUtils.isEmpty(times)) {

                Times times1 = times.get(0);
                times1.setLogoutTime(logoutTime);
                times1.setUser(user1);
                timesRepository.save(times1);
            }else {
                logger.info("user not found: {}");
            }
//            System.out.println(" Date: " + dateIndex);
            System.out.println(" Time: " + timeIndex);
        } else {
            System.out.println("The Object is not a LocalDateTime or compatible type.");
        }


        return ResponseEntity.ok("Logged out successfully");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // 7 is the length of "Bearer "
        }
        return null;
    }
}
