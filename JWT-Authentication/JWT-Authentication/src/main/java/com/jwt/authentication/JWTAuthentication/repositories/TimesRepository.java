package com.jwt.authentication.JWTAuthentication.repositories;

import com.jwt.authentication.JWTAuthentication.entity.Times;
import com.jwt.authentication.JWTAuthentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimesRepository extends JpaRepository<Times, Long> {

List<Times> findByUserOrderByIdDesc(User user);
}
