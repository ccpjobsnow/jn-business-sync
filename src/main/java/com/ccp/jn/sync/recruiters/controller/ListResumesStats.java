package com.ccp.jn.sync.recruiters.controller;

import java.util.Map;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.sync.recruiters.business.resumes.AddFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddOptionalKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddRequiredKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddSizeInTheQuery;
import com.ccp.jn.sync.recruiters.business.resumes.AddSortCriteria;
import com.ccp.jn.sync.recruiters.business.resumes.AddStatisCriteria;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesList;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesStatis;
import com.jn.commons.tables.fields.A3D_candidate;

public class ListResumesStats {

	
	public Map<String, Object> execute(String recruiter, String json){
		
		CcpMapDecorator values = this.defineRequest(json);
		
		values = this.defineFilters(values);

		Map<String, Object> extractResults = this.extractResults(values);

		return extractResults;
	}

	private Map<String, Object> extractResults(CcpMapDecorator values) {
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new GetResumesStatis());
		values = values.whenHasKey(A3D_candidate.ddd.name(), new GetResumesList());
		
		CcpMapDecorator response = values.getInternalMap("response");
		return response.content;
	}

	private CcpMapDecorator defineFilters(CcpMapDecorator values) {
		
		values = values.whenHasKey(A3D_candidate.seniority.name(), new AddFilter(A3D_candidate.seniority));
		values = values.whenHasKey(A3D_candidate.pcd.name(), new AddFilter(A3D_candidate.pcd));
		values = values.whenHasKey(A3D_candidate.ddd.name(), new AddFilter(A3D_candidate.ddd));
		values = values.whenHasKey("requiredKeywords", new AddRequiredKeywordsFilter());
		values = values.whenHasKey("optionalKeywords", new AddOptionalKeywordsFilter());
		
		return values;
	}

	private CcpMapDecorator defineRequest(String json) {
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		values = values.getTransformed(new AddSizeInTheQuery());
		values = values.whenHasKey("sort", new AddSortCriteria());
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new AddStatisCriteria());
		return values;
	}
}
