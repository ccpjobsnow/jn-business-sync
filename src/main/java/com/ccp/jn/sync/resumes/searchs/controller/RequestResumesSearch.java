package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnBusinessTopic;

public class RequestResumesSearch {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;
	
	public Map<String, Object> execute(String recruiter, String searchType, Map<String, Object> json){
		
		JnBusinessEntity entity = JnBusinessEntity.valueOf(searchType);
		
		CcpMapDecorator values = new CcpMapDecorator(json).put("recruiter", recruiter);

		entity.save(values);
		
		String searchId = entity.getId(values);
		
		CcpMapDecorator put = values.put("searchType", searchType).put("searchId", searchId);

		this.mensageriaSender.send(put.asJson(), JnBusinessTopic.saveResumesQuery);
		
		return put.content;
		
	}



}
