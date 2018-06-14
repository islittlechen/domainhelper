# domainhelper

【注意】目前SQL解析还不全面，仅支持MYSQL数据库查询和符合SQL92标准规范的数据库查询语句。

使用实例
1.mybatis配置
  <bean id="companyDomainProcessor" class="com.achilles.adminCommon.util.CompanyDomainProcessor"/>

	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="typeAliasesPackage" value="com.achilles.adminCommon.mapper" />
        <property name="mapperLocations" value="classpath*:com/achilles/adminCommon/mapper/*.xml"/>
        <property name="plugins">
        		 <array>
                <bean class="com.github.domainhelper.interceptor.DomainQueryInterceptor">
        		 		<property name="configurationService">
        		 			<bean class="com.github.domainhelper.configure.DefaultConfigurationService">
        		 				<property name="configuration">
        		 					<map>
	        		 				   <entry key="res_area">
	        		 				   		<bean class="com.github.domainhelper.configure.DomainConfiguration">
	        		 				   			<property name="domainTableName" value="res_area" /> <!--需要分域的表名-->
	        		 				   			<property name="domainColumnName" value="belong_company_id" /><!--需要分域的字段名-->
	        		 				   			<property name="domainProcessor" ref="companyDomainProcessor" /> <!--当前用户域回调-->
	        		 				   		</bean>
	        		 				   </entry>
	        		 				</map>
        		 				</property>
        		 			</bean>
        		 		</property>
        		 	</bean>
            </array>
        </property>
	</bean>
  
2.在数据访问前需调用
    DomainHelper.needDomainHelper(condition.getUser());
  将当前用户信息保存到ThreadLocal中。SQL Query Executor拦截器在执行完后会自己移除。多次执行拦截器存在问题，因为在最后一个拦截器执行完会主动移除。
  
3.管理域回调，需要实现DomainProcessor接口，例如：

import com.github.domainhelper.sql.domain.UserDomain;
import com.github.domainhelper.sql.helper.DomainHelper;
import com.github.domainhelper.sql.processor.DomainProcessor;

public class CompanyDomainProcessor implements DomainProcessor {

	@Override
	public UserDomain processor() {
		UserInfo userInfo = (UserInfo)DomainHelper.get();
		UserDomain userDomain = new UserDomain();
		userDomain.setAdmin(userInfo.getIsAdmin()==1);
		userDomain.setDomains(userInfo.getMgtCompany());
		return userDomain;
	}
}

4.原理总结
  通过mybatis的SQL拦截器拦截用户查询sql，通过SQL匹配目标数据表的DomainConfiguration配置，解析sql，并将管理域查询条件动态的插入到原始sql中，从而实现分域查询效果。如果你有多个SQL拦截器，则此拦截器则需按实际情况配置顺序。
