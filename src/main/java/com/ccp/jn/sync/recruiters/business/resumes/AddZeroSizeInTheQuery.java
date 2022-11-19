package com.ccp.jn.sync.recruiters.business.resumes;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;

public class AddZeroSizeInTheQuery implements CcpProcess {

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		Integer from = values.getAsIntegerNumber("from");
		
		if(from == null) {
			from = 0;
		}
		
		Integer size = values.getAsIntegerNumber("size");
		
		if(size == null) {
			size = 50;
		}
		Must must = new ElasticQuery().setFrom(from).setSize(size).startQuery().startBool().startMust();
		return values.put("_must", must);
	}

}
