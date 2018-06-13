package com.github.domainhelper.sql.processor;

import com.github.domainhelper.sql.domain.UserDomain;

public interface DomainProcessor {

	public UserDomain processor(Object paramObject);
}
