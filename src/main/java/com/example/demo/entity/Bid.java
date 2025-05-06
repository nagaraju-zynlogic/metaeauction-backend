package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "bids")
@AllArgsConstructor
@NoArgsConstructor
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    private String bidStatus = "PLACED";
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // <-- this will be a foreign key now
    private Users user;
    private double bidAmount;

    private LocalDateTime bidTime;
    
    public Bid(Users user, Auction auction, double amount, LocalDateTime time, String status) {
        this.user = user;
        this.auction = auction;
        this.bidAmount = amount;
        this.bidTime = time;
        this.bidStatus = status;
    }

}
