package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.jn.commons.entities.JnBaseEntity;

public class GetResumesData {
	
	public CcpMapDecorator apply(String searchType, Map<String, Object> json){
	
		CcpEntity entity = JnBaseEntity.valueOf(searchType);

		CcpMapDecorator query = new CcpMapDecorator(json);

		entity.createOrUpdate(query);
		
		CcpMapDecorator result = entity.getOneById(query);
		
		return result;
	}

}
