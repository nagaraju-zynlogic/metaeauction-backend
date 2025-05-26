package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.AutoBidConfigRepository;
import com.example.demo.Repository.BidRepository;
import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.AutoBidConfig;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoBidService {

    private final AutoBidConfigRepository autoBidRepo;
    private final BidService bidService;
    private final BidRepository bidRepository;
    private final userRepository userRepository;
    

    /**
     * Executes auto-bidding logic after a manual bid or auto-bid setup.
     *
     * @param auction          The auction where bidding happens
     * @param latestTopBidder  The user who placed the latest highest bid (can be null during setup)
     * @param currentBid       The current highest bid amount (start from base price during setup)
     */
//    public void processAutoBids(Auction auction, Users latestTopBidder, double currentBid) {
//        List<AutoBidConfig> configs = autoBidRepo.findByAuction(auction);
//
//        if (configs == null || configs.size() < 2) {
//            log.info("Not enough auto-bid configs to simulate competition.");
//            return;
//        }
//        
//      
//        
//        double currentPrice = currentBid;
//        Users currentTopBidder = latestTopBidder;
//
//        Map<Users, Double> maxMap = new HashMap<>();
//        Map<Users, Double> riseMap = new HashMap<>();
//        Map<Users, Double> lastBidMap = new HashMap<>();
//
//        for (AutoBidConfig config : configs) {
//            maxMap.put(config.getUser(), config.getMaxAmount());
//            riseMap.put(config.getUser(), config.getRiseAmount());
//            lastBidMap.put(config.getUser(), currentPrice);
//        }
//       
//        List<Bid> transactionHistory = new ArrayList<>();
//        Set<Users> exhaustedUsers = new HashSet<>();
//        boolean bidPlaced;
//
//        do {
//            bidPlaced = false;
//
//            for (AutoBidConfig config : configs) {
//                Users user = config.getUser();
//                if (user.equals(currentTopBidder) || exhaustedUsers.contains(user)) continue;
//                
//                double rise = riseMap.get(user);
//                double max = maxMap.get(user);
//                double nextBid = currentPrice + rise;
//
//                if (nextBid <= max) {
//                    currentPrice = nextBid;
//                    currentTopBidder = user;
//                    lastBidMap.put(user, currentPrice);
//
//                    // Record this bid as a transaction
//                    Bid bid = new Bid();
//                    bid.setUser(user);
//                    bid.setAuction(auction);
//                    bid.setBidAmount(currentPrice);
//                    bid.setBidTime(LocalDateTime.now());
//                    bid.setBidStatus("AUTO");
//
//                    transactionHistory.add(bid);
//                    bidPlaced = true;
//                    
//                    // Maintain only last 3 transactions
//                    if (transactionHistory.size() >  configs.size()*3) {
//                        transactionHistory.remove(0);
//                    }
//                } else {
//                    exhaustedUsers.add(user);
//                }
//            }
//
//        } while (bidPlaced);
//
//        // Save only last 3 bids of cold war
//        if (!transactionHistory.isEmpty()) {
//            bidRepository.saveAll(transactionHistory);
//            log.info("Cold war bidding complete. Final 3 bids saved.");
//        } else {
//            log.info("No auto-bids were placed in cold war.");
//        }
//    }
    
    
    
    public void processAutoBids(Auction auction, Users latestTopBidder, double currentBid) {
        List<AutoBidConfig> configs = autoBidRepo.findByAuction(auction);

        if (configs == null || configs.isEmpty()) {
            log.info("No auto-bid configs found.");
            return;
        }

        double currentPrice = currentBid;
        Users currentTopBidder = latestTopBidder;

        Map<Users, Double> maxMap = new HashMap<>();
        Map<Users, Double> riseMap = new HashMap<>();

        for (AutoBidConfig config : configs) {
            maxMap.put(config.getUser(), config.getMaxAmount());
            riseMap.put(config.getUser(), config.getRiseAmount());
        }

        List<Bid> transactionHistory = new ArrayList<>();
        Set<Users> exhaustedUsers = new HashSet<>();
        boolean bidPlaced;

        do {
            bidPlaced = false;

            for (AutoBidConfig config : configs) {
                Users user = config.getUser();
                if (exhaustedUsers.contains(user)) continue;

                double rise = riseMap.get(user);
                double max = maxMap.get(user);
                double nextBid = currentPrice + rise;

                // Don't allow a user to outbid themselves UNLESS someone else is top
                if (user.equals(currentTopBidder)) {
                    if (nextBid > max) {
                        exhaustedUsers.add(user);
                    }
                    continue;
                }

                if (nextBid <= max) {
                    currentPrice = nextBid;
                    currentTopBidder = user;

                    Bid bid = new Bid();
                    bid.setUser(user);
                    bid.setAuction(auction);
                    bid.setBidAmount(currentPrice);
                    bid.setBidTime(LocalDateTime.now());
                    bid.setBidStatus("AUTO");

                    transactionHistory.add(bid);
                    bidPlaced = true;

                    if (transactionHistory.size() > configs.size() * 3) {
                        transactionHistory.remove(0);
                    }
                } else {
                    exhaustedUsers.add(user);
                }
            }

        } while (bidPlaced);

        if (!transactionHistory.isEmpty()) {
            bidRepository.saveAll(transactionHistory);
            log.info("Cold war bidding complete. Final bids saved.");
        } else {
            log.info("No auto-bids were placed in cold war.");
        }
    }


    
   

    public void simulateFinalAutoBids(Auction auction) {
        List<AutoBidConfig> configs = autoBidRepo.findByAuction(auction);

        if (configs.isEmpty()) return; // If no auto-bid configurations, exit

        double currentPrice = auction.getStartingPrice();
        Users finalBidder = null;
        double finalAmount = currentPrice;

        Map<Users, Double> maxMap = new HashMap<>();
        Map<Users, Double> riseMap = new HashMap<>();

        // Set up the user's max and rise amounts
        for (AutoBidConfig config : configs) {
            maxMap.put(config.getUser(), config.getMaxAmount());
            riseMap.put(config.getUser(), config.getRiseAmount());
        }

        boolean bidPlaced;
        Set<Users> exhaustedUsers = new HashSet<>();

        // Simulate the bidding process
        do {
            bidPlaced = false;
            for (AutoBidConfig config : configs) {
                Users user = config.getUser();

                // Skip if user has exhausted their bidding limit
                if (exhaustedUsers.contains(user)) continue;

                double max = maxMap.get(user);
                double rise = riseMap.get(user);

                double nextBid = currentPrice + rise;

                // If the next bid is within the user's max bid, place the bid
                if (nextBid <= max) {
                    currentPrice = nextBid; // Update the current price to the next bid
                    finalBidder = user;     // Update the final bidder
                    finalAmount = nextBid;  // Update the final amount
                    bidPlaced = true;       // Mark as bid placed
                } else {
                    exhaustedUsers.add(user); // Mark user as exhausted if they can't bid anymore
                }
            }
        } while (bidPlaced); // Continue while bids are still being placed

        // Save only the final bid placed
        if (finalBidder != null) {
            Bid finalBid = new Bid();
            finalBid.setAuction(auction);
            finalBid.setUser(finalBidder);
            finalBid.setBidAmount(finalAmount);
            finalBid.setBidStatus("AUTO");
            finalBid.setBidTime(LocalDateTime.now());

            bidRepository.save(finalBid); // Save the final bid

            log.info("Final auto-bid placed by user {} for amount {}", finalBidder.getId(), finalAmount);
        }
    }
}


































	/*    public void processAutoBids(Auction auction, Users latestTopBidder, double manualBidAmount) {
	    List<AutoBidConfig> configs = autoBidRepo.findByAuction(auction);
	
	    if (configs == null || configs.isEmpty()) {
	        log.info("No auto-bid configs found.");
	        return;
	    }
	
	    double currentPrice = Math.max(manualBidAmount, bidRepository.findHighestBidForAuction(auction.getId()).orElse(auction.getStartingPrice()));
	    Users currentTopBidder = latestTopBidder;
	
	    Map<Integer, Integer> bidCounts = new HashMap<>();
	    Map<Integer, Double> lastBids = new HashMap<>();
	    List<Bid> autoBidsToSave = new ArrayList<>();
	
	    boolean biddingContinues;
	
	    do {
	        biddingContinues = false;
	
	        for (AutoBidConfig config : configs) {
	            Users user = config.getUser();
	            int userId = user.getId();
	
	            if (user.equals(currentTopBidder)) continue;
	
	            int placed = bidCounts.getOrDefault(userId, 0);
	            if (placed >= 3) continue;
	
	            // Next bid should always be currentPrice + riseAmount
	            double nextBid = currentPrice + config.getRiseAmount();
	
	            // Validate the bid
	            if (nextBid <= config.getMaxAmount()) {
	                currentPrice = nextBid;
	                currentTopBidder = user;
	
	                Bid bid = new Bid();
	                bid.setAuction(auction);
	                bid.setUser(user);
	                bid.setBidAmount(nextBid);
	                bid.setBidTime(LocalDateTime.now());
	                bid.setBidStatus("AUTO");
	
	                autoBidsToSave.add(bid);
	                bidCounts.put(userId, placed + 1);
	                lastBids.put(userId, nextBid);
	                biddingContinues = true;
	            }
	        }
	
	    } while (biddingContinues);
	
	    if (!autoBidsToSave.isEmpty()) {
	        bidRepository.saveAll(autoBidsToSave);
	        log.info("Saved {} auto-bids (up to 3 per user).", autoBidsToSave.size());
	    } else {
	        log.info("No auto-bids were eligible.");
	    }
	}
	*/

   
	/*public void processAutoBids(Auction auction, Users latestTopBidder, double currentBid) {
	    List<AutoBidConfig> configs = autoBidRepo.findByAuction(auction);
	
	    if (configs == null || configs.isEmpty()) {
	        log.info("No auto-bid configs found.");
	        return;
	    }
	
	    double currentPrice = currentBid;
	    Users currentTopBidder = latestTopBidder;
	
	    Map<Integer, Integer> bidCounts = new HashMap<>();
	    Map<Integer, Double> lastBids = new HashMap<>();
	    List<Bid> autoBidsToSave = new ArrayList<>();
	
	    boolean biddingContinues;
	
	    do {
	        biddingContinues = false;
	
	        for (AutoBidConfig config : configs) {
	            Users user = config.getUser();
	            int userId = user.getId();
	
	            if (user.equals(currentTopBidder)) continue; // skip current top bidder
	
	            int placed = bidCounts.getOrDefault(userId, 0);
	            if (placed >= 3) continue; // limit to 3 bids per user
	
	            double lastBid = lastBids.getOrDefault(userId, currentPrice);
	            double nextBid = lastBid + config.getRiseAmount();
	
	            if (nextBid <= config.getMaxAmount() && nextBid > currentPrice) {
	                currentPrice = nextBid;
	                currentTopBidder = user;
	
	                // create and store the bid
	                Bid bid = new Bid();
	                bid.setAuction(auction);
	                bid.setUser(user);
	                bid.setBidAmount(nextBid);
	                bid.setBidTime(LocalDateTime.now());
	                bid.setBidStatus("AUTO");
	
	                autoBidsToSave.add(bid);
	                bidCounts.put(userId, placed + 1);
	                lastBids.put(userId, nextBid);
	                biddingContinues = true;
	            }
	        }
	
	    } while (biddingContinues);
	
	    if (!autoBidsToSave.isEmpty()) {
	        bidRepository.saveAll(autoBidsToSave);
	        log.info("Saved {} auto-bids (up to 3 per user).", autoBidsToSave.size());
	    } else {
	        log.info("No auto-bids were eligible.");
	    }
	}*/


