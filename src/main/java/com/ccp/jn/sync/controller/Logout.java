package com.ccp.jn.sync.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.db.utils.TransferDataBetweenTables;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class Logout {
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	private CcpProcess decisionTree = values ->{
		return new TransferDataBetweenTables(JnBusinessEntity.login, JnBusinessEntity.logout)
				.goToTheNextStep(values).data;
			
	};
	public Map<String, Object> execute (Map<String, Object> json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
	
		this.crud.findById(values, 
			    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("action", this.decisionTree)
				);
		
		return json;
	}
}
