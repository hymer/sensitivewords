package org.hymer.sensitivewords;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 敏感词过滤
 * 
 * @author hymer
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Finder {
	// 敏感词
	private static Set<String> WORDS = new HashSet<String>();
	// 由敏感词生成的字树
	private static Map<String, Map> TREE = new ConcurrentHashMap<String, Map>();
	// 默认敏感词分割符
	public static final String DEFAULT_SEPARATOR = ",";
	/* 在树当中标志一个词的结束 */
	public static final String TREE_END_KEY = "^";
	// 敏感词value标记
	public static final String WORD_VALUE = "v";
	// 敏感词长度标记
	public static final String WORD_LENGTH = "l";
	// 默认替换符
	public static final char DEFAULT_REPLACEMENT = '*';
	// 默认起始标记
	public static final String DEFAULT_START_TAG = "<font color=\"red\">";
	// 默认结束标记
	public static final String DEFAULT_END_TAG = "</font>";

	public Finder(String[] words) {
		addSensitiveWords(words);
	}

	/**
	 * 删除所有敏感词
	 */
	public static void clearSensitiveWords() {
		WORDS.clear();
		TREE.clear();
	}

	/**
	 * 
	 * 添加敏感词
	 * 
	 * @author hymer
	 * @param words
	 */
	public static void addSensitiveWords(String[] words) {
		if (words == null || words.length == 0) {
			return;
		}
		check(words);
		addWords(words);
	}

	/**
	 * 
	 * 添加敏感词
	 * 
	 * @author hymer
	 * @param words
	 * @param separator
	 */
	public static void addSensitiveWords(String words, String separator) {
		if (words != null && !"".equals(words.trim())) {
			check(words);
			String[] sensitiveWords = words.split(separator);
			addWords(sensitiveWords);
		}
	}

	/**
	 * 
	 * 添加敏感词，默认使用,分割
	 * 
	 * @author hymer
	 * @param words
	 */
	public static void addSensitiveWords(String words) {
		check(words);
		addSensitiveWords(words, DEFAULT_SEPARATOR);
	}

	/**
	 * 删除敏感词
	 * 
	 * @param words
	 */
	public static void removeSensitiveWords(String words) {
		check(words);
		removeSensitiveWords(words, DEFAULT_SEPARATOR);
	}

	/**
	 * 删除敏感词
	 * 
	 * @param words
	 */
	public static void removeSensitiveWords(String words, String separator) {
		if (words != null && !"".equals(words.trim())) {
			check(words);
			String[] sensitiveWords = words.split(separator);
			removeWords(sensitiveWords);
		}
	}

	/**
	 * 删除敏感词
	 * 
	 * @param words
	 */
	public static void removeSensitiveWords(String[] words) {
		if (words == null || words.length == 0) {
			return;
		}
		check(words);
		removeWords(words);
	}

	/**
	 * 
	 * 找出文本中的敏感词
	 * 
	 * @author hymer
	 * @param text
	 * @return
	 */
	public static Set<String> find(String text) {
		return new TextAnalysis().analysis(TREE, text);
	}

	/**
	 * 替换文本中的敏感词
	 * 
	 * @param text
	 *            含敏感词的文本
	 * @return
	 */
	public static String replace(String text) {
		return new TextAnalysis().replace(TREE, text, DEFAULT_REPLACEMENT);
	}

	/**
	 * 替换文本中的敏感词
	 * 
	 * @param text
	 *            含敏感词的文本
	 * @param replacement
	 *            替换字符
	 * @return
	 */
	public static String replace(String text, Character replacement) {
		return new TextAnalysis().replace(TREE, text, replacement);
	}

	/**
	 * 
	 * 过滤文本，并标记出敏感词，默认使用HTML中红色font标记
	 * 
	 * @author hymer
	 * @param text
	 * @return
	 */
	public static String filter(String text) {
		return new TextAnalysis().mark(TREE, text, DEFAULT_START_TAG, DEFAULT_END_TAG);
	}

	/**
	 * 
	 * 过滤文本，并标记出敏感词
	 * 
	 * @author hymer
	 * @param text
	 * @param startTag
	 * @param endTag
	 * @return
	 */
	public static String filter(String text, String startTag, String endTag) {
		return new TextAnalysis().mark(TREE, text, startTag, endTag);
	}

	private static void check(String... words) {
		for (String word : words) {
			if (word != null && word.contains(TREE_END_KEY)) {
				throw new RuntimeException("包含非法字符：" + TREE_END_KEY);
			}
		}
	}

	private static void addWords(String... sensitiveWords) {
		for (String word : sensitiveWords) {
			if (word != null && !word.trim().equals("")) {
				word = word.trim();
				int len = word.length();
				if (len > 1024) {
					throw new RuntimeException("敏感词太长[最长1024]!");
				}
				// 添加该词，如果未重复，则添加到tree
				if (WORDS.add(word)) {
					TreeGenerator.addWord2Tree(TREE, word);
				}
			}
		}
		System.out.println("当前敏感词数量：" + WORDS.size());
	}

	private static void removeWords(String... sensitiveWords) {
		for (String word : sensitiveWords) {
			if (word != null && !word.trim().equals("")) {
				word = word.trim();
				WORDS.remove(word);
			}
		}
		TREE.clear();
		addWords(WORDS.toArray(new String[WORDS.size()]));
	}

	@SuppressWarnings("unused")
	private static void printTree(Map<String, Map> wordTree, int level) {
		if (wordTree == null || wordTree.isEmpty()) {
			return;
		}
		Iterator<String> it = wordTree.keySet().iterator();
		while (it.hasNext()) {
			String next = it.next();
			for (int i = 0; i < level; i++) {
				System.out.print("-");
			}
			System.out.print(next + "\n");
			Object tmp = wordTree.get(next);
			if (tmp instanceof Map) {
				printTree((Map) tmp, level + 1);
			}
		}
	}

}
