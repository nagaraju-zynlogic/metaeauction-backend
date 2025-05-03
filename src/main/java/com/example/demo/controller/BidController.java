package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	    
	    private  LocalDateTime NOW = auctionService.getIndianTime().now();
	    
	    @PostMapping("/bid/{userId}/{auctionId}/{bidAmount}")
	    public ResponseEntity<?> placeBid(@PathVariable("userId") int userId,
	                                           @PathVariable("auctionId") int auctionId,
	                                           @PathVariable("bidAmount") double bidAmount) {
	        // Fetch the user and auction details
	    	log.info("userId: " + userId);
	    	log.info("auctionId: " + auctionId);
	    	log.info("bidAmount: " + bidAmount);
	        Users user = usersRepository.findById(userId).orElse(null);
	        Auction auction = auctionService.getAuctionById(auctionId);
	        

	        if (user == null || auction == null ) {
	        	log.error("User or Auction not found");
                 	            return ResponseEntity.badRequest().body("user or auction not found");
	        }
	        // chack user has verified or not
	        if(!user.getStatus().equalsIgnoreCase("verified")) {
	        	log.info("user not verified");
	        	return ResponseEntity.badRequest().body("user not verified");
	        }
	        		        	
	        
	        
	        
	        // Check if the auction is active
	        if (auction.getStartDate().isAfter(NOW) || auction.getEndDate().isBefore(NOW)) {
	        		log.error("Auction is not active");
	        			            return ResponseEntity.badRequest().body(null);
	        }

	        // check if auction end time is less than 3 mins and incrse 3 mins
			if (java.time.Duration.between(NOW, auction.getEndDate()).toMinutes() < 3) {
				//	auction.setEndDate(auction.getEndDate().plusMinutes(3));
					auctionService.updateAuctionEndTime(auction.getId(), 3);
			}
	        
	        // Create a new bid

	        
	        
	        Bid bid = new Bid();
	        
	        bid.setUser(user);
	        bid.setAuction(auction);
	        bid.setBidAmount(bidAmount);
	        bid.setBidTime(NOW);

	        // Save the bid
	        Bid savedBid = bidService.saveBid(bid);
	        log.info("Bid saved successfully");



	        return ResponseEntity.ok(savedBid); 
	    }
	    // get bids by auction id and user id
	    @GetMapping("/getBids/{userId}/{auctionId}")
	    public ResponseEntity<List<Bid>> getBidsByUserAndAuction(@PathVariable("userId") int userId,
	                                                             @PathVariable("auctionId") int auctionId) {
	        Users user = usersRepository.findById(userId).orElse(null);
	        Auction auction = auctionService.getAuctionById(auctionId);

	        if (user == null || auction == null) {
	            return ResponseEntity.badRequest().body(null);
	        }

	        List<Bid> bids = bidService.getBidsByUserAndAuction(user, auction);
	        return ResponseEntity.ok(bids);
	    }
	    // get all bids by auction id
	    @GetMapping("/getAllBids/{auctionId}")
	    public ResponseEntity<List<Bid>> getBidsByAuction(@PathVariable("auctionId") int auctionId) {
	        Auction auction = auctionService.getAuctionById(auctionId);

	        if (auction == null) {
	            return ResponseEntity.badRequest().body(null);
	        }

	        List<Bid> bids = bidService.getBidsByAuction(auction);
	        return ResponseEntity.ok(bids);
	    }
	   // place a bid status  with Sheduled
	    @PostMapping("/sheduled/bid/{userId}/{auctionId}/{bidAmount}")
	    public ResponseEntity<?> placeSheduleBid(@PathVariable("userId") int userId,
	                                           @PathVariable("auctionId") int auctionId,
	                                           @PathVariable("bidAmount") double bidAmount) {
	        // Fetch the user and auction details
	    	log.info("userId: " + userId);
	    	log.info("auctionId: " + auctionId);
	    	log.info("bidAmount: " + bidAmount);
	        Users user = usersRepository.findById(userId).orElse(null);
	        Auction auction = auctionService.getAuctionById(auctionId);
	        

	        if (user == null || auction == null ) {
	        	log.error("User or Auction not found");
                 	            return ResponseEntity.badRequest().body("user or auction not found");
	        }
	        // chack user has verified or not
	        if(!user.getStatus().equalsIgnoreCase("verified")) {
	        	log.info("user not verified");
	        	return ResponseEntity.badRequest().body("user not verified");
	        }
	        // chcke it is upcomming auction or not
	        
	        if(!auction.getStartDate().isAfter(NOW)) {
	        	log.info("auction not upcomming");
	        	return ResponseEntity.badRequest().body("auction not upcomming");
	        	
	        }
	        
	        
	      // Create a new bid       
	        Bid bid = new Bid();
	        
	        bid.setUser(user);
	        bid.setAuction(auction);
	        bid.setBidAmount(bidAmount);
	        bid.setBidStatus("SCEHDULED");
	        bid.setBidTime(NOW);

	        // Save the bid
	        Bid savedBid = bidService.saveBid(bid);
	        log.info("Bid scehduled successfully");

	        return ResponseEntity.ok(savedBid); 
	    }

		
	   
}



