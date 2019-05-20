package com.liyuan3210.adr.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaiduMapDto {

	private Integer status;
	private String message;
	private List<Address> result;

	@Setter
	@Getter
	public static class Address {
		private String name;
		private String uid;
		private String city;
		private String district;
		private String business;
		private String cityid;
		private Location location;
	}
	
	@Setter
	@Getter
	public static class Location {
		private String lat;
		private String lng;
	}
}
