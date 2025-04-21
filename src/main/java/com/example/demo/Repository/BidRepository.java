package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Bid;

public interface BidRepository extends JpaRepository<Bid,Integer> {
	// Custom query methods can be defined here if needed
	// For example, find bids by auction ID or user ID

}
