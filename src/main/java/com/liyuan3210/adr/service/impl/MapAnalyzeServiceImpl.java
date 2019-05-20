package com.liyuan3210.adr.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.liyuan3210.adr.cache.AreaCache;
import com.liyuan3210.adr.dto.AreaDto;
import com.liyuan3210.adr.dto.BaiduMapDto;
import com.liyuan3210.adr.service.TokenizerService;
import com.liyuan3210.adr.util.SnCal;

@Primary
@Service
public class MapAnalyzeServiceImpl implements TokenizerService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MapAnalyzeServiceImpl.class);
	
	private final static String BAIDU_URL = "http://api.map.baidu.com";
	
	private final static String BAIDU_MAP_URL = "http://api.map.baidu.com/place/v2/suggestion";
	
	private final static String SPECIAL_SYMBOL = "&&&";
	
	/**
	 * 百度地图天配额超限提醒文案
	 */
	private final static String BAIDU_MAP_OVER_LIMIT = "天配额超限，限制访问";
	
	/**
	 * 百度地图天配额超限状态
	 */
	private final static int BAIDU_MAP_OVER_LIMIT_STATUS = 302;
	
	@Resource(type = TextSimilarityServiceImpl.class)
	private TokenizerService tokenizerService;
	
	@Autowired
	private AreaCache areaCache;
	
	@Autowired
	private RestTemplate restTemplate;
	
//	@Autowired
//	private volatile BaiduSecurityKey baiduSecurityKey;
	
	/**
	 * 切换百度密钥同步锁
	 */
	private final Lock lock = new ReentrantLock();
	
	@Override
	public AreaDto segmentation(String text) {
		AreaDto areaDto = tokenizerService.segmentation(text);
//		// 如果省市区为空，则使用相似度匹配算法解析
//		if (!areaDto.findArea()) {
//			String phone = areaDto.getPhone();
//			// 取详细地址（其中包含省、市、区）
//			text = areaDto.getDetail();
//			
//			// 调用百度地图api查找省市区
//			text = mapAnalyze(text);
//			if (text != null) {
//				text = (text + areaDto.getName()).replaceAll(SPECIAL_SYMBOL, phone);
//				areaDto = tokenizerService.segmentation(text);
//				if (areaDto.findArea()) {
//					areaDto.setSure("0");
//				}
//			} else {
//				areaDto.setSure("0");
//			}
//		}
		return areaDto;
	}
	
	/**
	 * 调用百度地图api定位当前地址
	 * @param text
	 * @return
	 */
//	private String mapAnalyze(String text) {
//		// 识别地址中城市或省份，缩小地图查找范围
//		ProvCityScore provCityScore = analyzeCity(text);
//		
//		Map<String, String> paramMap = new LinkedHashMap<String, String>();
//		paramMap.put("region", provCityScore.getArea());
//		paramMap.put("output", "json");
//		
//		int len = provCityScore.getS2().length();
//		for (int i=len-1; i>provCityScore.getArea().length(); i--) {
//			BaiduSecurityKey baiduSecurityKey = this.baiduSecurityKey.getInstance() == null ? this.baiduSecurityKey : this.baiduSecurityKey.getInstance();
//			paramMap.put("ak", baiduSecurityKey.getAk());
//			
//			String query = provCityScore.getS2().substring(0, i);
//			paramMap.put("query", query);
//			
//			String url = BAIDU_MAP_URL;
//			String uri = url.substring(url.indexOf(BAIDU_URL) + BAIDU_URL.length());
//			String sn = SnCal.getSn(uri, paramMap, baiduSecurityKey.getSk());
//			
//			url += "?region=" + provCityScore.getArea() + "&output=json&ak=" + baiduSecurityKey.getAk()
//			 		+ "&query=" + query + "&sn=" + sn;
//			
//			try {
//				// 调用百度地图API
//				String baiduMapResp = restTemplate.getForObject(url, String.class);
//				LOGGER.info("调用百度地图API接口返回结果：" + baiduMapResp);
//				
//				BaiduMapDto baiduMapDto = JSON.parseObject(baiduMapResp, BaiduMapDto.class);
//				if (baiduMapDto != null && (BAIDU_MAP_OVER_LIMIT_STATUS == baiduMapDto.getStatus() || BAIDU_MAP_OVER_LIMIT.equals(baiduMapDto.getMessage()))) {
//					if (lock.tryLock()) {
//						try {
//							BaiduSecurityKey instance = this.baiduSecurityKey.getInstance();
//							if (instance != null) {
//								this.baiduSecurityKey.setInstance(instance.getNext());
//								LOGGER.info("百度地图api密钥已切换为【" + instance.getNext().getSign() + "】");
//							}
//						} catch (Exception e) {
//							LOGGER.error("切换百度地图密钥失败...", e);
//						} finally {
//							lock.unlock();
//						}
//					}
//				}
//				
//				if (!CollectionUtils.isEmpty(baiduMapDto.getResult())) {
//					// LOGGER.info("百度地图API匹配结果：" + JSON.toJSONString(baiduMapDto.getResult().get(0)));
//					
//					BaiduMapDto.Address address = baiduMapDto.getResult().get(0);
//					String city = address.getCity();
//					String district = address.getDistrict();
//					// 如果找到区（县）
//					if (StringUtils.hasText(district)) {
//						Map<String, Set<String>> cityDistrictMap = areaCache.getCityDistrictMap();
//						for (String cityName : cityDistrictMap.keySet()) {
//							if (cityName.equals(city) || cityName.substring(0, cityName.length()-1).equals(city)) {
//								// 获取城市下所有省市区
//								Set<String> districtSet = cityDistrictMap.get(cityName);
//								if (!CollectionUtils.isEmpty(districtSet)) {
//									boolean findDistrict = false;
//									Iterator<String> iterator = districtSet.iterator();
//									while (iterator.hasNext()) {
//										String districtName = iterator.next();
//										String s = districtName.substring(0, districtName.length()-1);
//										if (district.equals(districtName) || district.startsWith(s)) {
//											district = districtName;
//											findDistrict = true;
//											break;
//										}
//									}
//									if (!findDistrict) {
//										district = districtSet.iterator().next();
//									}
//								} else {
//									district = cityName;
//								}
//							}
//						}
//						
//						// （城市+区） + 详细地址 + 手机号
//						String s = provCityScore.getS1() + city + district + provCityScore.getS2() + SPECIAL_SYMBOL;
//						return s;
//					}
//				}
//			} catch (RestClientException e) {
//				LOGGER.error("百度地图api请求处理异常", e);
//			} catch (Exception e) {
//				LOGGER.error("百度地图api逻辑处理异常", e);
//			}
//		}
//		return null;
//	}
	
	/**
	 * 先识别text城市，如果没有找到，则识别省份
	 * @param text
	 * @return
	 */
	private ProvCityScore analyzeCity(String text) {
		// 城市名称集合
		Set<String> cityNameSet = areaCache.getCityNameSet();
		ProvCityScore provCityScore = analyzeCity(text, cityNameSet);
		if (provCityScore == null) {
			// 省份名称集合
			Set<String> provNameSet = areaCache.getProvNameSet();
			provCityScore = analyzeCity(text, provNameSet);
			if (provCityScore == null) {
				provCityScore = new ProvCityScore();
				provCityScore.setS1("");
				provCityScore.setS2(text);
				provCityScore.setArea("全国");
				provCityScore.setScore(0.0);
			}
		}
		return provCityScore;
	}
	
	/**
	 * 识别城市或省份名称
	 * @param text
	 * @return
	 */
	private ProvCityScore analyzeCity(String text, Set<String> provOrCityNameSet) {
		if (StringUtils.hasText(text)) {
			List<ProvCityScore> list = new ArrayList<ProvCityScore>();
			
			int len = text.length();
			int match = 0;
			for (String name : provOrCityNameSet) {
				l1 : for (int i=0; i<len; i++) {
					for (int j=i+2; j<len; j++) {
						String s = text.substring(i, j);
						if (name.startsWith(s)) {
							match++;
							continue;
						}
						if (match > 0) {
							match++;
							
							String s1 = text.substring(0, i);
							String s2 = text.substring(i);
							BigDecimal matchDecimal = new BigDecimal(match);
							BigDecimal lenDecimal = new BigDecimal(name.length());
							BigDecimal score = matchDecimal.divide(lenDecimal, 4, BigDecimal.ROUND_HALF_UP);
							ProvCityScore provCityScore = new ProvCityScore(name, score.doubleValue(), s1, s2);
							
							list.add(provCityScore);
							match = 0;
							break l1;
						}
						break;
					}
				}
			}
			
			if (!CollectionUtils.isEmpty(list)) {
				ProvCityScore[] provCityScores = list.toArray(new ProvCityScore[] {});
				// 根据相似度率从大到小排序
				sortArea(provCityScores, 0, provCityScores.length-1);
				
				return provCityScores[0];
			}
		}
		return null;
	}
	
	/**
	 * 根据相似度快速排序
	 * @param provCityScores
	 * @param start
	 * @param end
	 */
	private void sortArea(ProvCityScore[] provCityScores, int start, int end) {
		if (start >= end) {
			return;
		}
		int i = start;
		int j = end;
		// 从左向右遍历，即arr[j]和i++进行比较，否则arr[i]和j--进行比较
		boolean leftToRight = true;
		ProvCityScore mark = provCityScores[j];
		while (i<j) {
			if (leftToRight) {
				ProvCityScore provCityScore = provCityScores[i];
				if (provCityScore.getScore() >= mark.getScore()) {
					i++;
				} else {
					provCityScores[i] = mark;
					provCityScores[j--] = provCityScore;
					leftToRight = false;
				}
			} else {
				ProvCityScore provCityScore = provCityScores[j];
				if (provCityScore.getScore() <= mark.getScore()) {
					j--;
				} else {
					provCityScores[j] = mark;
					provCityScores[i++] = provCityScore;
					leftToRight = true;
				}
			}
		}
		sortArea(provCityScores, start, i-1);
		sortArea(provCityScores, j+1, end);
	}
	
	class ProvCityScore {
		
		private String area;
		private double score;
		private String s1;
		private String s2;
		
		public ProvCityScore() {
			
		}
		
		public ProvCityScore(String area, double score, String s1, String s2) {
			this.area = area;
			this.score = score;
			this.s2 = s2;
			this.s1 = s1;
		}
		
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}

		public String getS1() {
			return s1;
		}

		public void setS1(String s1) {
			this.s1 = s1;
		}

		public String getS2() {
			return s2;
		}

		public void setS2(String s2) {
			this.s2 = s2;
		}
	}

//	public void setBaiduSecurityKey(BaiduSecurityKey baiduSecurityKey) {
//		this.baiduSecurityKey = baiduSecurityKey;
//	}
}
