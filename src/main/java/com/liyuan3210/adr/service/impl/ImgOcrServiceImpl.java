package com.liyuan3210.adr.service.impl;

import java.util.HashMap;
import org.springframework.stereotype.Service;
import com.liyuan3210.adr.service.ImgOcrService;

@Service
public class ImgOcrServiceImpl implements ImgOcrService {

//	@Autowired
//	private BaiduOcrClient baiduOcrClient;
	
	private static final HashMap<String, String> ORC_OPTIONS = new HashMap<String, String>();
	
	static {
		ORC_OPTIONS.put("language_type", "CHN_ENG");
		ORC_OPTIONS.put("detect_direction", "true");
		ORC_OPTIONS.put("detect_language", "true");
		ORC_OPTIONS.put("probability", "true");
	}
	
	
//	@Override
//	public String ocr(byte[] img) {
//		String words = null;
//		try {
//			JSONObject jsonObject = baiduOcrClient.basicGeneral(img, ORC_OPTIONS);
//			String json = jsonObject.toString();
//			BaiduOcrDto baiduOcrDto = JSON.parseObject(json, BaiduOcrDto.class);
//			List<WordsResult> wordsResults = baiduOcrDto.getWords_result();
//			if (!CollectionUtils.isEmpty(wordsResults)) {
//				words = "";
//				// String separator = System.getProperty("line.separator");
//				for (WordsResult wordsResult : wordsResults) {
//					words += wordsResult.getWords();
//				}
//				words.trim();
//			}
//		} catch (Exception e) {
//			throw new KnownChannelException("500", "百度OCR文字识别接口异常", e);
//		}
//	    return words;
//	}

}
