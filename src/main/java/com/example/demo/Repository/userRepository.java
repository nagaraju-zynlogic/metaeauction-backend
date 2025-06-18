package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Users;

public interface userRepository  extends JpaRepository<Users, Integer> , CrudRepository<Users, Integer> {
    Optional<Users> findByemail(String email);
    @Query(value = "SELECT * FROM users12 WHERE active = 0", nativeQuery = true)
    List<Users> findAllInactiveUsers();
    // GET inactive user by id
    @Query(value = "SELECT * FROM users12 WHERE id = ?1 AND active = 0", nativeQuery = true)
    Optional<Users> findInactiveUserById(Integer id);
    // find all users active and inactive
    @Query(value = "SELECT * FROM users12", nativeQuery = true)
    List<Users> findAllUsers();
    
    
    Optional<Users> findById(Integer id);
}
