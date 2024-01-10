package com.ccp.jn.sync.business;

import java.util.Map;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopic;

public class JnSyncBusinessContactUs {
	
	public CcpJsonRepresentation saveContactUs (Map<String, Object> json){
		CcpJsonRepresentation save = new JnEntityContactUs().createOrUpdate(new CcpJsonRepresentation(json));
		CcpJsonRepresentation send = JnTopic.notifyContactUs.send(save);
		return send;
	}

	public void verifyContactUs(String sender, String subjectType) {
		// TODO Auto-generated method stub
		
	}
}
