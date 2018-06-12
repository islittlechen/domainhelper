package com.github.domainhelper.sql.helper;

public class DomainHelper {
	
	private static ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();

	public static void needDomainHelper(Object currentUserInfo) {
		threadLocal.set(currentUserInfo);
	}
	
	public static void finishHelper() {
		threadLocal.remove();
	}
	
	public static Object get() {
		return threadLocal.get();
	}
}
