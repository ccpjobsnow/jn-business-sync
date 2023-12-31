package com.ccp.jn.sync.business;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpPasswordDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;

public class JnSyncBusinessEvaluatePasswordStrength extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {
		
		String password = values.getAsString("password");
		
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(password);
		CcpPasswordDecorator pwd = ccpStringDecorator.password();
		
		boolean strongPassword = pwd.isStrong();
		
		if(strongPassword) {         
			return new CcpStepResult(values, 200, this);
		}
		
		return new CcpStepResult(values, 422, this);
	}

}
