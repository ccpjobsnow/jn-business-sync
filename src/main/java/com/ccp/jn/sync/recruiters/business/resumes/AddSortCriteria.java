package com.ccp.jn.sync.recruiters.business.resumes;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;

public class AddSortCriteria implements CcpProcess{

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		Must must = values.getAsObject("must");
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		return null;
	}

}
