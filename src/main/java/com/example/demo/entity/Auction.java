package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.example.demo.statusEnum.AuctionStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

@Table(name = "auctions")
@Data
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime startDate;
	// input format: "2025-04-25T08:33:09.614Z"	
    
    private LocalDateTime endDate;

    private double startingPrice;

   // private double currentPrice;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status ;

    private Integer highestBidderId; // FK to users (nullable)
    
    private Double highestBidAmount; // Nullable
    
    private Integer bidId; // FK to bids (nullable)

    private Integer createdByAdminId; // FK to admins

    private LocalDateTime createdAt;
    
    // Default constructor

    @ManyToOne
    @JoinColumn(name = "user_id")  // FK column in auctions table
    @JsonBackReference
    private Users user;
    
    @PrePersist
    @PreUpdate
    public void updateAuctionStatus() {
    	
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        if (startDate != null && startDate.isAfter(now)) {
            this.status = AuctionStatus.UPCOMING;
        } else if (endDate != null && endDate.isBefore(now)) {
            this.status = AuctionStatus.COMPLETED;
        } else {
            this.status = AuctionStatus.ACTIVE;
        }
    }
    
    
}
