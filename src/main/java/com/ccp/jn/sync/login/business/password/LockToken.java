package com.ccp.jn.sync.login.business.password;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class LockToken extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		JnBusinessEntity.locked_token.save(values);
		
		return new CcpStepResult(values, 403, this);
	}

}
