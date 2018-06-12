package com.github.domainhelper.sql.parser;

public class SQLSegment {
	
	private String prifixSql;
	
	private String suffixSql;
	
	private SQLSegment innerSegment;

	public String getPrifixSql() {
		return prifixSql;
	}

	public void setPrifixSql(String prifixSql) {
		this.prifixSql = prifixSql;
	}

	public String getSuffixSql() {
		return suffixSql;
	}

	public void setSuffixSql(String suffixSql) {
		this.suffixSql = suffixSql;
	}

	public SQLSegment getInnerSegment() {
		return innerSegment;
	}

	public void setInnerSegment(SQLSegment innerSegment) {
		this.innerSegment = innerSegment;
	}
	
	
	
}
