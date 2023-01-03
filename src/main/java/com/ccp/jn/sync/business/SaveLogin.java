package com.ccp.jn.sync.business;

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
		
		JnBusinessEntity.login.save(values);

		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();
		String token = textDecorator.generateToken(8);
		values = values.put("token", token);
		try {
			CcpMapDecorator userStats = values.getInternalMap("_tables").getInternalMap(JnBusinessEntity.user_stats.name()).getSubMap("loginCount", "lastLogin");
			CcpMapDecorator putAll = values.putAll(userStats);
			return new CcpStepResult(putAll, 200, this);
			
		} catch (CcpRecordNotFound e) {
			return new CcpStepResult(values, 200, this);
		}
	}

}
