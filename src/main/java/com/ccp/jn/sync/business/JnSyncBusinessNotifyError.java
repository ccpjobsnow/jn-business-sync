package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.utils.JnTopic;

public class JnSyncBusinessNotifyError {

	public CcpMapDecorator apply(Throwable e) {
		CcpMapDecorator md = new CcpMapDecorator(e);
		CcpMapDecorator send = JnTopic.notifyError.send(md);
		return send;
	}
	
}
