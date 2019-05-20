package com.liyuan3210.adr.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AreaDto implements Serializable {

	private static final long serialVersionUID = 4053536626036797560L;
	
	private String province;
	private String city;
	private String county;
	private String detail;
	private String name;
	private String phone;
	private String sure;
	
	public AreaDto() {
		this.province = "";
		this.city = "";
		this.county = "";
		this.detail = "";
		this.name = "";
		this.phone = "";
	}
	
	/**
	 * 找到省市区三级地址
	 * @return
	 */
	public boolean findArea() {
		return StringUtils.hasText(province) && StringUtils.hasText(city) && StringUtils.hasText(county);
	}
	
}
