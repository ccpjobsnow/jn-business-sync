package com.ccp.jn.sync.controller.resumes;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessTopic;

public class SaveCandidateData {

	@CcpSpecification
	private CcpMensageriaSender mensageriaSender;
	
	public void execute(Map<String, Object> json){
		this.mensageriaSender.send(new CcpMapDecorator(json), JnBusinessTopic.saveCandidateData);
	}
	
}
