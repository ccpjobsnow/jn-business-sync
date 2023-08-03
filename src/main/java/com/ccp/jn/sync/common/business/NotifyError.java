package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.jn.commons.JnTopic;

public class NotifyError {

	public CcpMapDecorator apply(Throwable e) {
		CcpMapDecorator md = new CcpMapDecorator(e);
		CcpMapDecorator send = JnTopic.notifyError.send(md);
		return send;
	}
	
}
