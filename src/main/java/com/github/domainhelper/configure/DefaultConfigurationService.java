package com.github.domainhelper.configure;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultConfigurationService implements ConfigurationService {
	
	private ConcurrentHashMap<String, DomainConfiguration> configuration;

	@Override
	public ConcurrentHashMap<String, DomainConfiguration> getConfiguration() {
		
		return configuration;
	}

	public void setConfiguration(ConcurrentHashMap<String, DomainConfiguration> configuration) {
		this.configuration = configuration;
	}

	
}
