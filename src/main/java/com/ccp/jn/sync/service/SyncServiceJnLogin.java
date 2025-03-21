package com.ccp.jn.sync.service;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpGetEntityId;
import com.ccp.jn.sync.commons.EvaluateAttempts;
import com.ccp.jn.sync.mensageria.JnSyncMensageriaSender;
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
import com.jn.commons.utils.JnDeleteKeysFromCache;

public class SyncServiceJnLogin{
	
	private SyncServiceJnLogin() {}
	  
	public static final SyncServiceJnLogin INSTANCE = new SyncServiceJnLogin();
	
	public CcpJsonRepresentation executeLogin(CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateTries =
				new EvaluateAttempts(
						JnEntityLoginPasswordAttempts.ENTITY, 
						JnEntityLoginPassword.ENTITY, 
						JnEntityLoginPassword.Fields.password.name(), 
						JnEntityLoginPassword.Fields.password.name(), 
						StatusExecuteLogin.passwordLockedRecently,
						StatusExecuteLogin.wrongPassword, 
						JnAsyncBusiness.lockPassword, 
						JnAsyncBusiness.executeLogin, 
						
						JnEntityLoginPasswordAttempts.Fields.attempts.name(),
						JnEntityLoginPassword.Fields.email.name()
						);


		CcpJsonRepresentation findById =  new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginPassword.ENTITY).and()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginPasswordAttempts.ENTITY).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExecuteLogin.missingSavingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExecuteLogin.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusExecuteLogin.missingSavePassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY).executeAction(evaluateTries).andFinallyReturningTheseFields(
					JnEntityLoginToken.Fields.userAgent.name(),
					JnEntityLoginToken.Fields.email.name(),
					JnEntityLoginToken.Fields.ip.name(),
					"sessionToken" 
					)
		.endThisProcedureRetrievingTheResultingData(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE)
		;
		return findById; 
	}
	
	public CcpJsonRepresentation createLoginEmail(CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginEmail.ENTITY.createOrUpdate(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusCreateLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusCreateLoginEmail.missingSavePassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusCreateLoginEmail.missingSaveAnswers).andFinallyReturningTheseFields("x")
		.endThisProcedureRetrievingTheResultingData(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE);

		return result;
	}
	
	public CcpJsonRepresentation existsLoginEmail(CcpJsonRepresentation json){
		
		 new CcpGetEntityId(json) 
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExistsLoginEmail.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExistsLoginEmail.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExistsLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExistsLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusExistsLoginEmail.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusExistsLoginEmail.missingPassword).andFinallyReturningTheseFields("x")
		.endThisProcedure(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE)
		;
	 return json;
	}
	
	public CcpJsonRepresentation executeLogout(CcpJsonRepresentation json){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = new JnSyncMensageriaSender(JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).executeAction(action).andFinallyReturningTheseFields("x")
		.endThisProcedure(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE)
		;
		 
		return json;
	}
	
	public CcpJsonRepresentation saveAnswers (CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> valores.put("result_save_" + JnEntityLoginAnswers.ENTITY, JnEntityLoginAnswers.ENTITY.createOrUpdate(valores));
		 new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusSaveAnswers.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusSaveAnswers.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusSaveAnswers.missingPassword).andFinallyReturningTheseFields("x")
		.endThisProcedure(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE)
		;
		return json;
	}

	public CcpJsonRepresentation createLoginToken(CcpJsonRepresentation json){
		

		CcpJsonRepresentation result = new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginToken.statusLockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusUpdatePassword.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusCreateLoginToken.missingSaveAnswers).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY).returnStatus(StatusCreateLoginToken.statusAlreadySentToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.ENTITY).executeAction(new JnSyncMensageriaSender(JnAsyncBusiness.sendUserToken))
			.andFinallyReturningTheseFields(json.fieldSet())
		.endThisProcedureRetrievingTheResultingData(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE);

		return result;
	}

	public CcpJsonRepresentation savePassword(CcpJsonRepresentation json){

		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateAttempts =
				new EvaluateAttempts(
						JnEntityLoginTokenAttempts.ENTITY, 
						JnEntityLoginToken.ENTITY, 
						JnEntityLoginToken.Fields.token.name(),
						JnEntityLoginToken.Fields.token.name(),
						StatusUpdatePassword.tokenLockedRecently,
						StatusUpdatePassword.wrongToken, 
						JnAsyncBusiness.lockToken,
						JnAsyncBusiness.updatePassword, 
						JnEntityLoginTokenAttempts.Fields.attempts.name(),
						JnEntityLoginToken.Fields.email.name()
						);
		
		CcpJsonRepresentation result =  new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginTokenAttempts.ENTITY).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusUpdatePassword.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusUpdatePassword.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.ENTITY).returnStatus(StatusUpdatePassword.missingToken).and()
			.executeAction(evaluateAttempts).andFinallyReturningTheseFields(
					JnEntityLoginToken.Fields.userAgent.name(),
					JnEntityLoginToken.Fields.email.name(),
					JnEntityLoginToken.Fields.ip.name(),
					"sessionToken" 
					)	
		.endThisProcedureRetrievingTheResultingData(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE);
		
		return result;
	}
}

