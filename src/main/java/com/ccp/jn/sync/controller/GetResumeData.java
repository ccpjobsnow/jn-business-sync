package com.ccp.jn.sync.controller;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;

public class GetResumeData implements CcpProcess{
	
	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		CcpMapDecorator data = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.candidate.name());
		return data;
	}

}
