package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.entities.JnEntityLoginToken;

public class JnSyncBusinessEvaluateToken extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {

		CcpJsonRepresentation loginRequest = values.getInnerJson("_entities").getInnerJson(new JnEntityLoginToken().name());
		
		String tokenDb = loginRequest.getAsString("token");
		String token = values.getAsString("token");
		
		boolean thisTokenIsCorrect = token.equals(tokenDb);
	
		if(thisTokenIsCorrect) {
			return new CcpStepResult(values, 200, this);
		}
		
		return new CcpStepResult(values, 401, this);
	}

}
