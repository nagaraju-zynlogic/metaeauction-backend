package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.AdminRepository;
import com.example.demo.entity.Admin;

@Service
public class AdminService {
	
	@Autowired
	private AdminRepository adminRepository;

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

}
