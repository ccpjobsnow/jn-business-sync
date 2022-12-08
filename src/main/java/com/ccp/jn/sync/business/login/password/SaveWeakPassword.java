package com.ccp.jn.sync.business.login.password;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class SaveWeakPassword extends CcpNextStep  {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		JnBusinessEntity.weak_password.save(values);
		return new CcpStepResult(values, 200, this);
	}

}
