package com.ccp.jn.sync.service;

import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpGetEntityId;
import com.ccp.jn.sync.commons.EvaluateTries;
import com.ccp.jn.sync.commons.JnSyncMensageriaSender;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusExecuteLogin;
import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.sync.status.login.StatusSavePreRegistration;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.jn.commons.entities.JnEntityLoginLockedPassword;
import com.jn.commons.entities.JnEntityLoginLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;
import com.jn.commons.entities.JnEntityLoginStats;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncServiceJnLogin{
	/*
	 */

	public CcpJsonRepresentation executeLogin(Map<String, Object> json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateTries =
				new EvaluateTries(
						JnEntityLoginLockedPassword.INSTANCE, 
						JnEntityLoginPasswordAttempts.INSTANCE, 
						JnEntityLoginPassword.INSTANCE, 
						"password", 
						"password", 
						StatusExecuteLogin.passwordLockedRecently,
						StatusExecuteLogin.wrongPassword, 
						JnAsyncBusiness.executeLogin 
						);

		CcpJsonRepresentation values = new CcpJsonRepresentation(json);

		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginPassword.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginPasswordAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusExecuteLogin.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedPassword.INSTANCE).returnStatus(StatusExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(StatusExecuteLogin.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusExecuteLogin.missingPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE).executeAction(evaluateTries).andFinallyReturningThisFields("sessionToken")
		.endThisProcedureRetrievingTheResultingData()
		;
		return findById;
	}
	
	public CcpJsonRepresentation createLoginEmail (String email){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginEmail.INSTANCE.createOrUpdate(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusCreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedPassword.INSTANCE).returnStatus(StatusCreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(StatusCreateLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusCreateLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusCreateLoginEmail.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginEmail (String email){
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(CcpConstants.EMPTY_JSON.put("email", email));

		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusExistsLoginEmail.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusExistsLoginEmail.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedPassword.INSTANCE).returnStatus(StatusExistsLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(StatusExistsLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusExistsLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusExistsLoginEmail.missingAnswers)
		.andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = values -> JnSyncMensageriaSender.INSTANCE.send(values, JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(CcpConstants.EMPTY_JSON.put("email", email))
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(StatusExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void saveAnswers (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginAnswers.INSTANCE.createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusSavePreRegistration.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusSavePreRegistration.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(StatusSavePreRegistration.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedPassword.INSTANCE).returnStatus(StatusSavePreRegistration.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusSavePreRegistration.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation createLoginToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.sendUserToken);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusCreateLoginToken.statusLockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusCreateLoginToken.statusMissingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusCreateLoginToken.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}

	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation values){

		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateTries =
				new EvaluateTries(
						JnEntityLoginLockedToken.INSTANCE, 
						JnEntityLoginTokenAttempts.INSTANCE, 
						JnEntityLoginToken.INSTANCE, 
						"tokenHash", 
						"token", 
						StatusUpdatePassword.tokenLockedRecently,
						StatusUpdatePassword.wrongToken, 
						JnAsyncBusiness.updatePassword 
						);
		
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginToken.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginTokenAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginLockedToken.INSTANCE).returnStatus(StatusUpdatePassword.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusUpdatePassword.missingEmail).and()
			.executeAction(evaluateTries).andFinallyReturningThisFields()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

