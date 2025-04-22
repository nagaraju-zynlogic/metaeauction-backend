package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Auction;
import com.example.demo.entity.Users;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	List<Auction> findAllByUser(Users user);

}
