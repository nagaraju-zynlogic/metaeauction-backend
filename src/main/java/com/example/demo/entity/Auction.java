package com.example.demo.entity;

import java.time.LocalDateTime;

import com.example.demo.statusEnum.AuctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "auctions")

public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double startingPrice;

   // private double currentPrice;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status ;

    private Integer highestBidderId; // FK to users (nullable)

    private Integer createdByAdminId; // FK to admins

    private LocalDateTime createdAt;
    
    // Default constructor
    
    
    @ManyToOne
    @JoinColumn(name = "user_id")  // FK column in auctions table
    private Users user;
    
    
}
