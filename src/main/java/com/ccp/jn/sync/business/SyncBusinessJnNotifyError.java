package com.ccp.jn.sync.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.process.CcpAsyncProcess;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnTopics;

public class SyncBusinessJnNotifyError implements Function<Throwable, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(Throwable e) {
		CcpJsonRepresentation md = new CcpJsonRepresentation(e);
		CcpJsonRepresentation send = new CcpAsyncProcess().send(md, JnTopics.notifyError.getTopicName(), new JnEntityAsyncTask());
		return send;
	}
	
}
