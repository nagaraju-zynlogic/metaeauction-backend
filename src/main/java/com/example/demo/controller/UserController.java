package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		List<Auction> auctions = auctionService.getAuctionByUserId(user);
		return ResponseEntity.ok(auctions);
	}
	
	// find user by id
	@GetMapping("/userBy/{userId}")
	public ResponseEntity<Users> getUserById(@PathVariable("userId") Integer userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
	
	// update user  profile 
	@PutMapping("/update/{userId}")
	public ResponseEntity<Users> updateUser(@PathVariable("userId") Integer userId, @RequestBody Users updatedUser) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		user.setUsername(updatedUser.getUsername());
		
		user.setEmail(updatedUser.getEmail());		
		usersRepository.save(user);
		return ResponseEntity.ok(user);
	}
	// change user password after old password verification
	@PostMapping("/changePassword/{userId}")
	public ResponseEntity<String> changePassword(@PathVariable("userId") Integer userId, @RequestBody Users updatedUser) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		if (!user.getPassword().equals(updatedUser.getPassword())) {
			return ResponseEntity.badRequest().body("Old password is incorrect");
		}
		user.setPassword(updatedUser.getPassword());
		usersRepository.save(user);
		return ResponseEntity.ok("Password changed successfully");
	}
	// find won auctions by user id
	@GetMapping("/wonAuctions/{userId}")
	public ResponseEntity<List<Auction>> getWonAuctions(@PathVariable("userId") int userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		List<Auction> auctions = auctionService.getWonAuctionByUserId(user);
		return ResponseEntity.ok(auctions);
	
	}
	
	// delete user by id
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable("userId") Integer userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		usersRepository.delete(user);
		return ResponseEntity.ok("User deleted successfully");
	}

}
