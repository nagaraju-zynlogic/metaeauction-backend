package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Auction;
import com.example.demo.entity.Bid;
import com.example.demo.entity.Users;

public interface BidRepository extends JpaRepository<Bid,Integer> {

	 List<Bid> findAllByAuction(Auction auction);
	 List<Bid> findAllByUserAndAuction(Users user, Auction auction);
	 List<Bid> findAllByUser(Users user);
	 Optional<Bid> findTopByAuctionOrderByBidAmountDesc(Auction auction);
	 @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.auction.id = :auctionId")
	 Optional<Double> findHighestBidForAuction(@Param("auctionId") int auctionId);

	
	
	

}
