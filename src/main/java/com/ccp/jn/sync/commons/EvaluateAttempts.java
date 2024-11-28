package com.ccp.jn.sync.commons;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.password.CcpPasswordHandler;
import com.ccp.exceptions.process.CcpFlow;
import com.ccp.jn.sync.mensageria.JnSyncMensageriaSender;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.base.JnBaseEntity;
import com.jn.commons.utils.JnAsyncBusiness;

public class EvaluateAttempts implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	
	private final JnBaseEntity entityToGetTheSecret;
	
	private final JnBaseEntity entityToGetTheAttempts;

	private final String userFieldName;
	
	private final String databaseFieldName;

	private final CcpProcessStatus statusToReturnWhenWrongType;
	
	private final CcpProcessStatus statusToReturnWhenExceedAttempts;
	
	private final JnAsyncBusiness topicToRegisterSuccess;

	private final JnAsyncBusiness topicToCreateTheLockWhenExceedTries;

	public EvaluateAttempts(
			JnBaseEntity entityToGetTheAttempts, 
			JnBaseEntity entityToGetTheSecret, 
			String databaseFieldName, 
			String userFieldName, 
			CcpProcessStatus statusToReturnWhenExceedAttempts, 
			CcpProcessStatus statusToReturnWhenWrongType,
			JnAsyncBusiness topicToCreateTheLockWhenExceedTries,
			JnAsyncBusiness topicToRegisterSuccess
			) {

		this.statusToReturnWhenExceedAttempts = statusToReturnWhenExceedAttempts;
		this.statusToReturnWhenWrongType = statusToReturnWhenWrongType;
		this.topicToRegisterSuccess = topicToRegisterSuccess;
		this.entityToGetTheAttempts = entityToGetTheAttempts;
		this.entityToGetTheSecret = entityToGetTheSecret;
		this.databaseFieldName = databaseFieldName;
		this.userFieldName = userFieldName;
		this.topicToCreateTheLockWhenExceedTries = topicToCreateTheLockWhenExceedTries;
	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		String secretFromDatabase = json.getValueFromPath("","_entities", this.entityToGetTheSecret.getEntityName(), this.databaseFieldName);
		
		String secretFomUser = json.getAsString(this.userFieldName);
		
		CcpPasswordHandler dependency = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
		
		boolean correctSecret = dependency.matches(secretFomUser, secretFromDatabase);
		
		CcpJsonRepresentation toReturn = json.removeField("_entities");
		
		if(correctSecret) {

			CcpJsonRepresentation transformed = toReturn.putRandomToken(30, "sessionToken");
			new JnSyncMensageriaSender(this.topicToRegisterSuccess).apply(transformed);
			return transformed;
		}

		String attemptsEntityName = this.entityToGetTheAttempts.getEntityName();
		Double attemptsFromDatabase = json.getValueFromPath(0d,"_entities", attemptsEntityName, "attempts");
		//TODO PARAMETRIZAR O 3
		boolean exceededAttempts = attemptsFromDatabase >= 3;
		if(exceededAttempts) {
			new JnSyncMensageriaSender(this.topicToCreateTheLockWhenExceedTries).apply(toReturn);
			throw new CcpFlow(toReturn, this.statusToReturnWhenExceedAttempts);
		}
		
		String email = json.getAsString("email");
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("attempts", attemptsFromDatabase + 1)
				.put("email", email)
				;
		this.entityToGetTheAttempts.createOrUpdate(put);
		throw new CcpFlow(toReturn, this.statusToReturnWhenWrongType);
	}
	
	
	
}
