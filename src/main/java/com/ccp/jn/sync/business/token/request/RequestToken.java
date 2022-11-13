package com.ccp.jn.sync.business.token.request;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessTopic;

public class RequestToken extends CcpNextStep{

	private final CcpMensageriaSender mensageriaSender;
	
	public RequestToken(CcpMensageriaSender mensageriaSender) {
		this.mensageriaSender = mensageriaSender;
	}

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		 this.mensageriaSender.send(values, JnBusinessTopic.SendUserToken);
		
		return new CcpStepResult(values, 200, this);
	}

}
