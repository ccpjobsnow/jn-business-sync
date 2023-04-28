package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnBusinessEntity;

public class GetResumesData {
	
	public Map<String, Object> execute(String searchType, String json){
	
		JnBusinessEntity entity = JnBusinessEntity.valueOf(searchType.replace("search_", ""));
		
		CcpMapDecorator query = new CcpMapDecorator(json);

		entity.save(query);
		
		CcpMapDecorator result = entity.get(query);
		
		return result.content;
	}

}
