package com.ccp.jn.sync.common.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnEntity;

public class SaveLogin extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();
		String token = textDecorator.generateToken(8);
		values = values.put("token", token);
		JnEntity.login.createOrUpdate(values);


		CcpMapDecorator userStats = values.getInternalMap("_entities").getInternalMap(JnEntity.user_stats.name()).getSubMap("loginCount", "lastLogin");
		CcpMapDecorator putAll = values.putAll(userStats);
		return new CcpStepResult(putAll, 200, this);
	}

}
