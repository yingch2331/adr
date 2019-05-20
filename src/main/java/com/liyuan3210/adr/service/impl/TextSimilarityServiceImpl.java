package com.liyuan3210.adr.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.liyuan3210.adr.cache.AreaCache;
import com.liyuan3210.adr.cache.AreaCache.Area;
import com.liyuan3210.adr.dto.AreaDto;
import com.liyuan3210.adr.service.TokenizerService;
import com.liyuan3210.adr.tokenizer.Tokenizer;

@Service
public class TextSimilarityServiceImpl implements TokenizerService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TextSimilarityServiceImpl.class);
	
	private final static int SIMILARITY_AREA_LENGTH = 10;
	
	@Resource(type = TokenizerServiceImpl.class)
	private TokenizerService tokenizerService;
	
	@Autowired
	private AreaCache areaCache;
	
	@Autowired
	private Tokenizer tokenizer;

	@Override
	public AreaDto segmentation(String text) {
		AreaDto areaDto = tokenizerService.segmentation(text);
		// 如果省市区为空，则使用相似度匹配算法解析
		if (!areaDto.findArea()) {
			String phone = areaDto.getPhone();
			// 取详细地址（其中包含省、市、区）
			text = areaDto.getDetail();
			
			// 如果地址中没有找到区（县）名称，则不进行相似度匹配查找
			if (containDistrict(text)) {
				List<String> areaList = similarity(text);
				if (!CollectionUtils.isEmpty(areaList)) {
					LOGGER.info("相似度匹配算法结果：" + JSON.toJSONString(areaList));
					
					// 相似度匹配最优地址
					String address = areaList.get(0);
					if (StringUtils.hasText(phone)) {
						// 取相似度最高的省市区
						text = address + areaDto.getPhone() + areaDto.getName();
					} else {
						text = address + areaDto.getName();
					}
					
					areaDto = tokenizerService.segmentation(text);
					if (areaDto.findArea()) {
						areaDto.setSure("0");
					}
				}
			}
		}
		return areaDto;
	}
	
	/**
	 * 判断text中是否包含区（县）名称
	 * @param text
	 * @return
	 */
	private boolean containDistrict(String text) {
		if (StringUtils.hasText(text)) {
			for (String district : areaCache.getDistrictNameSet()) {
				int len = text.length();
				for (int i=0; i<len-1; i++) {
					int j = i+2;
					String s = text.substring(i, j);
					if (district.startsWith(s)) {
						while (j < len) {
							String ss = text.substring(i, j);
							if (district.startsWith(ss)) {
								j++;
							} else {
								break;
							}
						}
						BigDecimal d1 = new BigDecimal(j-i);
						BigDecimal d2 = new BigDecimal(district.length());
						if (d1.divide(d2, 4, BigDecimal.ROUND_HALF_UP).doubleValue() >= 0.5) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private List<String> similarity(String text) {
		List<SimilarityArea> list = new ArrayList<SimilarityArea>();
		
		for (String areaDetail : areaCache.getAreaDetailCache().keySet()) {
			Area area = areaCache.getAreaDetailCache().get(areaDetail);
			list.add(similarity(areaDetail, text, area));
		}
		
		SimilarityArea[] similarityAreas = list.toArray(new SimilarityArea[] {});
		// 根据相似度率从大到小排序
		sortArea(similarityAreas, 0, similarityAreas.length-1);
		
		// 解析原地址包含省份或城市
		String prov = null;
		String city = null;
		// 省份名称集合
		Set<String> provNameSet = areaCache.getProvNameSet();
		// 城市名称集合
		Set<String> cityNameSet = areaCache.getCityNameSet();
		List<String> words = tokenizer.tokenizer(text);
		l1 : for (String word : words) {
			for (String provName : provNameSet) {
				if (word.length() > 1 && provName.startsWith(word)) {
					prov = provName;
					break l1;
				}
			}
			for (String cityName : cityNameSet) {
				if (word.length() > 1 && cityName.startsWith(word)) {
					city = cityName;
					break l1;
				}
			}
		}
		
		List<String> areaList = new ArrayList<String>();
		// 取相似度最高的前SIMILARITY_AREA_LENGTH个省市区地址
		for (int i=0; i<SIMILARITY_AREA_LENGTH && i<similarityAreas.length; i++) {
			// 判断排序后地址是否包含区（县）
			SimilarityArea similarityArea = similarityAreas[i];
			// 原地址
			String area = similarityArea.getArea();
			String district = similarityArea.getDistrict();
			for (int j=district.length()-1; j>1; j--) {
				String s = district.substring(0, j);
				if (area.contains(s)) {
					// 原地址查找到的城市
					if (StringUtils.hasText(city)) {
						if (!city.equals(similarityArea.getCity())) {
							break;
						}
						
					// 原地址查找到的省份
					} else if (StringUtils.hasText(prov)) {
						if (!prov.equals(similarityArea.getProv())) {
							break;
						}
					}
					areaList.add(similarityAreas[i].getAddress());
					break;
				}
			}
		}
		return areaList;
	}
	
	/**
	 * 计算target在source中的相似度
	 * @param source
	 * @param target
	 * @param district
	 * @return
	 */
	private SimilarityArea similarity(String source, String target, Area area) {
		if (StringUtils.isEmpty(source) || StringUtils.isEmpty(target)) {
			return null;
		}
		
		String sourceArea = source;
		
		int len = target.length() > source.length() ? source.length() : target.length();
		// 字符匹配个数
		int match = 0;
		int k = -1;
		int n = -1;
		boolean mark = true;
		
		loop : for (int i=0; i<len-1; i++) {
			for (int j=i+1; j<len; j++) {
				String s = target.substring(i, j+1);
				if (source.contains(s)) {
					if (mark) {
						mark = false;
						n = i-1;
					}
					k = source.indexOf(s) + s.length();
					if (j == len-1) {
						match += s.length();
						break loop;
					}
				} else {
					if (k > 0) {
						match += s.length() - 1;
						source = source.substring(k);
						k = -1;
						i = j-1;
					}
					break;
				}
			}
		}
		
		// 姓名+省市区+详细地址
		String address = target.substring(0, n+1) + sourceArea + target.substring(n+1);
		
		BigDecimal matchDecimal = new BigDecimal(match);
		BigDecimal lenDecimal = new BigDecimal(sourceArea.length());
		BigDecimal score = matchDecimal.divide(lenDecimal, 4, BigDecimal.ROUND_HALF_UP);
		return new SimilarityArea(address, score.doubleValue(), target, area.getCounty(), area.getCity(), area.getProvince());
	}
	
	/**
	 * 根据相似度快速排序
	 * @param similarityAreas
	 * @param start
	 * @param end
	 */
	private void sortArea(SimilarityArea[] similarityAreas, int start, int end) {
		if (start >= end) {
			return;
		}
		int i = start;
		int j = end;
		// 从左向右遍历，即arr[j]和i++进行比较，否则arr[i]和j--进行比较
		boolean leftToRight = true;
		SimilarityArea mark = similarityAreas[j];
		while (i<j) {
			if (leftToRight) {
				SimilarityArea similarityArea = similarityAreas[i];
				if (similarityArea.getScore() >= mark.getScore()) {
					i++;
				} else {
					similarityAreas[i] = mark;
					similarityAreas[j--] = similarityArea;
					leftToRight = false;
				}
			} else {
				SimilarityArea similarityArea = similarityAreas[j];
				if (similarityArea.getScore() <= mark.getScore()) {
					j--;
				} else {
					similarityAreas[j] = mark;
					similarityAreas[i++] = similarityArea;
					leftToRight = true;
				}
			}
		}
		sortArea(similarityAreas, start, i-1);
		sortArea(similarityAreas, j+1, end);
	}
	
	class SimilarityArea {
		
		private String area;
		private double score;
		private String district;
		private String city;
		private String prov;
		private String address;
		
		public SimilarityArea() {
			
		}
		
		public SimilarityArea(String address, double score, String area, String district, String city, String prov) {
			this.address = address;
			this.score = score;
			this.area = area;
			this.district = district;
			this.city = city;
			this.prov = prov;
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
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCity() {
			return city;
		}

		public String getProv() {
			return prov;
		}
	}

}
