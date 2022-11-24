package com.ccp.jn.sync.recruiters.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpSpecification;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.jn.sync.recruiters.business.resumes.AddFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddGroupByCriteria;
import com.ccp.jn.sync.recruiters.business.resumes.AddOptionalKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddRequiredKeywordsFilter;
import com.ccp.jn.sync.recruiters.business.resumes.AddSizeInTheQuery;
import com.ccp.jn.sync.recruiters.business.resumes.AddSortCriteria;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesList;
import com.ccp.jn.sync.recruiters.business.resumes.GetResumesStatis;
import com.jn.commons.tables.fields.A3D_candidate;

public class ListResumesStats {

	@CcpSpecification
	private CcpDbQueryExecutor requestExecutor;

	public List<Map<String, Object>> execute(String recruiter, String json){
		
		CcpMapDecorator values = this.createSelect(json);
		
		values = this.createWhere(values);

		List<Map<String, Object>> extractResults = this.extractResults(values);

		return extractResults;
	}

	private List<Map<String, Object>> extractResults(CcpMapDecorator values) {
		
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new GetResumesStatis(this.requestExecutor));
		
		values = values.whenHasKey(A3D_candidate.ddd.name(), new GetResumesList(this.requestExecutor));
		
		List<Map<String, Object>> results = values.getAsMapList("results").stream().map(x -> x.content).collect(Collectors.toList());
	
		return results;
	}

	private CcpMapDecorator createWhere(CcpMapDecorator values) {
		
		values = values.whenHasKey(A3D_candidate.seniority.name(), new AddFilter(A3D_candidate.seniority));
		values = values.whenHasKey(A3D_candidate.pcd.name(), new AddFilter(A3D_candidate.pcd));
		values = values.whenHasKey(A3D_candidate.ddd.name(), new AddFilter(A3D_candidate.ddd));
		values = values.whenHasKey("requiredKeywords", new AddRequiredKeywordsFilter());
		values = values.whenHasKey("optionalKeywords", new AddOptionalKeywordsFilter());
		
		return values;
	}

	private CcpMapDecorator createSelect(String json) {
		CcpMapDecorator values = new CcpMapDecorator(json);
		
		values = values.getTransformed(new AddSizeInTheQuery());
		values = values.whenHasKey("sort", new AddSortCriteria());
		values = values.whenHasNotKey(A3D_candidate.ddd.name(), new AddGroupByCriteria());
		return values;
	}
}
