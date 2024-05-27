package com.ccp.jn.sync.service;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpGetEntityId;
import com.ccp.jn.sync.commons.EvaluateAttempts;
import com.ccp.jn.sync.commons.JnSyncMensageriaSender;
import com.ccp.jn.sync.status.login.StatusCreateLoginEmail;
import com.ccp.jn.sync.status.login.StatusCreateLoginToken;
import com.ccp.jn.sync.status.login.StatusExecuteLogout;
import com.ccp.jn.sync.status.login.StatusExistsLoginEmail;
import com.ccp.jn.sync.status.login.StatusSaveAnswers;
import com.ccp.jn.sync.status.login.StatusUpdatePassword;
import com.jn.commons.entities.JnEntityLoginAnswers;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginPasswordAttempts;
import com.jn.commons.entities.JnEntityLoginSessionCurrent;
import com.jn.commons.entities.JnEntityLoginStats;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;
import com.jn.commons.status.StatusExecuteLogin;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncServiceJnLogin{
	public CcpJsonRepresentation executeLogin(CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateTries =
				new EvaluateAttempts(
						JnEntityLoginPasswordAttempts.INSTANCE, 
						JnEntityLoginPassword.INSTANCE, 
						"password", 
						"password", 
						StatusExecuteLogin.passwordLockedRecently,
						StatusExecuteLogin.wrongPassword, 
						JnAsyncBusiness.lockPassword, 
						JnAsyncBusiness.executeLogin
						);


		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginPassword.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginPasswordAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusExecuteLogin.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE.getMirrorEntity()).returnStatus(StatusExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).returnStatus(StatusExecuteLogin.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusExecuteLogin.missingPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE).executeAction(evaluateTries).andFinallyReturningThisFields("sessionToken")
		.endThisProcedureRetrievingTheResultingData()
		;
		return findById; 
	}
	
	public CcpJsonRepresentation createLoginEmail (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginEmail.INSTANCE.createOrUpdate(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusCreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE.getMirrorEntity()).returnStatus(StatusCreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).returnStatus(StatusCreateLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusCreateLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusCreateLoginEmail.missingAnswers).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginEmail (CcpJsonRepresentation values){
		
		 new CcpGetEntityId(values) 
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusExistsLoginEmail.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusExistsLoginEmail.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE.getMirrorEntity()).returnStatus(StatusExistsLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).returnStatus(StatusExistsLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusExistsLoginEmail.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusExistsLoginEmail.missingPassword).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	
	public void executeLogout (CcpJsonRepresentation sessionValues){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = values -> JnSyncMensageriaSender.INSTANCE.send(values.removeKey(CcpConstants.ENTITIES_LABEL), JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(sessionValues)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).returnStatus(StatusExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void saveAnswers (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginAnswers.INSTANCE.createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusSaveAnswers.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusSaveAnswers.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.INSTANCE).returnStatus(StatusSaveAnswers.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.INSTANCE.getMirrorEntity()).returnStatus(StatusSaveAnswers.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.INSTANCE).returnStatus(StatusSaveAnswers.missingPassword).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation createLoginToken (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores.removeKey(CcpConstants.ENTITIES_LABEL), JnAsyncBusiness.sendUserToken);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusCreateLoginToken.statusLockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(StatusCreateLoginToken.statusAlreadySentToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusUpdatePassword.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.INSTANCE).returnStatus(StatusCreateLoginToken.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}

	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation values){

		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateAttempts =
				new EvaluateAttempts(
						JnEntityLoginTokenAttempts.INSTANCE, 
						JnEntityLoginToken.INSTANCE, 
						"tokenHash", 
						"token", 
						StatusUpdatePassword.tokenLockedRecently,
						StatusUpdatePassword.wrongToken, 
						JnAsyncBusiness.lockToken,
						JnAsyncBusiness.updatePassword 
						);
		
		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginToken.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginTokenAttempts.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.INSTANCE.getMirrorEntity()).returnStatus(StatusUpdatePassword.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(StatusUpdatePassword.missingEmail).and()
			.executeAction(evaluateAttempts).andFinallyReturningThisFields("sessionToken")	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}
}

