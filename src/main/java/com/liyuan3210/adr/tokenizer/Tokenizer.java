package com.liyuan3210.adr.tokenizer;

import java.util.List;

/**
 * 分词器统一接口
 * @author gaohuiyu
 *
 */
public interface Tokenizer {

	/**
	 * 正向最大匹配算法
	 * @param text
	 * @return
	 */
	List<String> tokenizer(String text);
	
	/**
	 * 逆向最大匹配算法
	 * @param text
	 * @return
	 */
	List<String> tokenizerReverseMax(String text);
	
}
