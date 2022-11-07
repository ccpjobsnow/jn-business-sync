package com.ccp.jn.sync.business.login;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class EvaluateLoginTries extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator tries = JnBusinessEntity.password_tries.get(values, CcpConstants.getFirstTry);
		Integer tentativas = tries.getAsIntegerNumber("tries");
		if(tentativas >= 3) {
			return new CcpStepResult(values.put("tries", tentativas), 429, this);
		}
		tries = tries.put("tries", tentativas + 1);
		JnBusinessEntity.password_tries.save(tries);
		
		return new CcpStepResult(values.put("tries", tentativas + 1), 401, this);
	}

}
