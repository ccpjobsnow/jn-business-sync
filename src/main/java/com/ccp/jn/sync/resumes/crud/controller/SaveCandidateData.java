package com.ccp.jn.sync.resumes.crud.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnTopic;

public class SaveCandidateData {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	public void execute(Map<String, Object> json){
		this.mensageriaSender.send(new CcpMapDecorator(json), JnTopic.saveCandidateData);
	}
	
}
