package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;
import com.example.demo.service.AuctionService;
import com.example.demo.service.BidService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bids")
@Slf4j
public class BidController {

	
		@Autowired
	    private  userRepository usersRepository;
		@Autowired
	    private  AuctionService auctionService;
	    @Autowired
	    private  BidService bidService;
	    
	    @PostMapping("/bid/{userId}/{auctionId}/{bidAmount}")
	    public ResponseEntity<Bid> placeBid(@PathVariable("userId") int userId,
	                                           @PathVariable("auctionId") int auctionId,
	                                           @PathVariable("bidAmount") double bidAmount) {
	        // Fetch the user and auction details
	    	log.info("userId: " + userId);
	    	log.info("auctionId: " + auctionId);
	    	log.info("bidAmount: " + bidAmount);
	        Users user = usersRepository.findById(userId).orElse(null);
	        Auction auction = auctionService.getAuctionById(auctionId);
	        

	        if (user == null || auction == null) {
	        	log.error("User or Auction not found");
                 	            return ResponseEntity.badRequest().body(null);
	        }

	        // Check if the auction is active
	        if (auction.getStartDate().isAfter(LocalDateTime.now()) || auction.getEndDate().isBefore(LocalDateTime.now())) {
	        		log.error("Auction is not active");
	        			            return ResponseEntity.badRequest().body(null);
	        }

	        // Create a new bid
	        
	        Bid bid = new Bid();
	        
	        bid.setUser(user);
	        bid.setAuction(auction);
	        bid.setBidAmount(bidAmount);
	        bid.setBidTime(LocalDateTime.now());

	        // Save the bid
	        Bid savedBid = bidService.saveBid(bid);
	        log.info("Bid saved successfully");
	        return ResponseEntity.ok(savedBid);
	    }
	   
	}



