package com.example.demo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.AutoBidConfigRepository;
import com.example.demo.Repository.userRepository;
import com.example.demo.dto.AutomaticBidReq;
import com.example.demo.dto.UpdateAutoBidReq;
import com.example.demo.entity.Auction;
import com.example.demo.entity.AutoBidConfig;
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

	    @GetMapping("/getBids/{userId}/{auctionId}")
	    public ResponseEntity<List<Bid>> getBidsByUserAndAuction(@PathVariable("userId") int userId,
	                                                             @PathVariable("auctionId") int auctionId) {
	    	
	    	LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
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
	    	LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
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
	    	LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
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

	   
	    @PostMapping("/auto-bid/setup")
		@Transactional
		public ResponseEntity<?> setupAutoBid(@RequestBody AutomaticBidReq abr) {
		    log.info("AutoBid Request: {}", abr);

		    Users user = usersRepository.findById(abr.getUserId()).orElse(null);
		    Auction auction = auctionService.getAuctionById(abr.getAuctionId());

		    if (user == null || auction == null) {
		        return ResponseEntity.badRequest().body("User or Auction not found");
		    }

		    if (abr.getMaxAmt() <= 0 || abr.getRiseAmt() <= 0) {
		        return ResponseEntity.badRequest().body("Max amount and rise amount must be positive");
		    }

				 Optional<AutoBidConfig> existing = autoBidConfigRepository.findByUserAndAuction(user, auction);
				if (existing.isPresent()) {
				    return ResponseEntity.badRequest().body("Auto-bid already set for this auction");
				}

		    // Save the AutoBidConfig
		    AutoBidConfig config = new AutoBidConfig();
		    config.setUser(user);
		    config.setAuction(auction);
		    config.setMaxAmount(abr.getMaxAmt());
		    config.setRiseAmount(abr.getRiseAmt());
		 AutoBidConfig n=    autoBidConfigRepository.save(config);
		    double currentPrice =  bidService.findHighestBidForAuction(auction.getId()) .orElse(auction.getStartingPrice());

		    // Place first AUTO bid with base price
		    Bid baseBid = new Bid();
		    baseBid.setUser(user);
		    baseBid.setAuction(auction);
		    baseBid.setBidAmount(currentPrice + abr.getRiseAmt());
		    baseBid.setBidTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime());
		    baseBid.setBidStatus("AUTO");

		    bidService.saveBid(baseBid);
		    log.info("Base AUTO bid placed by user {} for auction {} with amount {}", user.getId(), auction.getId(), auction.getStartingPrice());

		    // Simulate remaining auto-bids (2 more max per user)
		    autoBidService.processAutoBids(auction, user, auction.getStartingPrice());

		    return ResponseEntity.ok(n);
		}
	    
	    //update auto bid 
	    @PostMapping("/update/auto-bid")
	    public ResponseEntity<?> updateAutoBidding(@RequestBody UpdateAutoBidReq updateAutoBid) {
	        Integer userId = updateAutoBid.getUserId();
	        Integer auctionId = updateAutoBid.getAuctionId();
	        Double maxAmt = updateAutoBid.getMaxAmt();
	        Double riseAmt = updateAutoBid.getRiseAmt();

	        // Basic validation
	        if (maxAmt == null || riseAmt == null || maxAmt <= 0 || riseAmt <= 0) {
	            return ResponseEntity.badRequest().body("Invalid max or rise amount. Values must be > 0.");
	        }

	        Optional<AutoBidConfig> abc = autoBidConfigRepository.findByUserIdAndAuctionId(userId, auctionId);

	        if (abc.isPresent()) {
	            AutoBidConfig config = abc.get();
	            config.setMaxAmount(maxAmt);
	            config.setRiseAmount(riseAmt);

	            AutoBidConfig updatedConfig = autoBidConfigRepository.save(config);
	            return ResponseEntity.ok(updatedConfig);
	        } else {
	            return ResponseEntity
	                    .status(HttpStatus.NOT_FOUND)
	                    .body("Auto-bid configuration not found for userId: " + userId + " and auctionId: " + auctionId);
	        }
	    }

	    @GetMapping("/autoBids")
	    public ResponseEntity<List<AutoBidConfig>> getAllAutoBids() {
	        List<AutoBidConfig> autoBids = autoBidConfigRepository.findAll();
	        return new ResponseEntity<>(autoBids, HttpStatus.OK);
	    }
	    

}



