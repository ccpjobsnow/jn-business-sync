package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.commons.utils.JnTopic;

public class JnSyncBusinessNotifyError {

	public CcpJsonRepresentation apply(Throwable e) {
		CcpJsonRepresentation md = new CcpJsonRepresentation(e);
		CcpJsonRepresentation send = JnTopic.jnNotifyError.send(md);
		return send;
	}
	
}
