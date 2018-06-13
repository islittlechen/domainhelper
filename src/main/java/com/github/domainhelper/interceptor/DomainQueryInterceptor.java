package com.github.domainhelper.interceptor;

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
import com.github.domainhelper.sql.helper.DomainHelper;
 
@Intercepts(@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class DomainQueryInterceptor implements Interceptor {
	
	private ConfigurationService configurationService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if(DomainHelper.get() == null) {
			return invocation.proceed();
		}
		try {
			Object[] args = invocation.getArgs();
	        MappedStatement ms = (MappedStatement) args[0];
	        Object parameterObject = args[1];
	        RowBounds rowBounds = (RowBounds) args[2];
	        ResultHandler resultHandler = (ResultHandler) args[3];
	        Executor executor = (Executor) invocation.getTarget();
	        BoundSql boundSql = ms.getBoundSql(parameterObject);
	        String domainSql = generatorSQL(boundSql.getSql(), parameterObject);
	        BoundSql domainBoundSql = new BoundSql(ms.getConfiguration(), domainSql, boundSql.getParameterMappings(), parameterObject);
	        //可以对参数做各种处理
	        CacheKey cacheKey = executor.createCacheKey(ms, parameterObject, rowBounds, domainBoundSql);
	        return executor.query(ms, parameterObject, rowBounds, resultHandler, cacheKey, domainBoundSql);
		}finally{
			DomainHelper.finishHelper();
		}
	}
	
	 /**
	  * SQL重建
	  * @param originSQL
	  * @param parameterObject
	  * @return
	  */
	private String generatorSQL(String originSQL,Object parameterObject ) {
		String queryDomainSql = originSQL.toLowerCase();
		 
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
