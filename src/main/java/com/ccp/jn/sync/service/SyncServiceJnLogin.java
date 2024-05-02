package com.ccp.jn.sync.service;

import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.jn.sync.commons.EvaluateTries;
import com.ccp.jn.sync.commons.JnSyncMensageriaSender;
import com.ccp.jn.sync.status.login.CreateLoginEmail;
import com.ccp.jn.sync.status.login.CreateLoginToken;
import com.ccp.jn.sync.status.login.ExecuteLogin;
import com.ccp.jn.sync.status.login.ExecuteLogout;
import com.ccp.jn.sync.status.login.ExistsLoginEmail;
import com.ccp.jn.sync.status.login.SavePreRegistration;
import com.ccp.jn.sync.status.login.UpdatePassword;
import com.jn.commons.entities.JnEntityLockedPassword;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLogin;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityPassword;
import com.jn.commons.entities.JnEntityPasswordAttempts;
import com.jn.commons.entities.JnEntityPreRegistration;
import com.jn.commons.entities.JnEntityTokenAttempts;
import com.jn.commons.entities.JnEntityUserStats;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncServiceJnLogin{
	

	public CcpJsonRepresentation executeLogin(Map<String, Object> json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action =
				new EvaluateTries(
						JnEntityPassword.INSTANCE, 
						"password", 
						JnAsyncBusiness.executeLogin, 
						"password", 
						JnEntityPasswordAttempts.INSTANCE, 
						ExecuteLogin.wrongPassword, 
						ExecuteLogin.passwordLockedRecently
						);

		CcpJsonRepresentation values = new CcpJsonRepresentation(json);

		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityPassword.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityPasswordAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(ExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(ExecuteLogin.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(ExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExecuteLogin.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(ExecuteLogin.missingPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityPassword.INSTANCE).executeAction(action).andFinallyReturningThisFields("sessionToken")
		.endThisProcedureRetrievingTheResultingData()
		;
		return findById;
	}
	
	public CcpJsonRepresentation createLoginEmail (String email){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginEmail.INSTANCE.createOrUpdate(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(CreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(CreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(CreateLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(CreateLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(CreateLoginEmail.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginEmail (String email){
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(CcpConstants.EMPTY_JSON.put("email", email));

		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(ExistsLoginEmail.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(ExistsLoginEmail.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(ExistsLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExistsLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(ExistsLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(ExistsLoginEmail.missingAnswers)
		.andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(CcpConstants.EMPTY_JSON.put("email", email))
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void savePreRegistration (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityPreRegistration.INSTANCE.createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(SavePreRegistration.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(SavePreRegistration.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(SavePreRegistration.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(SavePreRegistration.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(SavePreRegistration.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation createLoginToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.sendUserToken);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(CreateLoginToken.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(CreateLoginToken.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(CreateLoginToken.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}

	/*
	 * resolver conflito de login
	 * zerar tentativas de token
	 * desbloquear senha
	 * salvar senha
	 * registrar login
	 */
	
	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation values){

		Function<CcpJsonRepresentation, CcpJsonRepresentation> action =
			new EvaluateTries(
					JnEntityLoginToken.INSTANCE, 
					"token", 
					JnAsyncBusiness.savePassword, 
					"token", 
					JnEntityTokenAttempts.INSTANCE, 
					UpdatePassword.wrongToken, 
					UpdatePassword.tokenLockedRecently
					);
		
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginToken.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityTokenAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(UpdatePassword.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(UpdatePassword.missingToken).and()
			.executeAction(action).andFinallyReturningThisFields()	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

