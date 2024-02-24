package com.ccp.jn.sync.business;

import java.util.Map;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.process.CcpAsyncProcess;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopics;

public class SyncBusinessJnContactUs {
	
	public CcpJsonRepresentation saveContactUs (Map<String, Object> json){
		CcpJsonRepresentation save = new JnEntityContactUs().createOrUpdate(new CcpJsonRepresentation(json));
		CcpJsonRepresentation send = new CcpAsyncProcess().send(save, JnTopics.notifyContactUs.getTopicName(), new JnEntityAsyncTask());
		return send;
	}

	public void verifyContactUs(String sender, String subjectType) {
		// TODO Auto-generated method stub
		
	}
}
