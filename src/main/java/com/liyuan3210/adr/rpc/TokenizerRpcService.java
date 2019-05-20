package com.liyuan3210.adr.rpc;

import com.liyuan3210.adr.util.KnownChannelException;
import com.liyuan3210.adr.dto.AreaDetailDto;
import com.liyuan3210.adr.dto.AreaDto;

public interface TokenizerRpcService {

	/**
	 * 智能识别地址
	 * @param text
	 * @return
	 */
	AreaDto segmentation(String text) throws KnownChannelException;
	
	/**
	 * 智能识别地址，返回省市区code
	 * @param text
	 * @return
	 * @throws KnownChannelException
	 */
	AreaDetailDto segmentationDetail(String text) throws KnownChannelException;

}
