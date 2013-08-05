package org.hymer.sensitivewords;

import org.hymer.sensitivewords.ext.FinderUtil;
import org.junit.Test;


public class FinderUtilTest {

	@Test
	public void testFind() {
		FinderUtil.initialize();
		System.out.println(FinderUtil.find("阿宾正在电视里看av！"));
	}
	
}
