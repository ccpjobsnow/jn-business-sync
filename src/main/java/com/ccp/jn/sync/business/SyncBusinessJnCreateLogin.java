package com.ccp.jn.sync.business;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpStepResult;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityUserStats;

public class SyncBusinessJnCreateLogin extends CcpNextStep {

	@Override
	public CcpStepResult executeThisStep(CcpJsonRepresentation values) {
		
		CcpTextDecorator textDecorator = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text();
		String token = textDecorator.generateToken(8);
		CcpJsonRepresentation tokenChanged = values.put("token", token);
		new JnEntityLogin().createOrUpdate(tokenChanged);


		CcpJsonRepresentation userStats = tokenChanged.getInnerJson("_entities").getInnerJson(new JnEntityUserStats().name()).getJsonPiece("loginCount", "lastLogin");
		CcpJsonRepresentation putAll = tokenChanged.putAll(userStats);
		return new CcpStepResult(putAll, 200, this);
	}

}
