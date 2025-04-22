package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Auction;
import com.example.demo.service.AdminService;
import com.example.demo.service.AuctionService;
import com.example.demo.service.BidService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
	
	private static final String ADMIN_ROLE = "ADMIN";
	@Autowired
	private userRepository usersRepository;
	@Autowired
	private AuctionService auctionService;
	
	@Autowired
	private BidService bidService;
	@Autowired
	private AdminService adminService;
	
	// check admin credentials 
	@PostMapping("/login")
	public ResponseEntity<Admin> login(@RequestBody Admin admin) {
		
		// Check if the user exists and has the admin role
		Admin existingAdmin = adminService.getAdminByEmail(admin.getEmail());
		
		if (existingAdmin != null && existingAdmin.getPasswordHash().equals(admin.getPasswordHash())) {
			return ResponseEntity.ok(existingAdmin);
		} else {
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	
		
	}
	// insert auction 
	@PostMapping("/insert/auction")
	public ResponseEntity<Auction> insertAuction(@RequestBody Auction auction) {
		// Validate the auction details
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate()) 
				|| auction.getStartDate().isBefore(java.time.LocalDateTime.now())) {
				return ResponseEntity.badRequest().body(null);
		}
		
		// Save the auction
		Auction newAuction = auctionService.saveAuction(auction);
		
		
		if (newAuction != null) {
			return ResponseEntity.ok(newAuction);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	
	}
}
