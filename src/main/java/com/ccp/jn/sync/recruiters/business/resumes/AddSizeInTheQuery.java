package com.ccp.jn.sync.recruiters.business.resumes;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;

public class AddSizeInTheQuery implements CcpProcess {

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		Must must = new ElasticQuery().setSize(0).startQuery().startBool().startMust();
		return values.put("_must", must);
	}

}
