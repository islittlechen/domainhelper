package com.github.domainhelper.sql.parser;

public class TableAliasSQLParser {

	public static String parseAlias(String sql,String tableName) {
		String tableAlias = "";
		int index = sql.indexOf(tableName);
		for(int i = index+tableName.length() ; i < sql.length() ; i++) {
			char ch = sql.charAt(i);
			if(ch == ',') {
				break;
			}
			if( ch != ' ') {
				if(i+1 == sql.length()) {
					tableAlias += ch;
				}else {
					if(ch == 'a' || ch == 'A') {
						char next = sql.charAt(i+1);
						if(next == 's' || next == 'S') {
							i++;
						}else {
							tableAlias += ch;
						}
					}else {
						tableAlias += ch;
					}
				}
			}
		}
		if(tableAlias.length() == 0) {
			return null;
		}
		return tableAlias;
	}
	
	
}
