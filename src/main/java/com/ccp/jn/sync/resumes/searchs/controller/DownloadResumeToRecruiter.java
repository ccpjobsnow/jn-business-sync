package com.ccp.jn.sync.resumes.searchs.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.cache.CcpCache;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.sync.common.business.DownloadResumeToRecruiterAction;
import com.jn.commons.JnBusinessEntity;


public class DownloadResumeToRecruiter {
	
	@CcpDependencyInject
	private CcpFileBucket bucket;
	
	@CcpDependencyInject
	private CcpCache cache;
	
	@CcpDependencyInject
	private CcpDbCrud crud;

	
	public Map<String, Object> execute (String resume, String recruiter, String viewType){
	
		CcpMapDecorator values = new CcpMapDecorator().put("resume", resume).put("recruiter", recruiter).put("viewType", viewType);

		DownloadResumeToRecruiterAction action = new DownloadResumeToRecruiterAction(this.bucket, this.cache);
		
		CcpMapDecorator result = this.crud.useThisId(values)
		.toBeginProcedureAnd()
		.ifThisIdIsPresentInTable(JnBusinessEntity.denied_view_to_recruiter).returnStatus(403).and()
		.ifThisIdIsNotPresentInTableThen(JnBusinessEntity.candidate_resume).returnStatus(404).and()
		.ifThisIdIsPresentInTable(JnBusinessEntity.candidate_resume).executeAction(action).andFinally()
		.endThisProcedureRetrievingTheResultingData();
		
		return result.content;

	}
}
