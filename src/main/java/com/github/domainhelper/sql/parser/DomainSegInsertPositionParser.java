package com.github.domainhelper.sql.parser;

import com.github.domainhelper.sql.keyword.MysqlKeyWord;

public class DomainSegInsertPositionParser {

	public static int location(String sql) {
		int index = -1;
		if((index=sql.indexOf(MysqlKeyWord.GROUP.getUpperCase())) > 0) {
			return index;
		}
		if((index=sql.indexOf(MysqlKeyWord.ORDER.getUpperCase())) > 0) {
			return index;
		}
		if((index=sql.indexOf(MysqlKeyWord.LIMIT.getUpperCase())) > 0) {
			return index;
		}

		if((index=sql.indexOf(")")) > 0) {
			return index+1;
		}
		return index;
	}
	
}
