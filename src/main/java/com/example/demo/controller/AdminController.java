package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.userRepository;
import com.example.demo.dto.AcceptBidDTO;
import com.example.demo.dto.AuctionReqFrom;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;
import com.example.demo.service.AdminService;
import com.example.demo.service.AuctionService;
import com.example.demo.service.BidService;
import com.example.demo.statusEnum.AuctionStatus;

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
	@PostMapping("/inserting/auction")
	public ResponseEntity<?> insertAuction(@RequestBody Auction auction) {
		// Validate the auction details
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate()) 
				|| auction.getStartDate().isBefore(java.time.LocalDateTime.now().minusMinutes(5))) {
			return ResponseEntity.badRequest().body("Invalid Action details" );
		}
		
		// Save the auction
		Auction newAuction = auctionService.saveAuction(auction);
		
		
		if (newAuction != null) {
			return ResponseEntity.ok(newAuction);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	
	}
	
	
	
	// insert auction 
//	@PostMapping("/inserting/auction")
//	public ResponseEntity<?> insertAuction(@RequestBody AuctionReqFrom auctionReqFrom) {
//	    // Validate the auction details
//		// accept start date before 5 minutes
//		  LocalDateTime startDateTime = auctionReqFrom.getStartDate().toLocalDateTime();
//		    LocalDateTime endDateTime = auctionReqFrom.getEndDate().toLocalDateTime();
//		    if (startDateTime == null || endDateTime == null || startDateTime.isAfter(endDateTime) 
//		            || startDateTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
//		        // return invalid data info
//		        return ResponseEntity.badRequest().body("Invalid auction details");
//		    }
//		    
//		    
////		if (auctionReqFrom.getStartDate() == null || auctionReqFrom.getEndDate() == null || auctionReqFrom.getStartDate().isAfter(auctionReqFrom.getEndDate()) 
////	            ||  auctionReqFrom.getStartDate().isBefore(localDateTime.now().plusMinutes(5))) {
////			// return invalid data info
////			return ResponseEntity.badRequest().body("Invalid auction details");
////			
////	        
////	    }
//
//	    // Convert OffsetDateTime to LocalDateTime (stripping timezone info)
//	  
//
//	    // Save the auction
//	    Auction auction = new Auction();
//	    auction.setName(auctionReqFrom.getName());
//	    auction.setDescription(auctionReqFrom.getDescription());
//	    auction.setStartDate(startDateTime); // Use LocalDateTime
//	    auction.setEndDate(endDateTime); // Use LocalDateTime
//	    auction.setStartingPrice(auctionReqFrom.getStartingPrice());
//	    auction.setStatus(auctionReqFrom.getStatus());
//	    auction.setCreatedByAdminId(1);
//	    auction.setCreatedAt(java.time.LocalDateTime.now());
//
//	    // Save the auction
//	    Auction newAuction = auctionService.saveAuction(auction);
//	    if (newAuction != null) {
//	        return ResponseEntity.ok(newAuction);
//	    } else {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//	    }
//	}

	
	
	
	// update auction
	@PostMapping("/update/auction")
	public ResponseEntity<Auction> updateAuction(@RequestBody Auction auction) {
		// Validate the auction details
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate())) {
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
	@DeleteMapping("/delete/auction")
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
	@GetMapping("/bids/{auctionId}")
	public ResponseEntity<List<Bid>> getAllBids(@PathVariable("auctionId") Integer auctionId) {
		log.info("Fetching bids for auction ID: " + auctionId);
		Auction auction = auctionService.getAuctionById(auctionId);
		log.info("Auction: " + auction);
		if (auction == null) {
			return ResponseEntity.notFound().build();
		}
		List<Bid> bids = bidService.getBidsByAuction(auction);
		return ResponseEntity.ok(bids);
	}
	// find all users
	@GetMapping("/users")
	public ResponseEntity<List<Users>> getAllUsers() {
		List<Users> users = usersRepository.findAll();
		if (users.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(users);
	}
	// delete user by id
	@DeleteMapping("/delete/user/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable("userId") Integer userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		List<Auction> auctions = auctionService.getAuctionByUserId(user);
		if (!auctions.isEmpty()) {
			return ResponseEntity.badRequest().body("User has auctions, cannot delete");
		}
		
		usersRepository.delete(user);
		return ResponseEntity.ok("User deleted successfully");
	}
	
	// accept bid based on bid id, auction id, user id and amount
	@PostMapping("/acceptBid")
	public ResponseEntity<String> acceptBid(@RequestBody AcceptBidDTO bidDTO) {
		// Validate the bid details
		if (bidDTO.getBidId() == null || bidDTO.getAuctionId() == null || bidDTO.getUserId() == null || bidDTO.getBidAmount() <= 0) {
			return ResponseEntity.badRequest().body("Invalid bid details");
		}
		// reject reamining all bids
		List<Bid> bids = bidService.getBidsByAuction(auctionService.getAuctionById(bidDTO.getAuctionId()));
		for (Bid bid : bids) {
			if (bid.getId() != bidDTO.getBidId()) {
				bid.setBidStatus("REJECTED");
				bidService.saveBid(bid);
			}
		}
		// Fetch the bid
		Bid bid = bidService.getBidById(bidDTO.getBidId());
		if (bid == null) {
			return ResponseEntity.notFound().build();
		}
		
		Auction  eacution = auctionService.getAuctionById(bidDTO.getAuctionId());
		if (eacution == null) {
			return ResponseEntity.notFound().build();
		}
		eacution.setHighestBidAmount(bidDTO.getBidAmount());
		eacution.setHighestBidderId(bidDTO.getUserId());
		eacution.setBidId(bidDTO.getBidId());		
		eacution.setStatus(AuctionStatus.COMPLETED);
		auctionService.saveAuction(eacution);
		// Accept the bid
		bid.setBidStatus("ACCEPTED");
		bidService.saveBid(bid);
		
		
		return ResponseEntity.ok("Bid accepted successfully");


	
	}
	// reject bid based on bid id
	@PostMapping("/rejectBid/{bidId}")
	public ResponseEntity<String> rejectBid(@PathVariable("bidId") Integer bidId) {
		// Fetch the bid
		Bid bid = bidService.getBidById(bidId);
		if (bid == null) {
			return ResponseEntity.notFound().build();
		}
		
		// Reject the bid
		bid.setBidStatus("REJECTED");
		bidService.saveBid(bid);
		
		return ResponseEntity.ok("Bid rejected successfully");
	}
	
	
	
	
	
}
