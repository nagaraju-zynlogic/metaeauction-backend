package com.example.demo.controller;

import java.time.LocalDateTime;
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
	private  LocalDateTime NOW = auctionService.getIndianTime().now();
	
	// http://localhost:8080/auction/auctions
	// find all auctions
	@GetMapping("/auctions")
	public List<Auction> getAllAuctions() {
		updateAuctionStatus();
		return auctionService.getAllAuctions();
	}
	
	
	// find all upcoming auctions
	@GetMapping("/upcomingAuctions")
	public List<Auction> getUpcomingAuctions() {
		updateAuctionStatus();
		return auctionService.getUpcomingAuctions();
	}
	// find auction by id
	@GetMapping("/auctionBy/{id}")
	public Auction getAuctionById(@PathVariable("id") Integer auctionId) {
		updateAuctionStatus();
		return auctionService.getAuctionById(auctionId);
	}
	
	// find auctions currently running
	@GetMapping("/runningAuctions")
	public List<Auction> getRunningAuctions() {
		updateAuctionStatus();
		
		return auctionService.getAllAuctions().stream()
				.filter(auction -> auction.getStartDate().isBefore(NOW)
						&& auction.getEndDate().isAfter(NOW))
				.toList();
		
	}
	
	
	
	// find auctions ended
	@GetMapping("/endedAuctions")
	public List<Auction> getEndedAuctions() {
		updateAuctionStatus();
		// filter auctions that have ended with time
		
		return auctionService.getAllAuctions().stream()
				.filter(auction -> auction.getEndDate().isBefore(NOW))
				.toList();
		
	}
	
	// update auction status based on start and end date time 
	@GetMapping("/updateAuctionStatus")
	public void updateAuctionStatus() {
		 auctionService.updateAuctionStatus();
	}
	
	


}
