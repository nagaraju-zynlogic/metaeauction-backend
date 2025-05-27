package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Auction;
import com.example.demo.entity.Users;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	List<Auction> findAllByUser(Users user);
	//  private Integer highestBidderId; 
	 @Query("SELECT a FROM Auction a WHERE a.highestBidderId = :userId")
	   List<Auction> findAuctionsWonByUser(@Param("userId") Integer userId);
	 @Query(value = "SELECT * FROM auctions", nativeQuery = true)
	 List<Auction> findAllIncludingInactive();
	 @Query(value = "SELECT * FROM auctions WHERE id = :id", nativeQuery = true)
	 Auction findByIdIncludingInactive(@Param("id") Integer id);

}
