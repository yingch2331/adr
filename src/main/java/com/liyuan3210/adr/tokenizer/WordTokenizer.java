package com.liyuan3210.adr.tokenizer;

import java.util.ArrayList;
import java.util.List;

import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class WordTokenizer implements Tokenizer {

	/**
	 * 正向最大匹配算法
	 */
	private static Segmentation segmentation;
	
	/**
	 * 逆向最大匹配算法
	 */
	private static Segmentation reverseMaxSegmentation;
	
	static {
		// 正向最大匹配算法
		segmentation = SegmentationFactory.getSegmentation(SegmentationAlgorithm.MaximumMatching);
		segmentation.seg("MaximumMatching Init Loading dic...");
		
		// 逆向最大匹配算法
		reverseMaxSegmentation = SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching);
		reverseMaxSegmentation.seg("ReverseMaximumMatching Init Loading dic...");
	}
	
	@Override
	public List<String> tokenizer(String text) {
		List<Word> words = segmentation.seg(text);
		if (!CollectionUtils.isEmpty(words)) {
			List<String> wordList = new ArrayList<String>();
			for (Word word : words) {
				wordList.add(word.getText());
			}
			return wordList;
		}
		return null;
	}
	
	@Override
	public List<String> tokenizerReverseMax(String text) {
		List<Word> words = reverseMaxSegmentation.seg(text);
		if (!CollectionUtils.isEmpty(words)) {
			List<String> wordList = new ArrayList<String>();
			for (Word word : words) {
				wordList.add(word.getText());
			}
			return wordList;
		}
		return null;
	}

}
