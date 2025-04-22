package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Users;

@RestController		
@RequestMapping("/user")
public class UserController {
	@Autowired
	private userRepository usersRepository;
	
	@Autowired
	private com.example.demo.service.AuctionService auctionService;
	
	// find all auctions partisipated by a user
	@GetMapping("/auctions/{userId}")
	public ResponseEntity<List<Auction>> getUserAuctions(@PathVariable("userId") int userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		List<Auction> auctions = auctionService.getAuctionById(user);
		return ResponseEntity.ok(auctions);
	}
	

}
