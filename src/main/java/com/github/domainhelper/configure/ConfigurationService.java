package com.github.domainhelper.configure;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 	获取管理域配置。
 * </p>
 * @author littlechen
 *
 */
public interface ConfigurationService {
	
	public ConcurrentHashMap<String, DomainConfiguration> getConfiguration();
	
}
