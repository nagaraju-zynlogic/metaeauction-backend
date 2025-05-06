package com.example.demo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.AutoBidConfigRepository;
import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;
import com.example.demo.service.AuctionService;
import com.example.demo.service.AutoBidService;
import com.example.demo.service.BidService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bids")
@Slf4j
public class BidController {

	
	@Autowired
	private AutoBidService autoBidService;

		@Autowired
	    private  userRepository usersRepository;
		@Autowired
	    private  AuctionService auctionService;
	    @Autowired
	    private  BidService bidService;
	    @Autowired
	    private AutoBidConfigRepository autoBidConfigRepository;
	    
	   
	    @Transactional
	    @PostMapping("/bid/{userId}/{auctionId}/{bidAmount}")
	    public ResponseEntity<?> placeBid(@PathVariable("userId") int userId,
	                                      @PathVariable("auctionId") int auctionId,
	                                      @PathVariable("bidAmount") double bidAmount) {

	        LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();

	        Users user = usersRepository.findById(userId).orElse(null);
	        Auction auction = auctionService.getAuctionById(auctionId);

	        if (user == null || auction == null) {
	            return ResponseEntity.badRequest().body("user or auction not found");
	        }

	        if (!user.getStatus().equalsIgnoreCase("verified")) {
	            return ResponseEntity.badRequest().body("user not verified");
	        }

	        if (auction.getStartDate().isAfter(NOW) || auction.getEndDate().isBefore(NOW)) {
	            return ResponseEntity.badRequest().body("Auction is not active");
	        }

	        // Extend end time if auction is ending in < 3 min
	        if (Duration.between(NOW, auction.getEndDate()).toMinutes() < 3) {
	            auctionService.updateAuctionEndTime(auction.getId(), 3);
	        }

	        // Save manual bid
	        Bid manualBid = new Bid();
	        manualBid.setUser(user);
	        manualBid.setAuction(auction);
	        manualBid.setBidAmount(bidAmount);
	        manualBid.setBidTime(NOW);
	        manualBid.setBidStatus("PLACED");

	        Bid savedBid = bidService.saveBid(manualBid);

	        // Call auto-bid service to simulate competition
	        autoBidService.processAutoBids(auction, user, bidAmount);

	        return ResponseEntity.ok(savedBid);
	    }

		
	   

}



