package com.liyuan3210.adr.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressResponse {
	private String code;
	private String message;
	private AreaDto data;
}
