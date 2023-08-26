package com.ccp.jn.sync.contactus.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class SaveContactUs {
	
	public CcpMapDecorator execute (Map<String, Object> json){
		CcpMapDecorator save = JnEntity.contact_us.createOrUpdate(new CcpMapDecorator(json));
		CcpMapDecorator send = JnTopic.notifyContactUs.send(save);
		return send;
	}
}
