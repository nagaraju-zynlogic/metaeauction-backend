package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Auction;
import com.example.demo.service.AuctionService;

@RestController
@RequestMapping("/auction")
public class AuctionController {
	@Autowired
	private AuctionService auctionService;
	
	// find all auctions
	@GetMapping("/auctions")
	public List<Auction> getAllAuctions() {
		return auctionService.getAllAuctions();
	}
	
	
	// find all upcoming auctions
	@GetMapping("/upcomingAuctions")
	public List<Auction> getUpcomingAuctions() {
		return auctionService.getUpcomingAuctions();
	}
	// find auction by id
	@GetMapping("/auctionBy/{id}")
	public Auction getAuctionById(@PathVariable("id") Integer auctionId) {
		return auctionService.getAuctionById(auctionId);
	}
	
	// find auctions currently running
	@GetMapping("/runningAuctions")
	public List<Auction> getRunningAuctions() {
		return auctionService.getAllAuctions().stream()
				.filter(auction -> auction.getStartDate().isBefore(java.time.LocalDateTime.now())
						&& auction.getEndDate().isAfter(java.time.LocalDateTime.now()))
				.toList();
		
		
	}
	


}
