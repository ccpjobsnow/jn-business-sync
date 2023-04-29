package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessTopic;

public class RequestTokenAction extends CcpNextStep{

	private final CcpMensageriaSender mensageriaSender;
	
	public RequestTokenAction(CcpMensageriaSender mensageriaSender) {
		this.mensageriaSender = mensageriaSender;
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		 this.mensageriaSender.send(values, JnBusinessTopic.sendUserToken);
		
		return new CcpStepResult(values, 200, this);
	}

}
