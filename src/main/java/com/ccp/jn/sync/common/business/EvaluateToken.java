package com.ccp.jn.sync.common.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class EvaluateToken extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		CcpMapDecorator loginRequest = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.login_token.name());
		
		String tokenDb = loginRequest.getAsString("token");
		String token = values.getAsString("token");
		
		boolean thisTokenIsCorrect = token.equals(tokenDb);
	
		if(thisTokenIsCorrect) {
			return new CcpStepResult(values, 200, this);
		}
		
		return new CcpStepResult(values, 401, this);
	}

}
