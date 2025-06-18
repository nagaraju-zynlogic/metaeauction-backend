package com.example.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Users;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> userOptional = userRepository.findByemail(email);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }


    public Users getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
