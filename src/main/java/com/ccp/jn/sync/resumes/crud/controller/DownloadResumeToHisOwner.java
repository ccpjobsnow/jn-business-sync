package com.ccp.jn.sync.resumes.crud.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.sync.business.DownloadThisResumeToHisOwner;
import com.jn.commons.JnBusinessEntity;


public class DownloadResumeToHisOwner {
	
	@CcpDependencyInject
	private CcpFileBucket bucket;
	
	@CcpDependencyInject
	private CcpCache cache;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String email, String viewType){

		CcpMapDecorator values = new CcpMapDecorator().put("email", email).put("viewType", viewType);
		
		DownloadThisResumeToHisOwner action = new DownloadThisResumeToHisOwner(this.bucket, this.cache);
		
		CcpMapDecorator put = this.crud
		.useThisId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInTable(JnBusinessEntity.candidate).executeAction(action).and()
			.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.candidate).returnStatus(404).andFinally()
		.endThisProcedureRetrievingTheResultingData();


		return put.content;
	}
}
