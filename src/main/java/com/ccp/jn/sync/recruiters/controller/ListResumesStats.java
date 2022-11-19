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
import com.ccp.jn.sync.recruiters.business.resumes.AddZeroSizeInTheQuery;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesList;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesStatis;
import com.jn.commons.tables.fields.A3D_candidate;

public class ListResumesStats {

	
	public Map<String, Object> execute(String recruiter, String json){
		
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new AddZeroSizeInTheQuery());
		values = values.whenHasKey(A3D_candidate.ddd.name(), new AddSizeInTheQuery());
		values = values.whenHasKey("sort", new AddSortCriteria());

		values = values.whenHasKey(A3D_candidate.seniority.name(), new AddSeniorityFilter());
		values = values.whenHasKey("requiredKeywords", new AddRequiredKeywordsFilter());
		values = values.whenHasKey("optionalKeywords", new AddOptionalKeywordsFilter());
		values = values.whenValueIsTrue(A3D_candidate.pcd.name(), new AddPcdFilter());
		values = values.whenHasKey(A3D_candidate.ddd.name(), new AddDddFilter());


		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new GetResumesStatis());
		values = values.whenHasKey(A3D_candidate.ddd.name(), new GetResumesList());
		
		CcpMapDecorator response = values.getInternalMap("response");
		//sort(exp, pj,clt, btc, update), from, size, ddd
		return response.content;
	}
}
