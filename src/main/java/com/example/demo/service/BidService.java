package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.BidRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BidService {
	
	@Autowired
	private BidRepository bidRepository;
	

	
LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();	
	public Bid saveBid(Bid bid) {
		// Save the bid to the database
		Bid savedBid = bidRepository.save(bid);
		return savedBid;
		
	}

	public List<Bid> getBidsByAuction(Auction auction) {
		// Retrieve all bids for a specific auction
		List<Bid> bids = bidRepository.findAllByAuction(auction);
		return bids;
	}

	public List<Bid> getBidsByUserAndAuction(Users user, Auction auction) {
		// Retrieve all bids for a specific user and auction
		List<Bid> bids = bidRepository.findAllByUserAndAuction(user, auction);
		return bids;
	
	}

	public Bid getBidById(Integer bidId) {
		// Retrieve a specific bid by its ID
		Bid bid = bidRepository.findById(bidId).orElse(null);
		return bid;
	}

	public void saveAllBids(List<Bid> bids) {

		// Save a list of bids to the database
		bidRepository.saveAll(bids);
	}
	
	// based on auction start time update action details for auto bid
	public void updateBidStatusAndTime() {
		LocalDateTime NOW = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
		List<Bid> allBids = bidRepository.findAll();

	    for (Bid bid : allBids) {
	        Auction auction = bid.getAuction();

	        // Check if the auction has started
	        if (auction.getStartDate().isBefore(NOW) && bid.getBidStatus().equals("SCEHDULED") ) {
	        	bid.setBidStatus("PLACED");
	        	bid.setBidTime(auction.getStartDate());
	        }
	    }

	    // Save the updated bids back to the database
	    bidRepository.saveAll(allBids);





	}

	public Optional<Double> findHighestBidForAuction(Integer id) {
		
		return bidRepository.findHighestBidForAuction(id);
	}

}
