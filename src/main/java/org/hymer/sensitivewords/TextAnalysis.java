package org.hymer.sensitivewords;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 文本分析类
 * 
 * @author hymer
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TextAnalysis {

	/**
	 * 
	 * 分析文本，返回搜寻到的所有敏感字
	 * 
	 * @author hymer
	 * @param tree
	 *            敏感字树
	 * @param text
	 *            待分析的文本
	 * @return
	 */
	public Set<String> analysis(Map<String, Map> tree, String text) {
		Set<String> words = new LinkedHashSet<String>();
		if (text != null && text.trim().length() > 0) {
			analysis(tree, text, words);
		}
		return words;
	}

	/**
	 * 替换文本中的敏感词
	 * 
	 * @param tree
	 *            敏感字树
	 * @param text
	 *            待分析的文本
	 * @param replacement
	 *            替换字符
	 * @return
	 */
	public String replace(Map<String, Map> tree, String text,
			Character replacement) {
		if (replacement == null) {
			replacement = Finder.DEFAULT_REPLACEMENT;
		}
		if (text != null && text.trim().length() > 0) {
			StringBuffer sb = new StringBuffer("");
			replace(tree, text, 0, replacement, sb);
			return sb.toString();
		}
		return text;
	}

	/**
	 * 
	 * 标记文本中的敏感词
	 * 
	 * @author hymer
	 * @param tree
	 *            敏感字树
	 * @param text
	 *            待分析的文本
	 * @param startTag
	 *            开始标记
	 * @param endTag
	 *            结束标记
	 * @return
	 */
	public String mark(Map<String, Map> tree, String text, String startTag,
			String endTag) {
		if (text != null && text.trim().length() > 0) {
			StringBuffer sb = new StringBuffer("");
			mark(tree, text, 0, startTag, endTag, sb);
			return sb.toString();
		}
		return text;
	}

	private void mark(Map<String, Map> tree, String text, int index,
			String startTag, String endTag, StringBuffer sb) {
		int last = 0;
		int textLen = text.length();
		while (index < textLen) {
			String tmp = text.substring(index, index + 1);
			String nexts = text.substring(index);
			String word = "";
			word = findMaxWord(tree, nexts, 0, word);
			if (!"".equals(word)) {
				int wordLen = word.length();
				if (index >= last) {
					sb.append(startTag + word + endTag);
				} else {
					if (last < index + wordLen) {
						sb.insert(sb.length() - endTag.length(),
								text.substring(last, index + wordLen));
					}
				}
				last = index + wordLen;
			} else {
				if (index >= last) {
					sb.append(tmp);
				}
			}
			index++;
		}
	}

	private String findMaxWord(Map<String, Map> tree, String text, int index,
			String word) {
		Map<String, Map> subTree = tree.get(text.substring(index, index + 1));
		if (subTree != null) {
			Map<String, Object> end = subTree.get(Finder.TREE_END_KEY);
			if (end != null) {
				String sensitiveWord = (String) end.get(Finder.WORD_VALUE);
				if (word.length() < sensitiveWord.length()) {
					word = sensitiveWord;
				}
			}
			if ((index + 1) < text.length()
					&& (end == null || subTree.size() > 1)) {
				return findMaxWord(subTree, text, index + 1, word);
			}
		}
		return word;
	}

	private void analysis(Map<String, Map> tree, String text, Set<String> words) {
		int index = 0;
		while (index < text.length()) {
			findWord(tree, text, index, words);
			index++;
		}
	}

	private void findWord(Map<String, Map> tree, String text, int index,
			Set<String> words) {
		Map<String, Map> subTree = tree.get(text.substring(index, index + 1));
		if (subTree != null) {
			Map<String, Object> end = subTree.get(Finder.TREE_END_KEY);
			if (end != null) {
				words.add((String) end.get(Finder.WORD_VALUE));
			}
			if ((index + 1) < text.length()
					&& (end == null || subTree.size() > 1)) {
				findWord(subTree, text, index + 1, words);
			}
		}
	}

	private void replace(Map<String, Map> tree, String text, int index,
			char replacement, StringBuffer sb) {
		int last = 0;
		int textLen = text.length();
		while (index < textLen) {
			String tmp = text.substring(index, index + 1);
			String nexts = text.substring(index);
			String word = "";
			word = findMaxWord(tree, nexts, 0, word);
			if (!"".equals(word)) {
				int replaceLen = 0;
				int wordLen = word.length();
				if (index >= last) {
					replaceLen = wordLen;
				} else {
					replaceLen = index + wordLen - last;
				}
				while (replaceLen > 0) {
					sb.append(replacement);
					replaceLen--;
				}
				last = index + wordLen;
			} else {
				if (index >= last) {
					sb.append(tmp);
				}
			}
			index++;
		}
	}

}
