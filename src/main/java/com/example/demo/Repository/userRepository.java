package com.example.demo.Repository;

import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Users;

public interface userRepository  extends JpaRepository<Users, Integer>{
    Optional<Users> findByemail(String email);


}
