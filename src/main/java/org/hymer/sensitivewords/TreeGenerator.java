package org.hymer.sensitivewords;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 敏感字树生成器
 * 
 * @author hymer
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TreeGenerator {

	/**
	 * 将指定的词分成字构建到一棵树中。
	 * 
	 * @param tree
	 *            字树
	 * @param sensitiveWord
	 *            敏感词词
	 * @return
	 */
	public static void addWord2Tree(Map<String, Map> tree, String sensitiveWord) {
		addWord2Tree(tree, sensitiveWord, 0);
	}

	private static Map<String, Map> addWord2Tree(Map<String, Map> tree,
			String word, int index) {
		if (index == word.length()) {
			tree.put(Finder.TREE_END_KEY, generateWordMap(word));
			return tree;
		}
		String next = word.substring(index, index + 1);
		Map<String, Map> subTree = tree.get(next);
		if (subTree == null) {
			subTree = new HashMap<String, Map>();
		}
		tree.put(next, addWord2Tree(subTree, word, index + 1));
		return tree;
	}

	private static Map<String, Object> generateWordMap(String word) {
		Map<String, Object> wordMap = new HashMap<String, Object>();
		wordMap.put(Finder.WORD_VALUE, word);
		wordMap.put(Finder.WORD_LENGTH, word.length());
		return wordMap;
	}

}
