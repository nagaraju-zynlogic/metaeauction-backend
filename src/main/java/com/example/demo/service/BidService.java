package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.BidRepository;
import com.example.demo.entity.Bid;

@Service
public class BidService {
	@Autowired
	private BidRepository bidRepository;

	public Bid saveBid(Bid bid) {
		// Save the bid to the database
		Bid savedBid = bidRepository.save(bid);
		return savedBid;
		
	}

}
