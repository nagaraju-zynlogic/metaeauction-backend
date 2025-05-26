package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Auction;
import com.example.demo.entity.AutoBidConfig;
import com.example.demo.entity.Users;

public interface AutoBidConfigRepository extends JpaRepository<AutoBidConfig, Integer> {

    Optional<AutoBidConfig> findByUserAndAuction(Users user, Auction auction);

    List<AutoBidConfig> findByAuction(Auction auction);
    
    Optional<AutoBidConfig> findByUserIdAndAuctionId(int userId, int auctionId);

    
}
