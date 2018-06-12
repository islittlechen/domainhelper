package com.github.domainhelper.configure;

import com.github.domainhelper.sql.processor.DomainProcessor;

public class DomainConfiguration {
	
	
	private String domainTableName;
	
	private String domainColumnName;
	
	
	private DomainProcessor domainProcessor;

	public String getDomainTableName() {
		return domainTableName;
	}

	public void setDomainTableName(String domainTableName) {
		this.domainTableName = domainTableName;
	}

	public String getDomainColumnName() {
		return domainColumnName;
	}

	public void setDomainColumnName(String domainColumnName) {
		this.domainColumnName = domainColumnName;
	}
 
	public DomainProcessor getDomainProcessor() {
		return domainProcessor;
	}

	public void setDomainProcessor(DomainProcessor domainProcessor) {
		this.domainProcessor = domainProcessor;
	}
	
	
}
