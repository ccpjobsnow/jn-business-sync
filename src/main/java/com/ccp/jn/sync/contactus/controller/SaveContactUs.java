package com.ccp.jn.sync.contactus.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.entities.JnEntityContactUs;
import com.jn.commons.utils.JnTopic;

public class SaveContactUs {
	
	public CcpMapDecorator execute (Map<String, Object> json){
		CcpMapDecorator save = new JnEntityContactUs().createOrUpdate(new CcpMapDecorator(json));
		CcpMapDecorator send = JnTopic.notifyContactUs.send(save);
		return send;
	}
}
