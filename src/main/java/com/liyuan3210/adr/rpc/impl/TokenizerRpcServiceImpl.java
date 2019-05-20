package com.liyuan3210.adr.rpc.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.liyuan3210.adr.util.KnownChannelException;
import com.liyuan3210.adr.cache.AreaCache;
import com.liyuan3210.adr.cache.AreaCache.RegionId;
import com.liyuan3210.adr.dto.AreaDetailDto;
import com.liyuan3210.adr.dto.AreaDto;
import com.liyuan3210.adr.rpc.TokenizerRpcService;
import com.liyuan3210.adr.service.ImgOcrService;
import com.liyuan3210.adr.service.TokenizerService;

public class TokenizerRpcServiceImpl implements TokenizerRpcService {

	@Autowired
	private TokenizerService tokenizerService;
	
	@Autowired
	private AreaCache areaCache;
	
	@Override
	public AreaDto segmentation(String text) throws KnownChannelException {
		return tokenizerService.segmentation(text);
	}

	@Override
	public AreaDetailDto segmentationDetail(String text) throws KnownChannelException {
		AreaDetailDto areaDetailDto = null;
		AreaDto areaDto = tokenizerService.segmentation(text);
		if (areaDto != null) {
			areaDetailDto = new AreaDetailDto();
			BeanUtils.copyProperties(areaDto, areaDetailDto);
			
			String key = areaDto.getProvince() + areaDto.getCity() + areaDto.getCounty();
			RegionId regionId = areaCache.getRegionIdMap().get(key);
			if (regionId != null) {
				areaDetailDto.setProvinceCode(regionId.getProvinceId());
				areaDetailDto.setCityCode(regionId.getCityId());
				areaDetailDto.setCountyCode(regionId.getCountyId());
			}
		}
		return areaDetailDto;
	}
	
}
