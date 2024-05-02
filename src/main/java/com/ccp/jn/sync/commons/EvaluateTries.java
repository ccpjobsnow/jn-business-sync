package com.ccp.jn.sync.commons;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.exceptions.process.CcpFlow;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.commons.utils.JnAsyncBusiness;

public class EvaluateTries implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private final JnBaseEntity entity;
	
	private final String userFieldName;
	
	private final JnAsyncBusiness topic;

	private final String databaseFieldName;
	
	private final JnBaseEntity entityAttempts;
	
	private final CcpProcessStatus whenWrongType;

	private final CcpProcessStatus whenExceedAttempts;

	public EvaluateTries(JnBaseEntity entity, String userFieldName, JnAsyncBusiness topic, String databaseFieldName,
			JnBaseEntity entityAttempts, CcpProcessStatus whenWrongType, CcpProcessStatus whenExceedAttempts) {
		this.entity = entity;
		this.userFieldName = userFieldName;
		this.topic = topic;
		this.databaseFieldName = databaseFieldName;
		this.entityAttempts = entityAttempts;
		this.whenWrongType = whenWrongType;
		this.whenExceedAttempts = whenExceedAttempts;
	}

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation values) {
		
		String entityName = this.entity.getEntityName();
		
		String tokenFromDatabase = values.getValueFromPath("_entities", entityName, this.databaseFieldName);
		
		String tokenFomUser = values.getAsString(this.userFieldName);
		
		CcpPasswordHandler dependency = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
		
		dependency.matches(tokenFomUser, tokenFromDatabase);
		
		boolean correctToken = tokenFomUser.equals(tokenFromDatabase);

		CcpJsonRepresentation toReturn = values.removeKey("_entities");
		
		if(correctToken) {
			CcpTextDecorator sessionToken = new CcpStringDecorator(CcpConstants.CHARACTERS_TO_GENERATE_TOKEN).text().generateToken(30);
			toReturn.put("sessionToken", sessionToken);
			CcpJsonRepresentation send = JnSyncMensageriaSender.INSTANCE.send(toReturn, this.topic);
			return send;
		}

		String attemptsEntityName = this.entityAttempts.getEntityName();
		Integer attemptsFromDatabase = values.getValueFromPath("_entities", attemptsEntityName, "attempts");
		
		boolean exceededAttempts = attemptsFromDatabase >= 3;
		if(exceededAttempts) {//TODO PARAMETRIZAR O 3
			int status = this.whenExceedAttempts.status();
			throw new CcpFlow(toReturn, status);
		}
		String email = values.getAsString("email");
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("attempts", attemptsFromDatabase + 1)
				.put("email", email)
				;
		this.entityAttempts.createOrUpdate(put);
		
		int status = this.whenWrongType.status();
		throw new CcpFlow(toReturn, status);
	}
	
	
	
}
