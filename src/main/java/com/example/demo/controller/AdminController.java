package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.AuctionRepository;
import com.example.demo.Repository.userRepository;
import com.example.demo.dto.AcceptBidDTO;
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
	private AuctionRepository arepo;
	
	@Autowired
	private BidService bidService;
	@Autowired
	private AdminService adminService;
	
	// Check admin credentials 
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Admin adminRequest) {
	    Admin existingAdmin = adminService.getAdminByEmail(adminRequest.getEmail());
	    auctionService.updateAuctionStatus();
	    // no encryption for simplicity
	    if (existingAdmin != null && existingAdmin.getPasswordHash().equals(adminRequest.getPasswordHash())) {
	        // Generate a response with admin details
	    	auctionService.updateAuctionStatus();
	        return ResponseEntity.ok(existingAdmin);
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
	    }
	}
	
	// insert auction 
	@PostMapping("/inserting/auction")
	public ResponseEntity<?> insertAuction(@RequestBody Auction auction) {
		// Validate the auction details
		// || auction.getStartDate().isBefore(java.time.LocalDateTime.now().minusMinutes(10))
		log.info("input from the form data" + auction);
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate())) {
			log.info("Invalid auction details");
			return ResponseEntity.badRequest().body("Invalid Action details" );
		}
		
		
		// Save the auction
		
		Auction newAuction = auctionService.saveAuction(auction);
		log.info("Auction saved successfully");
		
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
	@PutMapping("/update/auction")
	public ResponseEntity<Auction> updateAuction(@RequestBody Auction auction) {
		// Validate the auction details
		
		if (auction.getStartDate() == null || auction.getEndDate() == null || auction.getStartDate().isAfter(auction.getEndDate())) {
			log.info("Invalid auction details");
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

	/*	@DeleteMapping("/delete/auction")
		public ResponseEntity<String> deleteAuction(@RequestBody Auction auction) {
			// Validate the auction details
			if (auction.getId() == null) {
				log.info("Invalid auction details");
				return ResponseEntity.badRequest().body("Auction ID is required");
			}
			
			// Delete the auction
			auctionService.deleteAuction(auction.getId());
			log.info("Auction deleted successfully");
			return ResponseEntity.ok("Auction deleted successfully");
		}*/
	
	
	// delete auction by setting is Active 0
	
	@DeleteMapping("/delete/auction")
	public ResponseEntity<String> deleteAuction(@RequestBody Auction auction) {
	    if (auction.getId() == null) {
	        log.warn("Attempted to delete auction with null ID");
	        return ResponseEntity.badRequest().body("Auction ID is required");
	    }

	    Auction existingAuction = auctionService.getAuctionById(auction.getId());
	    if (existingAuction == null) {
	        log.warn("Auction not found for deletion (ID: {})", auction.getId());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auction not found");
	    }

	    if (!auctionService.canDeleteAuction(auction.getId())) {
	        log.warn("Cannot delete auction (ID: {}) due to existing bids or auto-bids", auction.getId());
	        return ResponseEntity.badRequest().body("Cannot delete auction because it has existing bids or auto-bids");
	    }

	    boolean deleted = auctionService.softDeleteAuction(auction.getId());
	    if (deleted) {
	        log.info("Auction soft-deleted successfully (ID: {})", auction.getId());
	        return ResponseEntity.ok("Auction soft-deleted successfully");
	    } else {
	        log.warn("Auction not found or already deleted (ID: {})", auction.getId());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Auction not found or already deleted");
	    }
	}


	// find all bids
	@GetMapping("/bids/{auctionId}")
	public ResponseEntity<List<Bid>> getAllBids(@PathVariable("auctionId") Integer auctionId) {
		
		log.info("Fetching bids for auction ID: " + auctionId);
		Auction auction = auctionService.getAuctionById(auctionId);
		log.info("Auction: " + auction);
		if (auction == null) {
			log.error("Auction not found");
			return ResponseEntity.notFound().build();
		}
		List<Bid> bids = bidService.getBidsByAuction(auction);
		return ResponseEntity.ok(bids);
	}
	// find all users
	@GetMapping("/users")
	public ResponseEntity<List<Users>> getAllUsers() {
		List<Users> users = usersRepository.findAllUsers();
		log.info("Fetching all users");
		if (users.isEmpty()) {
			log.error("No users found");
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(users);
	}
	// delete user by id
//	@DeleteMapping("/delete/user/{userId}")
//	public ResponseEntity<String> deleteUser(@PathVariable("userId") Integer userId) {
//		Users user = usersRepository.findById(userId).orElse(null);
//		if (user == null) {
//			return ResponseEntity.notFound().build();
//		}
//		List<Auction> auctions = auctionService.getAuctionByUserId(user);
//		if (!auctions.isEmpty()) {
//			return ResponseEntity.badRequest().body("User has auctions, cannot delete");
//		}
//		
//		usersRepository.delete(user);
//		return ResponseEntity.ok("User deleted successfully");
//	}
	
	// delete user by id by setting active to 0
	@DeleteMapping("/delete/user/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable("userId") Integer userId) {
		Users user = usersRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.badRequest().body("User not found");
		}
		user.setActive(0);
		
		// handle exception if user has any auctions
		try {
			List<Auction> auctions = auctionService.getAuctionByUserId(user);
			if (!auctions.isEmpty()) {
				return ResponseEntity.badRequest().body("User has auctions, cannot delete");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error occurred while checking auctions");
		}
		usersRepository.save(user);
		log.info("User deleted successfully");
		
		return ResponseEntity.ok("User deleted successfully");
	}
	
	// accept bid based on bid id, auction id, user id and amount
	@PostMapping("/acceptBid")
	public ResponseEntity<String> acceptBid(@RequestBody AcceptBidDTO bidDTO) {
		// Validate the bid details
		if (bidDTO.getBidId() == null || bidDTO.getAuctionId() == null || bidDTO.getUserId() == null || bidDTO.getBidAmount() <= 0) {
			log.info("Invalid bid details");
			return ResponseEntity.badRequest().body("Invalid bid details");
		}
		// reject reamining all bids
		List<Bid> bids = bidService.getBidsByAuction(auctionService.getAuctionById(bidDTO.getAuctionId()));
		for (Bid bid : bids) {
			if (bid.getId() != bidDTO.getBidId()) {
				bid.setBidStatus("REJECTED");
//				bidService.saveBid(bid);
				
			}
		}
		bidService.saveAllBids(bids);
		// Fetch the bid
		Bid bid = bidService.getBidById(bidDTO.getBidId());
		if (bid == null) {
			log.error("Bid not found");
			return ResponseEntity.badRequest().body("Bid not found");
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
		log.info("Bid accepted successfully");
		
		return ResponseEntity.ok("Bid accepted successfully");


	
	}
	// reject bid based on bid id
	@PostMapping("/rejectBid/{bidId}")
	public ResponseEntity<String> rejectBid(@PathVariable("bidId") Integer bidId) {
		// Fetch the bid
		Bid bid = bidService.getBidById(bidId);
		if (bid == null) {
			log.error("Bid not found");
			return ResponseEntity.badRequest().body("Bid not found");
		}
		
		// Reject the bid
		bid.setBidStatus("REJECTED");
		bidService.saveBid(bid);
		log.info("Bid rejected successfully");
		
		return ResponseEntity.ok("Bid rejected successfully");
	}
	
	// retrieve user by setting active to 1
	@PostMapping("/active/user/{userId}")
	public ResponseEntity<String> retrieveUser(@PathVariable("userId") Integer userId) {
	    Users user = usersRepository.findInactiveUserById(userId).orElse(null);
	    if (user == null) {
	        return ResponseEntity.badRequest().body("Inactive user not found");
	    }
	    user.setActive(1);
	    usersRepository.save(user);
	    return ResponseEntity.ok("User reactivated successfully");
	}

	
	
	// verify user  by setting user status as a verified
	@PostMapping("/verify/user/{userId}")
	public ResponseEntity<String> verifyUser(@PathVariable Integer userId){
		Optional<Users> user = usersRepository.findById(userId);
		if (user.get().getStatus().equalsIgnoreCase("verified")) {
			log.info("user alredy verified");
			return ResponseEntity.ok("user already verifyed");
		}
		else if(user.isPresent()) {
			user.get().setStatus("verified");
		
			usersRepository.save(user.get());
			log.info("User verified");
			return ResponseEntity.ok("User verified");
		}
		else
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		
	}
	
	// register admin
	@PostMapping("/register")
	public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
		// Validate the admin details
		if (admin.getEmail() == null || admin.getPasswordHash() == null) {
			log.info("Invalid admin details");
			return ResponseEntity.badRequest().body("Invalid admin details");
		}
		
		// Check if email already exists
		if (adminService.getAdminByEmail(admin.getEmail()) != null) {
			log.info("Email already exists");
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
		}
		
		// Save the admin
		adminService.saveAdmin(admin);
		log.info("Admin registered successfully");
		
		return ResponseEntity.ok("Admin registered successfully");
	}
	// get all auctions 
	@GetMapping("/AllAuctions")
	public ResponseEntity<?> getAllAuctionForAdmin(){
		
		List<Auction> allAuctions= auctionService.getAllAuctionIncludingInActive();
		allAuctions.forEach(a -> a.setActive(1));
	    arepo.saveAll(allAuctions);
		if(allAuctions.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No auctions found");
		}
		return new ResponseEntity<List<Auction>>(allAuctions,HttpStatus.OK);
	}
	
	
	
	
}
