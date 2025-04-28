package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.BidRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;

@Service
public class BidService {
	@Autowired
	private BidRepository bidRepository;

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

}
