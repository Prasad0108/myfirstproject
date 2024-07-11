package com.jwt.authentication.JWTAuthentication.serviceImpl;

import com.jwt.authentication.JWTAuthentication.entity.User;
import com.jwt.authentication.JWTAuthentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;

    private static final int SECRET_CODE_LENGTH = 16;

    public static String generateSecretCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(SECRET_CODE_LENGTH);
        for (int i = 0; i < SECRET_CODE_LENGTH; i++) {
            int randomIndex = new Random().nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }
    public List<User> getUsers( ){
        return userRepository.findAll();
    }

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found") );
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    public User getUserById(long userid){
        return userRepository.findById(userid).orElseThrow(()->new RuntimeException("User Not Found"));
    }

    public User upadateUser(User user, long userid){
        User existingUser  = userRepository.findById(userid).orElseThrow(()->new RuntimeException("resource not found "));
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());

        return userRepository.save(existingUser);
    }

    public void daleteUser(long userid){
        userRepository.findById(userid).orElseThrow(()->new RuntimeException("user Not Found"));
        userRepository.deleteById(userid);
    }

    public Page<User> getByFilter(String email,  int pagenumber, int pagesize){
        Sort sort =Sort.by(Sort.Direction.ASC,"userid");
        Pageable pageable = PageRequest.of(pagenumber,pagesize,sort);

        userRepository.findAll(pageable);
        if (email==null){
            return userRepository.findAll(pageable);
        }

        List<User> users = userRepository.findUserByEmail(email);

        return userRepository.findByFilter(email,pageable);
    }

}
