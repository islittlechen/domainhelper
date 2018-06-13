package com.github.domainhelper.sql.keyword;

public enum MysqlKeyWord {

	UNION("union","UNION"),LEFT_BRACKET("(","("),RIGHT_BRACKET(")",")"),ORDER("order","ORDER"),
	GROUP("group","GROUP"),WHERE("where","WHERE"),AS("as","AS"),AND("and","AND"),IN("in","IN"),
	LIMIT("limit","LIMIT");
	
	private String lowCase;
	
	private String upperCase;

	
	private MysqlKeyWord(String lowCase, String upperCase) {
		this.lowCase = lowCase;
		this.upperCase = upperCase;
	}

	public String getLowCase() {
		return lowCase;
	}

	public void setLowCase(String lowCase) {
		this.lowCase = lowCase;
	}

	public String getUpperCase() {
		return upperCase;
	}

	public void setUpperCase(String upperCase) {
		this.upperCase = upperCase;
	}
	
	
	
}
