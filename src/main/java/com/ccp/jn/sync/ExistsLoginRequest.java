package com.ccp.jn.sync;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class ExistsLoginRequest implements CcpProcess{

	@CcpEspecification
	private CcpMensageriaSender mensageriaSender;
	
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		JnBusinessEntity loginRequest = JnBusinessEntity.login_request;
	
		String id = loginRequest.getId(values);
		
		CcpDbCrud crud = loginRequest.getCrud();
		
		crud.findById(values, id, 
				    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("action", valores -> this.mensageriaSender.send(values, JnBusinessTopic.SendUserToken))
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				);
		
		
		return values;
	}
}
