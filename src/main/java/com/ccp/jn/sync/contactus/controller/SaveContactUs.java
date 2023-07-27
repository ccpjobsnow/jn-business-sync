package com.ccp.jn.sync.contactus.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDao;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SaveContactUs {
	
	@CcpDependencyInject
	private CcpDao crud;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator save = JnEntity.contact_us.createOrUpdate(new CcpMapDecorator(json));
		this.mensageriaSender.send(save, JnTopic.notifyContactUs);
		return save.content;
	}
}
