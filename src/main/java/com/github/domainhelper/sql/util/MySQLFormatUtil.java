package com.github.domainhelper.sql.util;

public class MySQLFormatUtil {

	public static final String formatWithWhitespace(String sql) {
		sql = sql.replaceAll("\n", " ");
		sql = sql.replaceAll("\t", " ");
		sql = sql.replaceAll("\\(", " \\( ");
		sql = sql.replaceAll("\\)", " \\) ");
		StringBuilder fromatSql = new StringBuilder();
		int sum = 0;
		for(int i = 0 ; i < sql.length() ; i++) {
			char ch = sql.charAt(i);
			if(ch != ' ') {
				fromatSql.append(ch);
				sum = 0;
			}else {
				if(sum == 0) {
					fromatSql.append(ch);
				}
				sum++;
			}
		}
		return fromatSql.toString();
	}
}
