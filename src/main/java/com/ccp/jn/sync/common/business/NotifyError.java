package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpDependencyInject;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.JnBusinessTopic;

public class NotifyError {

	@CcpDependencyInject
	private CcpMensageriaSender mensageriaSender;

	
	public void execute(Throwable e) {
		CcpMapDecorator md = new CcpMapDecorator(e);
		this.mensageriaSender.send(md, JnBusinessTopic.notifyError);
	}
	
}
