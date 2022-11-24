package com.ccp.jn.sync.recruiters.business.resumes;

import java.util.List;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;

public class AddSortCriteria implements CcpProcess{

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		Must must = values.getAsObject("_must");
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		CcpMapDecorator sort = values.getInternalMap("sort");
		
		query = this.addSorting(query, sort, "desc");
		query = this.addSorting(query, sort, "asc");
		
		must = query.startQuery().startBool().startMust();
		
		CcpMapDecorator put = values.put("_must", must);
		
		return put;
	}

	private ElasticQuery addSorting(ElasticQuery query, CcpMapDecorator sort, String sortType) {
		List<String> list = sort.getAsStringList(sortType);
		String[] field = new String[list.size()];
		list.toArray(field);
		query = query.addSorting(sortType,  field);
		return query;
	}

	
	
 
}
