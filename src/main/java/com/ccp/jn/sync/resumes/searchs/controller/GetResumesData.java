package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnEntity;

public class GetResumesData {
	
	public CcpMapDecorator apply(String searchType, Map<String, Object> json){
	
		JnEntity entity = JnEntity.valueOf(searchType.replace("search_", ""));
		
		CcpMapDecorator query = new CcpMapDecorator(json);

		entity.createOrUpdate(query);
		
		CcpMapDecorator result = entity.getOneById(query);
		
		return result;
	}

}
