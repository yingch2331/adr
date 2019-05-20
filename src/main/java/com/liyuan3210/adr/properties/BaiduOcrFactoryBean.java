//package com.liyuan3210.adr.properties;
//
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.baidu.aip.ocr.AipOcr;
//
//import com.liyuan3210.adr.properties.BaiduOcrFactoryBean.BaiduOcrClient;
//
//@Component
//public class BaiduOcrFactoryBean implements FactoryBean<BaiduOcrClient> {
//
//	@Autowired
//	private BaiduOcrSecurityKey baiduOcrSecurityKey;
//	
//	@Override
//	public BaiduOcrClient getObject() throws Exception {
//		return new BaiduOcrClient(baiduOcrSecurityKey);
//	}
//
//	@Override
//	public Class<?> getObjectType() {
//		return BaiduOcrClient.class;
//	}
//
//	@Override
//	public boolean isSingleton() {
//		return true;
//	}
//	
//	public static class BaiduOcrClient extends AipOcr {
//		
//		private BaiduOcrSecurityKey baiduOcrSecurityKey; 
//		
//		private BaiduOcrClient(BaiduOcrSecurityKey baiduOcrSecurityKey) {
//			super(baiduOcrSecurityKey.getAppid(), baiduOcrSecurityKey.getAk(), baiduOcrSecurityKey.getSk());
//			this.baiduOcrSecurityKey = baiduOcrSecurityKey;
//			init();
//		}
//		
//		private void init() {
//			BaiduOcrSecurityKey instance = this.baiduOcrSecurityKey.getInstance() == null ? this.baiduOcrSecurityKey : this.baiduOcrSecurityKey.getInstance();
//			// 重置密钥
//			this.appId = instance.getAppid();
//			this.aipKey = instance.getAk();
//			this.aipToken = instance.getSk();
//			 // 可选：设置网络连接参数
//			this.setConnectionTimeoutInMillis(10000);
//			this.setSocketTimeoutInMillis(60000);
//		}
//		
//		public void fetchAccount() {
//			BaiduOcrSecurityKey instance = this.baiduOcrSecurityKey.getInstance();
//			if (instance != null) {
//				BaiduOcrSecurityKey current = instance.getNext();
//				while (current != instance) {
//					if (this.appId.equals(current.getAppid()) && this.aipKey.equals(current.getAk()) && this.aipToken.equals(current.getSk())) {
//						BaiduOcrSecurityKey fetchSecurityKey = current.getNext();
//						// 重置密钥
//						this.appId = fetchSecurityKey.getAppid();
//						this.aipKey = fetchSecurityKey.getAk();
//						this.aipToken = fetchSecurityKey.getSk();
//						break;
//					}
//					current = current.getNext();
//				}
//			} else {
//				// 重置密钥
//				this.appId = this.baiduOcrSecurityKey.getAppid();
//				this.aipKey = this.baiduOcrSecurityKey.getAk();
//				this.aipToken = this.baiduOcrSecurityKey.getSk();
//			}
//		}
//	}
//}
