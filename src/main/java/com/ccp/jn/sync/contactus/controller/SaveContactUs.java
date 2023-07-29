package com.ccp.jn.sync.contactus.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SaveContactUs {
	
	@CcpDependencyInject
	private CcpDao crud;


	public CcpMapDecorator execute (Map<String, Object> json){
		
		CcpMapDecorator save = JnEntity.contact_us.createOrUpdate(new CcpMapDecorator(json));
		CcpMapDecorator send = JnTopic.notifyContactUs.send(save);
		return send;
	}
}
