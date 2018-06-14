package domainhelper.test;

import java.util.Arrays;

import com.github.domainhelper.sql.domain.UserDomain;
import com.github.domainhelper.sql.processor.DomainProcessor;

public class CompanyDomainProcessor implements DomainProcessor {

	@Override
	public UserDomain processor(Object paramObject) { 
		UserDomain userDomain = new UserDomain();
		userDomain.setAdmin(false);
		userDomain.setDomains(Arrays.asList(1,2));
		return userDomain;
	}

}
