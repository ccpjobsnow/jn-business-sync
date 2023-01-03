package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class LockLogin extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		JnBusinessEntity.locked_password.save(values);
		
		return new CcpStepResult(values, 429, this);
	}

}
