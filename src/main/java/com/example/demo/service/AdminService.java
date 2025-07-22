//package com.example.demo.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.stereotype.Service;
//
//import com.example.demo.Repository.AdminRepository;
//import com.example.demo.entity.Admin;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import java.time.LocalDateTime;
//
//@Service
//public class AdminService {
//	
//	@Autowired
//	private AdminRepository adminRepository;
//	
//	@Autowired
//    private PasswordEncoder passwordEncoder;
//
//	public Admin getAdminByEmail(String email) {
//		// Check if the user exists and has the admin role
//		Admin admin = adminRepository.findByEmail(email);
//		if (admin != null) {
//			return admin;
//		}
//		return null;
//	}
//
//	public void saveAdmin(Admin admin) {
//		// Save the admin to the database
//		adminRepository.save(admin);		
//	}I
//	
//	
//
//
//}












package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.entity.Admin;

import java.time.LocalDateTime;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin getAdminByEmail(String email) {
        // Check if the user exists and has the admin role
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return admin;
        }
        return null;
    }

    public void saveAdmin(Admin admin) {
        // Save the admin to the database
        adminRepository.save(admin);        
    }
    
    /**
     * Registers a new admin. This method securely hashes the password
     * and sets the creation timestamp before saving.
     * @param admin The Admin object containing the plain-text password.
     * @return The saved Admin entity with the hashed password.
     */
    public Admin registerAdmin(Admin admin) {
        // 1. Securely hash the plain-text password from the request
        String hashedPassword = passwordEncoder.encode(admin.getPasswordHash());
        admin.setPasswordHash(hashedPassword);
        
        // 2. Set the current timestamp for when the admin was created
        admin.setCreatedAt(LocalDateTime.now());
        
        // 3. Save the new admin to the database
        return adminRepository.save(admin);    
    }
}
