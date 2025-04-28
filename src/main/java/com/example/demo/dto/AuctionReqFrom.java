package com.example.demo.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.demo.statusEnum.AuctionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Data
public class AuctionReqFrom {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    private String name;

	    @Column(columnDefinition = "TEXT")
	    private String description;

	    // Use OffsetDateTime for ISO 8601 timestamps like "2025-04-25T08:33:09.614Z"
	    private OffsetDateTime startDate;
	    private OffsetDateTime endDate;

	    private double startingPrice;

	    @Enumerated(EnumType.STRING)
	    private AuctionStatus status;

	    private Integer createdByAdminId = 1;

	    private LocalDateTime createdAt = LocalDateTime.now();

	    // Automatically set status before saving or updating
	    @PrePersist
	    @PreUpdate
	    public void updateAuctionStatus() {
	        OffsetDateTime now = OffsetDateTime.now();
	        if (startDate != null && startDate.isAfter(now)) {
	            this.status = AuctionStatus.UPCOMING;
	        } else if (endDate != null && endDate.isBefore(now)) {
	            this.status = AuctionStatus.COMPLETED;
	        } else {
	            this.status = AuctionStatus.ACTIVE;
	        }
	    }
    
    
   

}
