package com.ccp.jn.sync.business.password;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class ResetTokenTries extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		JnBusinessEntity.token_tries.remove(values);
		return new CcpStepResult(values, 200, this);
	}

}
