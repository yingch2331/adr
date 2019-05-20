package com.liyuan3210.adr.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.liyuan3210.adr.dto.AddressResponse;
import com.liyuan3210.adr.dto.AreaDto;
import com.liyuan3210.adr.service.TokenizerService;

/**
 * @author Administrator
 */
@RestController
public class AddressController {
	@Autowired
	private TokenizerService tokenizerService;
	
	/**
	 * 地址解析
	 * @param request
	 * @param address
	 * @return
	 */
	@CrossOrigin({"http://www.liyuan3210.com","http://127.0.0.1:8080",
		"http://proxy.liyuan3210.com:10802","https://www.liyuan3210.com"})
	@RequestMapping(value = "/resolveAddress", method = RequestMethod.POST)  
    public String resolveAddress(HttpServletRequest request,String address){
		System.out.println("###系统接收参数:"+address);
		AddressResponse addressResponse = new AddressResponse();
		AreaDto areaDto = tokenizerService.segmentation(address);
		if(null != areaDto){
			addressResponse.setCode("0000");
			addressResponse.setMessage("success");
			addressResponse.setData(areaDto);
		}else{
			addressResponse.setCode("0001");
			addressResponse.setMessage("fail");
		}
		return JSON.toJSONString(addressResponse);
    }
	
	
}