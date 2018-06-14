package com.github.domainhelper.sql.parser;

import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.github.domainhelper.sql.builder.DomainSQLBuilder;

public class SQLParser {

	public static SQLStatement parser(String sql) {
		String dbType = JdbcConstants.MYSQL;
		List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
		return statementList.get(0);
	}
	
	public static void main(String[] args) {
		String sql = "select * from (select * from t_a a,t_b b where a.id=b.aid order by a.id desc) limit 10 union select * from (select * from t_c c where c.id=1) tc join (select * from t_d where id=2) td on tc.id=td.id";
		
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
		
		sql = "select * from (select * from t_a a,t_b b where a.id=b.aid order by a.id desc) limit 10";
		
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
		
		sql = "select * from (select * from t_c c where c.id=1) tc join (select * from t_d where id=2) td on tc.id=td.id";
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
		
		sql = "select * from t_a a,t_b b where a.id=b.aid order by a.id desc";
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
	
		sql = "select a,sum(b) from t_a a,t_b b where a.id=b.aid group by a";
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
		
		sql = "select a,sum(b) from t_a ,t_b  where id=aid group by a";
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
		
		sql = "select a,sum(b) from t_a where id=1 group by a";
		System.out.println(DomainSQLBuilder.builder(sql.toUpperCase(), null, null));
	}
}
