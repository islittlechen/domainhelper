package com.github.domainhelper.sql.builder;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.github.domainhelper.configure.ConfigurationService;
import com.github.domainhelper.configure.DomainConfiguration;
import com.github.domainhelper.sql.domain.UserDomain;
import com.github.domainhelper.sql.keyword.MysqlKeyWord;
import com.github.domainhelper.sql.parser.DomainSegInsertPositionParser;
import com.github.domainhelper.sql.parser.SQLParser;
import com.github.domainhelper.sql.parser.SQLSegment;
import com.github.domainhelper.sql.parser.TableAliasSQLParser;

public class DomainSQLBuilder {

	public static final String builder(String sql,ConfigurationService configurationService,Object paramObject) {
		String resultSql = "";
		if(sql.indexOf(MysqlKeyWord.UNION.getLowCase()) == -1) {
			SQLSegment segment = SQLParser.parser(sql);
			ConcurrentHashMap<String, DomainConfiguration> configMap = configurationService.getConfiguration();
			BuilderSegment builderSeg = regression(segment,configMap,paramObject);
			return builderSeg.applySql;
		}else {
			String[] subSqls = sql.split(MysqlKeyWord.UNION.getLowCase());
			for(String subSql:subSqls) {
				SQLSegment segment = SQLParser.parser(subSql);
				ConcurrentHashMap<String, DomainConfiguration> configMap = configurationService.getConfiguration();
				BuilderSegment builderSeg = regression(segment,configMap,paramObject);
				if(resultSql.length() > 0) {
					resultSql += " "+MysqlKeyWord.UNION.getLowCase()+" ";
				}
				resultSql += builderSeg.applySql;
			}
			return resultSql;
		}
	}
	
	private static final BuilderSegment regression(SQLSegment segment,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		
		if(segment.getInnerSegment() != null) {
			BuilderSegment inner = regression(segment.getInnerSegment(),configMap,paramObject);
			String currentSql = segment.getPrifixSql()+inner.applySql+segment.getSuffixSql();
			if(!inner.applyed) {
				String applySql = generator(currentSql, configMap,paramObject);
				inner.applySql = applySql;
			}else {
				inner.applySql = currentSql;
			}
			return inner;
		}else {
			String prefixSql = segment.getPrifixSql();
			String applySql = generator(prefixSql, configMap,paramObject);
			BuilderSegment current = new BuilderSegment();
			if(applySql.equals(prefixSql)) {
				current.applyed = false;
			}else{
				current.applyed = true;
			}
			current.applySql = applySql;
			return current;
		}
		 
	}
	
	private static final String generator(String sql,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		Iterator<String> iterator = configMap.keySet().iterator();
		while(iterator.hasNext()) {
			String tableName = iterator.next();
			if(sql.indexOf(tableName) != -1) {
				sql = apply(sql,configMap.get(tableName),paramObject);
				break;
			}
		}
		return sql;
	}
	
	private static final String apply(String sql,DomainConfiguration configuration,Object paramObject) {
		UserDomain userDomain = configuration.getDomainProcessor().processor(paramObject);
		if(userDomain.isAdmin()) {
			return sql;
		}
		StringBuilder domainCondition = new StringBuilder(" ");
		int location = DomainSegInsertPositionParser.location(sql);
		
		if(sql.indexOf(MysqlKeyWord.WHERE.getLowCase()) == -1) {
			domainCondition.append(MysqlKeyWord.WHERE.getLowCase());
		}else {
			domainCondition.append(" "+MysqlKeyWord.AND.getLowCase()+" ");
		}
		
		String aliasName = TableAliasSQLParser.parseAlias(sql, configuration.getDomainTableName());
		if(null != aliasName) {
			domainCondition.append(aliasName).append(".");
		}
		domainCondition.append(configuration.getDomainColumnName()).append(" ").append(MysqlKeyWord.IN.getLowCase()).append(" (");
		boolean flag = false;
		for(Object id: userDomain.getDomains()) {
			 if(flag) {
				 domainCondition.append(",");
			 }else {
				 flag = true;
			 }
			 if(id instanceof String) {
				 domainCondition.append("'").append(id.toString()).append("'");
			 }else {
				 domainCondition.append(id.toString());
			 }
		}
		domainCondition.append(")");
		if(location == -1) {
			sql += domainCondition.toString();
		}else {
			sql = sql.substring(0, location)+" "+domainCondition.toString()+" "+sql.substring(location);
		}
		return sql+" ";
	}
	
	private static class BuilderSegment{
		
		boolean applyed;
		
		String applySql;
	}
}
