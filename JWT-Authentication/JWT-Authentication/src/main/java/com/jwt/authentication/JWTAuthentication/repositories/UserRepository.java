package com.jwt.authentication.JWTAuthentication.repositories;

import com.jwt.authentication.JWTAuthentication.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

     Optional<User> findByEmail(String email);

     List<User> findUserByEmail(String email);

     @Query(value = "SELECT u.* FROM User_table u where u.email = :email", nativeQuery = true )
     Page<User> findByFilter(String email,Pageable pageable);
}
