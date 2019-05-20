//package com.liyuan3210.adr.test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//
//import org.json.JSONObject;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import com.liyuan3210.adr.bootstrap.Main;
//import com.liyuan3210.adr.properties.BaiduOcrFactoryBean.BaiduOcrClient;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Main.class)
//@WebAppConfiguration
//public class BaiduOcrTest {
//
//	@Autowired
//	private BaiduOcrClient baiduOcrClient;
//	
//	@Test
//	public void test() {
//		// 传入可选参数调用接口
//		HashMap<String, String> options = new HashMap<String, String>();
//	    options.put("language_type", "CHN_ENG");
//	    options.put("detect_direction", "true");
//	    options.put("detect_language", "true");
//	    options.put("probability", "true");
//		
//		InputStream inputStream = null;
//		try {
//			inputStream = new FileInputStream(new File("D:/ocr.png"));
//			byte[] img = new byte[inputStream.available()];
//			inputStream.read(img);
//			JSONObject jsonObject = baiduOcrClient.basicGeneral(img, options);
//		    System.out.println(jsonObject.toString(2));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (inputStream != null) {
//				try {
//					inputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	
//}
