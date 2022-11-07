package com.ccp.jn.sync.business.login;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.exceptions.db.CcpRecordNotFound;
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
		try {
			CcpMapDecorator userStats = JnBusinessEntity.job_user_stats.get(values, CcpConstants.returnEmpty).getSubMap("loginCount", "lastLogin");
			CcpMapDecorator putAll = values.putAll(userStats);
			return new CcpStepResult(putAll, 200, this);
			
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 200, this);
		}
	}

}
