package com.github.domainhelper.sql.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.util.JdbcConstants;
import com.github.domainhelper.configure.ConfigurationService;
import com.github.domainhelper.configure.DomainConfiguration;
import com.github.domainhelper.sql.domain.UserDomain;
import com.github.domainhelper.sql.keyword.MysqlKeyWord;
import com.github.domainhelper.sql.parser.DomainSegInsertPositionParser;
import com.github.domainhelper.sql.parser.SQLParser;
import com.github.domainhelper.sql.parser.TableAliasSQLParser;
import com.github.domainhelper.sql.util.MySQLFormatUtil;

public class DomainSQLBuilder {

	
	public static final String builder(String sql,ConfigurationService configurationService,Object paramObject) {
		
		ConcurrentHashMap<String, DomainConfiguration> configMap = configurationService.getConfiguration();
		
		/*测试数据
		ConcurrentHashMap<String, DomainConfiguration> configMap = new ConcurrentHashMap<>();
		DomainConfiguration domainConfiguration = new DomainConfiguration();
		domainConfiguration.setDomainTableName("T_C");
		domainConfiguration.setDomainColumnName("DOMAIN");
		domainConfiguration.setDomainProcessor(new CompanyDomainProcessor());
		configMap.put("T_C", domainConfiguration);
		domainConfiguration = new DomainConfiguration();
		domainConfiguration.setDomainTableName("T_A");
		domainConfiguration.setDomainColumnName("DOMAIN_A");
		domainConfiguration.setDomainProcessor(new CompanyDomainProcessor());
		configMap.put("T_A", domainConfiguration);
		*/
		
		SQLSelectStatement stm = (SQLSelectStatement)SQLParser.parser(sql);
		HashMap<String, String> needReplaceMap = new HashMap<String, String>();
		SQLSelect select = stm.getSelect();
		
		List<String> order = new ArrayList<String>();
		regression(order,needReplaceMap, select.getQuery(), configMap, paramObject);
		judgeAndGenerate(order,needReplaceMap, select.toString(), configMap, paramObject);
		
		return mergeSQL(sql, order, needReplaceMap);
	}

	private static final String mergeSQL(String sql,List<String> order,HashMap<String, String> needReplaceMap) {
		sql = SQLUtils.format(sql, JdbcConstants.MYSQL);
		String resultSql = MySQLFormatUtil.formatWithWhitespace(sql);
		for(String rpsql:order) {
			String rpsqlFormat = MySQLFormatUtil.formatWithWhitespace(rpsql).trim();
			String destsqlFormat =  MySQLFormatUtil.formatWithWhitespace(needReplaceMap.get(rpsql)).trim();
			if(resultSql.equals(resultSql)){
				return destsqlFormat;
			}
			while(resultSql.contains(rpsqlFormat)) {
				int index = resultSql.indexOf(rpsqlFormat);
				resultSql = resultSql.substring(0, index) + destsqlFormat + resultSql.substring(index+rpsqlFormat.length());
			}
		} 
		return resultSql;
	}
	
	private static final void regression(List<String> order,HashMap<String, String> needReplaceMap,SQLSelectQuery selectQuery,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		if(selectQuery instanceof SQLUnionQuery) {
			SQLUnionQuery sqlUnionQuery = (SQLUnionQuery)selectQuery;
			SQLSelectQuery leftSelectQuery = sqlUnionQuery.getLeft();
			regression(order,needReplaceMap, leftSelectQuery, configMap, paramObject);
			SQLSelectQuery rightSelectQuery = sqlUnionQuery.getRight();
			regression(order,needReplaceMap, rightSelectQuery, configMap, paramObject);
		}else {
			SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock)selectQuery;
			SQLTableSource sqlTableSource = selectQueryBlock.getFrom();
			regressionJoinQuery(order,needReplaceMap, sqlTableSource, configMap, paramObject);
		} 
	}
	
	private static final void regressionJoinQuery(List<String> order,HashMap<String, String> needReplaceMap, SQLTableSource sqlTableSource,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		if(null == sqlTableSource){
			return;
		}
		String sql = sqlTableSource.toString();
		if(sqlTableSource instanceof SQLExprTableSource) {
			//最底层SQL
			judgeAndGenerate(order,needReplaceMap, sql, configMap, paramObject);
		}else if(sqlTableSource instanceof SQLSubqueryTableSource) {
			
			regression(order,needReplaceMap, ((SQLSubqueryTableSource) sqlTableSource).getSelect().getQuery(), configMap, paramObject);
			judgeAndGenerate(order,needReplaceMap, sql, configMap, paramObject);
		}else if(sqlTableSource instanceof SQLJoinTableSource) {
			regressionJoinQuery(order,needReplaceMap, ((SQLJoinTableSource)sqlTableSource).getLeft(), configMap, paramObject);
			judgeAndGenerate(order,needReplaceMap, sql, configMap, paramObject);
			
			regressionJoinQuery(order,needReplaceMap, ((SQLJoinTableSource)sqlTableSource).getRight(), configMap, paramObject);
			judgeAndGenerate(order,needReplaceMap, sql, configMap, paramObject);
		}else {
			//SQLWithSubqueryClause
			judgeAndGenerate(order,needReplaceMap, sql, configMap, paramObject);
		}
	}
	
	private static void judgeAndGenerate(List<String> order,HashMap<String, String> needReplaceMap,String sql,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		if(!isInnerReplace(needReplaceMap, sql)) {
			generator(order,needReplaceMap, sql, configMap, paramObject);
		}
	}
	
	public static boolean isInnerReplace(HashMap<String, String> needReplaceMap,String sql) {
		for(String key:needReplaceMap.keySet()) {
			if(sql.contains(key)) {
				return true;
			}
		}
		return false;
	}
	
	private static final String generator(List<String> order,HashMap<String, String> needReplaceMap,String sql,ConcurrentHashMap<String, DomainConfiguration> configMap,Object paramObject) {
		Iterator<String> iterator = configMap.keySet().iterator();
		while(iterator.hasNext()) {
			String tableName = iterator.next();
			if(needApply(sql,tableName)) {
				String applysql = apply(sql,configMap.get(tableName),paramObject);
				if(!applysql.equals(sql)) {
					if(!order.contains(sql)) {
						order.add(sql);
						needReplaceMap.put(sql, applysql);
					}
				}
				break;
			}
		}
		return sql;
	}

	private static final boolean needApply(String sql,String tableName){
		int index = sql.indexOf(tableName);
		if( index > 0 && (sql.charAt(index-1) == ' ' || sql.charAt(index-1) == ',')){
			if(sql.length() == index + tableName.length()){
				return true;
			}
			if(sql.charAt(index+tableName.length()) == ' ' || sql.charAt(index+tableName.length()) == ','
		      || sql.charAt(index+tableName.length()) == '\n' || sql.charAt(index+tableName.length()) == '\t'){
				return  true;
			}
		}
		return false;
	}
	
	private static final String apply(String sql,DomainConfiguration configuration,Object paramObject) {
		if(sql.indexOf(MysqlKeyWord.SELECT.getUpperCase()) == -1) {
			return sql;
		}
		UserDomain userDomain = configuration.getDomainProcessor().processor(paramObject);
		if(userDomain.isAdmin()) {
			return sql;
		}
		StringBuilder domainCondition = new StringBuilder(" ");
		int location = DomainSegInsertPositionParser.location(sql);
		
		if(sql.indexOf(MysqlKeyWord.WHERE.getUpperCase()) == -1) {
			domainCondition.append(MysqlKeyWord.WHERE.getUpperCase()+" ");
		}else {
			domainCondition.append(" "+MysqlKeyWord.AND.getUpperCase()+" ");
		}
		
		String aliasName = TableAliasSQLParser.parseAlias(sql, configuration.getDomainTableName());
		if(null != aliasName && aliasName.trim().length() > 0) {
			domainCondition.append(aliasName).append(".");
		}
		domainCondition.append(configuration.getDomainColumnName()).append(" ").append(MysqlKeyWord.IN.getUpperCase()).append(" (");
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
	 
}
