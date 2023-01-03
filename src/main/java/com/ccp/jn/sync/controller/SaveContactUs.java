package com.ccp.jn.backend.site.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class SaveContactUs {
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator save = JnBusinessEntity.contact_us.save(new CcpMapDecorator(json));
		this.mensageriaSender.send(save, JnBusinessTopic.notifyContactUs);
		return save.content;
	}
}
