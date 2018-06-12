package com.github.domainhelper.sql.parser;

public class SQLParser {

	public static SQLSegment parser(String sql) {
		SQLSegment segment = new SQLSegment();
		int startIndex = -1;
		if((startIndex = sql.indexOf("(")) != -1) {
			int lastIndex = sql.lastIndexOf(")");
			String innerSql = sql.substring(startIndex+1, lastIndex);
			SQLSegment innerSegment = parser(innerSql);
			segment.setInnerSegment(innerSegment);
			segment.setPrifixSql(sql.substring(0,startIndex+1));
			segment.setSuffixSql(sql.substring(lastIndex));
		}else {
			segment.setPrifixSql(sql);
		}
		return segment;
	}
	
}
