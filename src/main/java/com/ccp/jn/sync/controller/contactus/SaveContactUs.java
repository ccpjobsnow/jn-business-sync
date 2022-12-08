package com.ccp.jn.sync.controller.contactus;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;

public class SaveContactUs {
	
	@CcpSpecification
	private CcpDbCrud crud;

	@CcpSpecification
	private CcpMensageriaSender mensageriaSender;

	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator save = JnBusinessEntity.contact_us.save(new CcpMapDecorator(json));
		return save.content;
	}
}
