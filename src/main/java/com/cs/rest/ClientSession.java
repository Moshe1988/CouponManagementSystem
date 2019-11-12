package com.cs.rest;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cs.rest.service.AbsService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientSession {

	/**
	 * This Class is describing a client session that contains: 
	 * service - in order to perform all actions for the user. 
	 * lastAccessedMillis - in order to perform auto logout after same time.
	 */
	private AbsService service;
	private long lastAccessedMillis;

	public AbsService getService() {
		return service;
	}

	public void setService(AbsService service) {
		this.service = service;
	}

	public long getLastAccessedMillis() {
		return lastAccessedMillis;
	}

	public void accessed() {
		this.lastAccessedMillis = System.currentTimeMillis();
	}
}
