package com.ccp.jn.sync.business.login;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class ResetLoginTries extends CcpNextStep{
	
	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		JnBusinessEntity.password_tries.remove(values);
		return new CcpStepResult(values, 200, this);
	}

}
