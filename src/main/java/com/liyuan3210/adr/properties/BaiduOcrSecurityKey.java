//package com.liyuan3210.adr.properties;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import com.alibaba.fastjson.JSON;
//import com.liyuan3210.adr.config.XxlConfClient;
//import com.liyuan3210.adr.config.listener.impl.KvRefreshXxlConfListener;
//import com.liyuan3210.adr.util.CircleLinkedList;
//import com.liyuan3210.adr.util.CircleNode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//@Setter
//@Getter
//@ToString
//@Component
//@ConfigurationProperties(prefix = "baidu.ocr")
//public class BaiduOcrSecurityKey extends CircleNode<BaiduOcrSecurityKey> implements KvRefreshXxlConfListener, InitializingBean {
//
//	private final static Logger LOGGER = LoggerFactory.getLogger(BaiduOcrSecurityKey.class);
//	
//	private final static String XXL_KEY = "baidu.ocr.secret";
//	
//	private String appid;
//	private String ak;
//	private String sk;
//	private String sign;
//	
//	private BaiduOcrSecurityKey instance;
//	
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		String value = XxlConfClient.get(XXL_KEY);
//		if (StringUtils.hasText(value)) {
//			instance = updateSecret(value);
//		}
//	}
//	
//	@Override
//	public void onChange(String key, String value) throws Exception {
//		instance = updateSecret(value);
//	}
//	
//	@Override
//	public String key() {
//		return XXL_KEY;
//	}
//	
//	private BaiduOcrSecurityKey updateSecret(String value) {
//		try {
//			List<BaiduOcrSecurityKey> securityKeys = JSON.parseArray(value, BaiduOcrSecurityKey.class);
//			if (!CollectionUtils.isEmpty(securityKeys)) {
//				CircleLinkedList<BaiduOcrSecurityKey> circleLinkedList = new CircleLinkedList<BaiduOcrSecurityKey>();
//				for (BaiduOcrSecurityKey securityKey : securityKeys) {
//					circleLinkedList.add(securityKey);
//				}
//				return circleLinkedList.getFirst();
//			}
//		} catch (Exception e) {
//			LOGGER.error("同步配置key={}，value={}失败", e);
//		}
//		return null;
//	}
//
//	public boolean equals(BaiduOcrSecurityKey baiduOcrSecurityKey) {
//		if (baiduOcrSecurityKey.ak.equals(this.ak) 
//				&& baiduOcrSecurityKey.sk.equals(this.sk)
//				&& baiduOcrSecurityKey.appid.equals(this.appid)) {
//			
//			return true;
//		}
//		return false;
//	}
//
//}
