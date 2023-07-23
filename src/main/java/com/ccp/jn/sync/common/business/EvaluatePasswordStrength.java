package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

public class EvaluatePasswordStrength extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		String password = values.getAsString("password");
		
		boolean strongPassword = new CcpStringDecorator(password).password().isStrong();
		
		if(strongPassword) {
			return new CcpStepResult(values, 200, this);
		}
		
		return new CcpStepResult(values, 422, this);
	}

}
