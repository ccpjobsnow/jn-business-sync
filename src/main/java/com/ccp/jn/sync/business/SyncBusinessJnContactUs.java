package com.ccp.jn.sync.business;

import java.util.Map;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.business.utils.JnSyncMensageriaSender;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncBusinessJnContactUs {
	
	public CcpJsonRepresentation saveContactUs (Map<String, Object> json){
		CcpJsonRepresentation values = new CcpJsonRepresentation(json);
		CcpJsonRepresentation save = JnEntityContactUs.INSTANCE.createOrUpdate(values);
		CcpJsonRepresentation send = JnSyncMensageriaSender.INSTANCE.send(save, JnAsyncBusiness.notifyContactUs);
		return send;
	}

	public void verifyContactUs(String sender, String subjectType) {
		// TODO Auto-generated method stub
		
	}
}
