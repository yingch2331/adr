package com.liyuan3210.adr.service;

import com.liyuan3210.adr.dto.AreaDto;

public interface TokenizerService {

	AreaDto segmentation(String text);
	
}
