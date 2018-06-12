package com.github.domainhelper.sql.domain;

import java.util.List;

public class UserDomain {

	private boolean admin;
	
	private List<?> domains;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<?> getDomains() {
		return domains;
	}

	public void setDomains(List<?> domains) {
		this.domains = domains;
	}
	
	
}
