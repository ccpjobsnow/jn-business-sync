package com.ccp.jn.sync.business.login;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class SaveLogin extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		JnBusinessEntity.login.save(values.getSubMap("email", "userAgent", "macAddress", "coordinates", "ip"));

		CcpTextDecorator textDecorator = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%*&_-+=()").text();
		String token = textDecorator.generateToken(8);
		values = values.put("token", token);
		CcpMapDecorator userStats = JnBusinessEntity.job_user_stats.get(values).getSubMap("loginCount", "lastLogin");
		values = values.putAll(userStats);
		return new CcpStepResult(values, 200, this);
	}

}
