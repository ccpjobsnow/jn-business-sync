package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;

public class RequestResumesSearch {

	public CcpMapDecorator apply(String recruiter, String searchType, Map<String, Object> json){
		
		JnEntity entity = JnEntity.valueOf(searchType);
		
		CcpMapDecorator values = new CcpMapDecorator(json).put("recruiter", recruiter);

		entity.createOrUpdate(values);
		
		String searchId = entity.getId(values);
		
		CcpMapDecorator put = values.put("searchType", searchType).put("searchId", searchId);

		CcpMapDecorator result = JnTopic.saveResumesQuery.send(put);
		
		return result;
		
	}



}
