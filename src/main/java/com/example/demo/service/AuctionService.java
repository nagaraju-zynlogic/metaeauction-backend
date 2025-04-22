package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.AuctionRepository;
import com.example.demo.entity.Auction;
import com.example.demo.entity.Users;

@Service
public class AuctionService {
	@Autowired
	private AuctionRepository auctionRepository;

	public List<Auction> getAllAuctions() {
		List<Auction> auctions = auctionRepository.findAll();
		return auctions;
	}

	public List<Auction> getUpcomingAuctions() {
		List<Auction> auctions = auctionRepository.findAll();
		List<Auction> upcomingAuctions = auctions.stream()
				.filter(auction -> auction.getStartDate().isAfter(java.time.LocalDateTime.now()))
				.toList();
		
		return upcomingAuctions;
	}

	public Auction getAuctionById(int auctionId) {
		
		Auction auction = auctionRepository.findById(auctionId).orElse(null);
		if (auction != null) {
			return auction;
		}
		
		
		return null;
	}

	public List<Auction> getAuctionById(Users user) {
		List<Auction> auctions = auctionRepository.findAllByUser(user);
		
		return null;
	}

}
