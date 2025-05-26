package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdateAutoBidReq {
	private Integer userId ;
	private Integer auctionId;
	private Double maxAmt ;
	private Double riseAmt;
}
