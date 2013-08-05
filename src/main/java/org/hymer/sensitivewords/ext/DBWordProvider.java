package org.hymer.sensitivewords.ext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化从MySQL中导入敏感词
 * 
 * @author hymer
 * 
 */
public class DBWordProvider {

	private String className;
	private String url;
	private String user;
	private String password;

	public DBWordProvider(String className, String url, String user,
			String password) {
		super();
		this.className = className;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public String[] loadWords() {
		List<String> list = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName(className);
			conn = DriverManager.getConnection(url, user, password);
			String sql = "SELECT word FROM tb_sensitivewords";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			list = new ArrayList<String>();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			return list.toArray(new String[list.size()]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
