package com.ccp.jn.sync.resumes.crud.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.jn.sync.common.business.DownloadThisResumeToHisOwner;
import com.jn.commons.JnBusinessEntity;


public class DownloadResumeToHisOwner {
	
	private final DownloadThisResumeToHisOwner action = CcpDependencyInjection.getInjected(DownloadThisResumeToHisOwner.class);
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		
		CcpMapDecorator put = this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnBusinessEntity.candidate).executeAction(this.action).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.candidate).returnStatus(404).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put.content;
	}
}
