package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;

public interface BidRepository extends JpaRepository<Bid,Integer> {

	 List<Bid> findAllByAuction(Auction auction);
	
	
	

}
