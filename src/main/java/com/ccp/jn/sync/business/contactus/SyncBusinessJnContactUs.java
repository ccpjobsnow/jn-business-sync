package com.ccp.jn.sync.business.contactus;

import java.util.Map;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnAsyncBusiness;
import com.jn.sync.mensageria.JnSyncMensageriaSender;

public class SyncBusinessJnContactUs {
	
	public CcpJsonRepresentation saveContactUs (Map<String, Object> json){
		CcpJsonRepresentation jsonDecorator = new CcpJsonRepresentation(json);
		CcpJsonRepresentation save = JnEntityContactUs.ENTITY.createOrUpdate(jsonDecorator);
		CcpJsonRepresentation send = new JnSyncMensageriaSender(JnAsyncBusiness.notifyContactUs).apply(save);
		return send;
	}

	public SyncBusinessJnContactUs verifyContactUs(String sender, String subjectType) {
		// LATER Auto-generated method stub
		return this;
	}
}
