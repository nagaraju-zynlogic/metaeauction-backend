package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Auction;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	
}
