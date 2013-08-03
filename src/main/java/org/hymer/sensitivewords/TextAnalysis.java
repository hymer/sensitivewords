package org.hymer.sensitivewords;

import java.util.HashSet;
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
	 * @author hewang
	 * @param tree
	 *            敏感字树
	 * @param texts
	 *            待分析的文本
	 * @return
	 */
	public Set<String> analysis(Map<String, Map> tree, String texts) {
		Set<String> words = new HashSet<String>();
		analysis(tree, texts, words);
		return words;
	}

	/**
	 * 
	 * 标记文本中的敏感词
	 * 
	 * @author hewang
	 * @param tree
	 *            敏感字树
	 * @param texts
	 *            待分析的文本
	 * @param startTag
	 *            开始标记
	 * @param endTag
	 *            结束标记
	 * @return
	 */
	public String mark(Map<String, Map> tree, String texts, String startTag,
			String endTag) {
		StringBuffer sb = new StringBuffer("");
		mark(tree, texts, 0, startTag, endTag, sb);
		return sb.toString();
	}

	private void mark(Map<String, Map> tree, String texts, int index,
			String startTag, String endTag, StringBuffer sb) {
		int last = 0;
		int textLen = texts.length();
		while (index < textLen) {
			String tmp = texts.substring(index, index + 1);
			String nexts = texts.substring(index);
			String word = "";
			word = findMaxWord(tree, nexts, 0, word);
			if (!"".equals(word)) {
				int wordLen = word.length();
				if (index >= last) {
					sb.append(startTag + word + endTag);
				} else {
					if (last < index + wordLen) {
						sb.insert(sb.length() - endTag.length(),
								texts.substring(last, index + wordLen));
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

	private String findMaxWord(Map<String, Map> tree, String texts, int index,
			String word) {
		if (index < texts.length()) {
			Map<String, Map> subTree = tree.get(texts.substring(index,
					index + 1));
			if (subTree != null) {
				Map<String, Object> end = subTree.get(Finder.TREE_END_KEY);
				if (end != null) {
					String sensitiveWord = (String) end.get(Finder.WORD_VALUE);
					if (word.length() < sensitiveWord.length()) {
						word = sensitiveWord;
					}
				}
				if (end == null || subTree.size() > 1) {
					return findMaxWord(subTree, texts, index + 1, word);
				}
			}
		}
		return word;
	}

	private void analysis(Map<String, Map> tree, String texts, Set<String> words) {
		int index = 0;
		while (index < texts.length()) {
			findWord(tree, texts, index, words);
			index++;
		}
	}

	private void findWord(Map<String, Map> tree, String texts, int index,
			Set<String> words) {
		Map<String, Map> subTree = tree.get(texts.substring(index, index + 1));
		if (subTree != null) {
			Map<String, Object> end = subTree.get(Finder.TREE_END_KEY);
			if (end != null) {
				words.add((String) end.get(Finder.WORD_VALUE));
			}
			if (end == null || subTree.size() > 1) {
				findWord(subTree, texts, index + 1, words);
			}
		}
	}

}
