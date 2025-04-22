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

	public Auction getAuctionById(Integer auctionId) {
		
		Auction auction = auctionRepository.findById(auctionId).orElse(null);
		if (auction != null) {
			return auction;
		}
		
		
		return null;
	}

	public List<Auction> getAuctionByUserId(Users user) {
		List<Auction> auctions = auctionRepository.findAllByUser(user);
		
		return auctions;
	}

	public Auction saveAuction(Auction auction) {
		if (auction != null) {
			return auctionRepository.save(auction);
		}
		
		return null;
	}

	public void deleteAuction(Integer id) {
		if (id != null) {
			auctionRepository.deleteById(id);
		}
		
		
	}

	
}
