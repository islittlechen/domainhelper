package com.github.domainhelper.interceptor;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.domainhelper.configure.ConfigurationService;
import com.github.domainhelper.sql.builder.DomainSQLBuilder;

@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class DomainQueryInterceptor implements Interceptor {
	
	private ConfigurationService configurationService;

	private static Field additionalParametersField;

	static {
		try {
			additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
			additionalParametersField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("获取 BoundSql 属性 additionalParameters 失败: " + e, e);
		}
	}

	/**
	 * 获取 BoundSql 属性值 additionalParameters
	 *
	 * @param boundSql
	 * @return
	 */
	public static Map<String, Object> getAdditionalParameter(BoundSql boundSql) {
		try {
			return (Map<String, Object>) additionalParametersField.get(boundSql);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("获取 BoundSql 属性值 additionalParameters 失败: " + e, e);
		}
	}


	@SuppressWarnings("rawtypes")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		Object parameterObject = args[1];
		RowBounds rowBounds = (RowBounds) args[2];
		ResultHandler resultHandler = (ResultHandler) args[3];
		Executor executor = (Executor) invocation.getTarget();
		BoundSql boundSql = ms.getBoundSql(parameterObject);
		Map<String, Object> additionalParameters = getAdditionalParameter(boundSql);
		String domainSql = generatorSQL(boundSql.getSql(), parameterObject);
		BoundSql domainBoundSql = new BoundSql(ms.getConfiguration(), domainSql, boundSql.getParameterMappings(), parameterObject);

		//当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
		for (String key : additionalParameters.keySet()) {
			domainBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
		}

		//可以对参数做各种处理
		CacheKey cacheKey = executor.createCacheKey(ms, parameterObject, rowBounds, domainBoundSql);
		return executor.query(ms, parameterObject, rowBounds, resultHandler,cacheKey, domainBoundSql);
	}
	
	 /**
	  * SQL重建
	  * @param originSQL
	  * @param parameterObject
	  * @return
	  */
	private String generatorSQL(String originSQL,Object parameterObject ) {
		String queryDomainSql = originSQL.toUpperCase();
		 
		return DomainSQLBuilder.builder(queryDomainSql,configurationService,parameterObject);
	}
	
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) { 
    }

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

    

}
