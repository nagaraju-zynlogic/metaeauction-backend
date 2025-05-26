package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AutoBidConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Users user;

    @ManyToOne
    private Auction auction;

    private Double maxAmount;
    private Double riseAmount;
}
