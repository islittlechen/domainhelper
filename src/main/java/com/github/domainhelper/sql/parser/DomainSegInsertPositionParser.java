package com.github.domainhelper.sql.parser;

public class DomainSegInsertPositionParser {

	public static int location(String sql) {
		int index = -1;
		if((index=sql.indexOf("group")) == -1) {
			if((index=sql.indexOf("order")) == -1) {
				index=sql.indexOf("limit");
			}
		}
		return index;
	}
	
}
