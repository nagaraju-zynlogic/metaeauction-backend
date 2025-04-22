package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
		Admin findByEmail(String email);
	
}
