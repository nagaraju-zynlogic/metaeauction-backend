package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	


}
