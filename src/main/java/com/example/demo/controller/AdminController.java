package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
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
	
	// Check admin credentials 
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Admin adminRequest) {
	    Admin existingAdmin = adminService.getAdminByEmail(adminRequest.getEmail());
	    // no encryption for simplicity
	    if (existingAdmin != null && existingAdmin.getPasswordHash().equals(adminRequest.getPasswordHash())) {
	        // Generate a response with admin details
	    
	        return ResponseEntity.ok(existingAdmin);
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
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
	// update auction
	@PostMapping("/update/auction")
	public ResponseEntity<Auction> updateAuction(@RequestBody Auction auction) {
		// Validate the auction details
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate()) 
				|| auction.getStartDate().isBefore(java.time.LocalDateTime.now())) {
				return ResponseEntity.badRequest().body(null);
		}
		
		// Save the auction
		Auction updatedAuction = auctionService.saveAuction(auction);
		
		
		if (updatedAuction != null) {
			return ResponseEntity.ok(updatedAuction);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	
	}
	@PostMapping("/delete/auction")
	public ResponseEntity<String> deleteAuction(@RequestBody Auction auction) {
		// Validate the auction details
		if (auction.getId() == null) {
			return ResponseEntity.badRequest().body("Auction ID is required");
		}
		
		// Delete the auction
		auctionService.deleteAuction(auction.getId());
		
		return ResponseEntity.ok("Auction deleted successfully");
	}
	
	// find all bids
	@PostMapping("/bids/{auctionId}")
	public ResponseEntity<List<Bid>> getAllBids(@PathVariable("auctionId") Integer auctionId) {
		Auction auction = auctionService.getAuctionById(auctionId);
		if (auction == null) {
			return ResponseEntity.notFound().build();
		}
		List<Bid> bids = bidService.getBidsByAuction(auction);
		return ResponseEntity.ok(bids);
	}
	
	
	
}
