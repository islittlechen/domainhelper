package com.github.domainhelper.sql.parser;

import com.github.domainhelper.sql.keyword.MysqlKeyWord;

public class DomainSegInsertPositionParser {

	public static int location(String sql) {
		int index = -1;
		if((index=sql.indexOf(MysqlKeyWord.GROUP.getUpperCase())) == -1) {
			if((index=sql.indexOf(MysqlKeyWord.ORDER.getUpperCase())) == -1) {
				if((index=sql.indexOf(MysqlKeyWord.LIMIT.getUpperCase())) == -1) {
					index=sql.indexOf(")");
				}
			}
		}
		return index;
	}
	
}
