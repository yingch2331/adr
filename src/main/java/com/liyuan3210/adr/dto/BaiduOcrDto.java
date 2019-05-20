package com.liyuan3210.adr.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaiduOcrDto {

	private String log_id;
	private String words_result_num;
	private String language;
	private String direction;
	private List<WordsResult> words_result;
	
	@Setter
	@Getter
	public static class WordsResult {
		private String words;
		private Probability probability;
	}
	
	@Setter
	@Getter
	public static class Probability {
		private String average;
		private String min;
		private String variance;
	}
}
