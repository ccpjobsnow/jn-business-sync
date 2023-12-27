package com.ccp.jn.sync.business;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopic;

public class JnSyncBusinessContactUs {
	
	public CcpMapDecorator saveContactUs (Map<String, Object> json){
		CcpMapDecorator save = new JnEntityContactUs().createOrUpdate(new CcpMapDecorator(json));
		CcpMapDecorator send = JnTopic.notifyContactUs.send(save);
		return send;
	}

	public void verifyContactUs(String sender, String subjectType) {
		// TODO Auto-generated method stub
		
	}
}
