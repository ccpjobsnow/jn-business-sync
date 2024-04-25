package com.ccp.jn.sync.business;

import java.util.Map;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.business.utils.JnSyncMensageriaSender;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopics;

public class SyncBusinessJnContactUs {
	
	public CcpJsonRepresentation saveContactUs (Map<String, Object> json){
		CcpJsonRepresentation values = new CcpJsonRepresentation(json);
		CcpJsonRepresentation save = JnEntityContactUs.INSTANCE.createOrUpdate(values);
		JnSyncMensageriaSender jnMensageria = new JnSyncMensageriaSender();
		CcpJsonRepresentation send = jnMensageria.send(save, JnTopics.notifyContactUs);
		return send;
	}

	public void verifyContactUs(String sender, String subjectType) {
		// TODO Auto-generated method stub
		
	}
}
