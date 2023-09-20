package com.ccp.jn.sync.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityUserStatis;

public class JnSyncBusinessCreateLogin extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();
		String token = textDecorator.generateToken(8);
		CcpMapDecorator tokenChanged = values.put("token", token);
		new JnEntityLogin().createOrUpdate(tokenChanged);


		CcpMapDecorator userStats = tokenChanged.getInternalMap("_entities").getInternalMap(new JnEntityUserStatis().name()).getSubMap("loginCount", "lastLogin");
		CcpMapDecorator putAll = tokenChanged.putAll(userStats);
		return new CcpStepResult(putAll, 200, this);
	}

}
