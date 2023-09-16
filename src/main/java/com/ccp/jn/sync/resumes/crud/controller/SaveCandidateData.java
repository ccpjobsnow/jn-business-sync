package com.ccp.jn.sync.resumes.crud.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.utils.JnTopic;

public class SaveCandidateData {

	
	public CcpMapDecorator apply(Map<String, Object> json){
		CcpMapDecorator send = JnTopic.saveCandidateData.send(new CcpMapDecorator(json));
		return send;
	}
	
}
