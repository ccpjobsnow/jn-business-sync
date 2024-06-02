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
import com.jn.commons.utils.JnGenerateRandomToken;

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
		
		String secretFromDatabase = this.entityToGetTheSecret.getInnerJsonFromMainAndMirrorEntities(json).getAsString(this.databaseFieldName);
		
		String secretFomUser = json.getAsString(this.userFieldName);
		
		CcpPasswordHandler dependency = CcpDependencyInjection.getDependency(CcpPasswordHandler.class);
		
		boolean correctSecret = dependency.matches(secretFomUser, secretFromDatabase);
		
		CcpJsonRepresentation toReturn = json.removeKey("_entities");
		
		if(correctSecret) {

			JnGenerateRandomToken transformer = new JnGenerateRandomToken(30, "sessionToken");
			CcpJsonRepresentation transformed = toReturn.getTransformed(transformer);
			CcpJsonRepresentation send = JnSyncMensageriaSender.INSTANCE.whenSendMessage(this.topicToRegisterSuccess).apply(transformed);
			return send;
		}

		String attemptsEntityName = this.entityToGetTheAttempts.getEntityName();
		Double attemptsFromDatabase = json.getValueFromPath(0d,"_entities", attemptsEntityName, "attempts");
		//TODO PARAMETRIZAR O 3
		boolean exceededAttempts = attemptsFromDatabase >= 3;
		if(exceededAttempts) {
			JnSyncMensageriaSender.INSTANCE.whenSendMessage(this.topicToCreateTheLockWhenExceedTries).apply(toReturn);
			int status = this.statusToReturnWhenExceedAttempts.status();
			throw new CcpFlow(toReturn, status);
		}
		
		String email = json.getAsString("email");
		CcpJsonRepresentation put = CcpConstants.EMPTY_JSON
				.put("attempts", attemptsFromDatabase + 1)
				.put("email", email)
				;
		this.entityToGetTheAttempts.createOrUpdate(put);
		int status = this.statusToReturnWhenWrongType.status();
		throw new CcpFlow(toReturn, status);
	}
	
	
	
}
