package com.liyuan3210.adr.service.impl;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import com.liyuan3210.adr.cache.AreaCache;
import com.liyuan3210.adr.cache.AreaCache.Area;
import com.liyuan3210.adr.dto.AreaDto;
import com.liyuan3210.adr.service.TokenizerService;
import com.liyuan3210.adr.tokenizer.Tokenizer;
//@Primary
@Service
public class TokenizerServiceImpl implements TokenizerService {

	private final static Logger LOGGER = LoggerFactory.getLogger(TokenizerServiceImpl.class);
	
	@Autowired
	private Tokenizer tokenizer;
	
	@Autowired
	private AreaCache areaCache;
	
	@Override
	public AreaDto segmentation(String text) {
		// 将text中大写字母全部转成小写
		text = text.toLowerCase();
		
		// 初始化地址明细对象
		AreaDto areaDto = new AreaDto();
		areaDto.setDetail(text);
		// 去掉中英文空格
		text = text.replaceAll("[ , ,,，\\|,｜,|,-,。,.]", "");
		
		String mobileReg = "(((13[0-9])|(15[^4])|(18[0,1,2,3,4,5-9])|(17[0-8])|(147)|(19[8,9])|(166))\\d{8})";
		String fixedPhoneReg = "((?:(400)(\\-?\\d{3})(\\-?\\d{4})?)"
							+ "|(?:(800)(\\-?\\d{3})(\\-?\\d{4})?)"
							+ "|(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)"
							+ "|(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?))";
		
		String phone = null;
		Pattern pattern = Pattern.compile(mobileReg);
		Matcher matcher = pattern.matcher(text);
		// 如果能够匹配到手机号码
		if (matcher.find()) {
			phone = matcher.group(1);
		} else {
			pattern = Pattern.compile(fixedPhoneReg);
			matcher = pattern.matcher(text);
			// 如果未匹配到手机号码，则匹配固定电话号码
			if (matcher.find()) {
				phone = matcher.group(1);
			} else {
				LOGGER.info("地址【" + text + "】未能匹配到任何手机和固定电话号码");
			}
		}
		
		if (phone != null) {
			// 手机/固话
			areaDto.setPhone(phone);
			
			// 1、name + phone + address 2、address + phone + name
			String s1 = text.substring(0, text.indexOf(phone));
			String s2 = text.substring(text.indexOf(phone) + phone.length());
			// 如果s1和s2都不为空，则取较短的字符串，作为寄件人姓名，长的作为地址
			if (StringUtils.hasText(s1) && StringUtils.hasText(s2)) {
				// 从s1和s2中筛选出姓名、地址
				if (containArea(s1)) {
					areaDto.setName(s2);
					areaDto.setDetail(s1);
				} else if (containArea(s2)) {
					areaDto.setName(s1);
					areaDto.setDetail(s2);
				} else {
					// 如果都不包含区（县），则短的是姓名，长的是地址
					if (s1.length() < s2.length()) {
						areaDto.setName(s1);
						areaDto.setDetail(s2);
					} else {
						areaDto.setName(s2);
						areaDto.setDetail(s1);
					}
				}
			} else {
				// 省市区地址去除手机或者固定电话号码
				String s = text.replaceAll(phone, "");
				areaDto.setDetail(s);
			}
		}
		
		//  是否包含三级区（县）
		if (containArea(text)) {
			// 获取省市区地址，但不包含寄件人姓名
			getArea(areaDto);
		}
		
		if (areaDto.findArea()) {
			areaDto.setSure("1");
		}
		
		String name = areaDto.getName();
		if (StringUtils.hasText(name)) {
			// 姓名，其中可能包含省份或城市，分词器进行分词，如果含有省份或城市，则移除省份或城市名称
			List<String> words = tokenizer.tokenizerReverseMax(name);
			if (!CollectionUtils.isEmpty(words)) {
				// 省份和城市名称集合
				Set<String> provCityNames = areaCache.getAreaNameCache();
				l1 : for (int i=words.size()-1; i>=0; i--) {
					for (String provCityName : provCityNames) {
						if (provCityName.startsWith(words.get(i))) {
							continue l1;
						}
					}
					
					StringBuilder sb = new StringBuilder();
					for (int j=0; j<=i; j++) {
						sb.append(words.get(j));
					}
					areaDto.setName(sb.toString());
				}
			}
		}
		return areaDto;
	}
	
	/**
	 * 文本分词器取出省市区信息
	 * @param areaDto 地址明细对象（包含：省、市、区、详细地址、姓名、手机等）
	 */
	private void getArea(AreaDto areaDto) {
		if (areaDto == null) {
			return;
		}
		
		// 详细地址（包含省、市、区）
		String text = areaDto.getDetail();
		
		String county = "";
		int countyIndex = -1;
		List<String> words = tokenizer.tokenizer(text);
		
		LOGGER.info("原语句【" + text + "】，分词结果：" + JSON.toJSONString(words));
		
		if (!CollectionUtils.isEmpty(words)) {
			for (int i=0; i<words.size(); i++) {
				String word = words.get(i);
				// 如果是省份，则跳过本次循环
				if (areaCache.containProv(word)) {
					continue;
				}
				Area area = areaCache.getArea(word, words.subList(0, i));
				if (area != null) {
					// 区（县）名称
					county = word;
					countyIndex = i;
					
					areaDto.setCounty(county);
					areaDto.setCity(area.getCity());
					areaDto.setProvince(area.getProvince());
					break;
				}
			}
			
			if (StringUtils.isEmpty(areaDto.getName())) {
				// 取出text中包含的名字信息
				if (countyIndex != -1) {
					String name = "";
					for (int i=0; i<countyIndex; i++) {
						String word = words.get(i);
						if (areaCache.containProvCity(words.get(i)) && word.length() > 1) {
							break;
						} else {
							name += word;
						}
					}
					if (StringUtils.hasText(name.trim())) {
						areaDto.setName(name.trim());
					} else {
						// 如果字符串头没有找到名字，则取末尾分词
						String lastWord = words.get(words.size()-1);
						text = text.substring(0, text.lastIndexOf(lastWord));
						areaDto.setName(lastWord);
					}
				}
			}
		}
		// 区（县）对应字符串下标
		int index = text.indexOf(county) + county.length();
		if (index < text.length()-1) {
			String detail = text.substring(index);
			areaDto.setDetail(detail.trim());
		}
	}
	
	/**
	 * 判断字符串是否包含区(县)
	 * @param text
	 * @param containName
	 * @return
	 */
	private boolean containArea(String text) {
		List<String> words = tokenizer.tokenizer(text);
		
		LOGGER.info("检查语句【" + text + "】中是否包含区（县），分词结果：" + JSON.toJSONString(words));
		
		if (!CollectionUtils.isEmpty(words)) {
			for (int i=0; i<words.size(); i++) {
				String word = words.get(i);
				// 如果是省份，则跳过本次循环
				if (areaCache.containProv(word)) {
					continue;
				} else {
					Area area = areaCache.getArea(word, words.subList(0, i));
					if (area != null) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
