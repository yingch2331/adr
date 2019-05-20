package com.liyuan3210.adr.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AreaDetailDto extends AreaDto implements Serializable {

	private static final long serialVersionUID = 5829494553064811230L;

	private String provinceCode;
	private String cityCode;
	private String countyCode;
	
	public AreaDetailDto() {
		this.provinceCode = "";
		this.cityCode = "";
		this.countyCode = "";
	}
	
}
