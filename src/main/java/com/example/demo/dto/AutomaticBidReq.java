package com.example.demo.dto;

import lombok.Data;

@Data
public class AutomaticBidReq {

	private Integer userId;
	private Integer auctionId;
	private Double riseAmt;
	private Double maxAmt;
	
}
