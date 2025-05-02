package com.example.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.RegisterRequest;
import com.example.demo.entity.Users;
import com.example.demo.service.AuctionService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private userRepository userrepository;
    
    @Autowired
    private AuctionService auctionService;
    
    
    

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        Optional<Users> userOptional = userrepository.findByemail(loginRequest.getEmail());
//
//        if (userOptional.isPresent()) {
//            Users user = userOptional.get();
//
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            if (encoder.matches(loginRequest.getPassword(), user.getPassword())) {
//                return ResponseEntity.ok("Login successful!");
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
//            }
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String name = registerRequest.getName();
        String email = registerRequest.getEmail();
      
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        // Name validation
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }

        // Email validation
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        // Password validation
        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
        }

        // Confirm password match
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        // Check if email already exists
        if (userrepository.findByemail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        }

        // Hash the password before saving
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        // Save user
        Users newUser = new Users();
        newUser.setUsername(name);
        newUser.setEmail(email);

        newUser.setPassword(hashedPassword);
        newUser.setActive(1); // Set active status to 1 (active)
        userrepository.save(newUser);
        

        return ResponseEntity.ok("User registered successfully.");
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Users> userOptional = userrepository.findByemail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Return user details as JSON
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
//                userData.put("role", user.getRole()); // assuming you store role
                userData.put("status",user.getStatus());
                auctionService.updateAuctionStatus();
                return ResponseEntity.ok(userData);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


	
    
    
    

    
}

