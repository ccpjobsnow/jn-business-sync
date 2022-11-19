package com.ccp.jn.sync.recruiters.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.sync.recruiters.business.resumes.AddDddFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddOptionalKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddPcdFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddRequiredKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddSeniorityFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddSizeInTheQuery;
import com.ccp.jn.sync.recruiters.business.resumes.AddSortCriteria;
import com.ccp.jn.sync.recruiters.business.resumes.AddStatisSpecification;
import com.ccp.jn.sync.recruiters.business.resumes.AddZeroSizeInTheQuery;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesList;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesStatis;
import com.jn.commons.tables.fields.A3D_candidate;

public class ListResumesStats {

	
	public Map<String, Object> execute(String recruiter, String json){
		
		CcpMapDecorator parameters = new CcpMapDecorator(json);
		
		parameters = parameters.whenHasKey(A3D_candidate.ddd.name(), new AddSizeInTheQuery());
		parameters = parameters.whenHasNotKey(A3D_candidate.ddd.name(), new AddZeroSizeInTheQuery());
		parameters = parameters.whenHasKey("sort", new AddSortCriteria());

		parameters = parameters.whenHasKey(A3D_candidate.seniority.name(), new AddSeniorityFilter());
		parameters = parameters.whenHasKey("requiredKeywords", new AddRequiredKeywordsFilter());
		parameters = parameters.whenHasKey("optionalKeywords", new AddOptionalKeywordsFilter());
		parameters = parameters.whenValueIsTrue(A3D_candidate.pcd.name(), new AddPcdFilter());
		parameters = parameters.whenHasKey(A3D_candidate.ddd.name(), new AddDddFilter());

		parameters = parameters.whenHasNotKey(A3D_candidate.ddd.name(), new AddStatisSpecification());

		parameters = parameters.whenHasNotKey(A3D_candidate.ddd.name(), new GetResumesStatis());
		parameters = parameters.whenHasKey(A3D_candidate.ddd.name(), new GetResumesList());
		
		//sort(exp, pj,clt, btc, update), from, size, ddd
		return null;
	}
}
