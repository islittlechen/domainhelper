package com.github.domainhelper.sql.parser;

import com.github.domainhelper.sql.keyword.MysqlKeyWord;

public class TableAliasSQLParser {

	public static String parseAlias(String sql,String tableName) {
		String tableAlias = "";
		int index = sql.indexOf(tableName);
		for(int i = index+tableName.length() ; i < sql.length() ; i++) {
			char ch = sql.charAt(i);
			if(ch == ',' || ch == ')' || sql.startsWith(MysqlKeyWord.WHERE.getUpperCase(), i)
					|| sql.startsWith(MysqlKeyWord.ORDER.getUpperCase(), i)
					|| sql.startsWith(MysqlKeyWord.GROUP.getUpperCase(), i)
					|| sql.startsWith(MysqlKeyWord.LIMIT.getUpperCase(), i)) {
				break;
			}
			if( ch != ' ') {
				if(i+1 == sql.length()) {
					tableAlias += ch;
				}else {
					if(ch == 'a' || ch == 'A') {
						if(i+1 < sql.length()) {
							char next = sql.charAt(i+1);
							if(next == 's' || next == 'S') {
								if(i+2 < sql.length()) {
									if(sql.charAt(i+2) == ' ') {
										i += 2;
									}else {
										tableAlias += ch;
									}
								}else {
									i++;
								}
								
							}else {
								tableAlias += ch;
							}
						}
						
					}else {
						tableAlias += ch;
					}
				}
			}else {
				if(tableAlias.length() > 0) {
					break;
				}
			}
		}
		if(tableAlias.length() == 0) {
			return null;
		}
		if(tableAlias.equals("\n") || tableAlias.equals("\t")){
			return null;
		}
		return tableAlias;
	}
	
	
}
