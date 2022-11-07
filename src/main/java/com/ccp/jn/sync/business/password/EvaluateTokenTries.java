package com.ccp.jn.sync.business.password;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class EvaluateTokenTries extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
	
		CcpMapDecorator tokenTries = JnBusinessEntity.token_tries.get(values, CcpConstants.getFirstTry);
		
		Integer tries = tokenTries.getAsIntegerNumber("tries");
		
		if(tries >= 3) {
			return new CcpStepResult(values, 403, this);
		}
		
		tokenTries = tokenTries.put("tries", tries + 1);
		
		JnBusinessEntity.token_tries.save(tokenTries);
		
		return new CcpStepResult(values.put("tries", tries + 1), 401, this);
	}

}
