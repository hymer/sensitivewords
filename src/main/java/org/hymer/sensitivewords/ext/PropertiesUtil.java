package org.hymer.sensitivewords.ext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

/**
 * 资源文件解析工具
 * 
 * @author hymer
 * 
 */
public class PropertiesUtil {
	private Properties props;
	private URI uri;

	public PropertiesUtil() {
		readProperties("config.properties");
	}

	public PropertiesUtil(String fileName) {
		readProperties(fileName);
	}

	private void readProperties(String fileName) {
		try {
			props = new Properties();
			InputStream fis = this.getClass().getClassLoader()
					.getResourceAsStream(fileName);
			props.load(fis);
			uri = this.getClass().getClassLoader().getResource(fileName)
					.toURI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取某个属性
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * 在控制台上打印出所有属性，调试时用。
	 */
	public void printProperties() {
		props.list(System.out);
	}

	/**
	 * 写入properties信息
	 */
	public void writeProperties(String key, String value) {
		try {
			OutputStream fos = new FileOutputStream(new File(uri));
			props.setProperty(key, value);
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			props.store(fos, "#Update key：" + key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
