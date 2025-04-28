package com.example.demo.dto;

import lombok.Data;

@Data
public class AcceptBidDTO {
	
	private Integer auctionId;
	private Integer bidId;
	private Integer userId;
	private Double bidAmount;

}
