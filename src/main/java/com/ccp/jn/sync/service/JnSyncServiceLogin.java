package com.ccp.jn.sync.service;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;
import com.ccp.jn.sync.commons.EvaluateAttempts;
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
import com.jn.commons.entities.JnEntityLoginSessionConflict;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.entities.JnEntityLoginStats;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityLoginTokenAttempts;
import com.jn.commons.json.transformers.JnJsonTransformerPutRandomTokenHash;
import com.jn.commons.status.StatusExecuteLogin;
import com.jn.commons.utils.JnAsyncBusiness;
import com.jn.commons.utils.JnDeleteKeysFromCache;
import com.jn.sync.mensageria.JnSyncMensageriaSender;

public class JnSyncServiceLogin{
	
	private JnSyncServiceLogin() {}
	   
	public static final JnSyncServiceLogin INSTANCE = new JnSyncServiceLogin();
	
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

		CcpJsonRepresentation transformedJson = json
				.getTransformedJson(JnJsonTransformerPutRandomTokenHash.INSTANCE)
				.duplicateValueFromField("originalToken", "sessionToken")
				;
		CcpJsonRepresentation findById =  new CcpGetEntityId(transformedJson)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginPassword.ENTITY).and()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginPasswordAttempts.ENTITY).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExecuteLogin.missingSavingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionConflict.ENTITY).returnStatus(StatusExecuteLogin.loginConflict).and()
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
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = JnEntityLoginEmail.ENTITY.getOperationCallback(CcpEntityCrudOperationType.save);

		CcpJsonRepresentation result = new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionConflict.ENTITY).returnStatus(StatusCreateLoginEmail.loginConflict).and()
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
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionConflict.ENTITY).returnStatus(StatusExistsLoginEmail.loginConflict).and()
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
			.ifThisIdIsNotPresentInEntity(JnEntityLoginSessionConflict.ENTITY).returnStatus(StatusExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionConflict.ENTITY).executeAction(action).andFinallyReturningTheseFields("x")
		.endThisProcedure(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE)
		;
		 
		return json;
	}
	
	public CcpJsonRepresentation saveAnswers (CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = JnEntityLoginAnswers.ENTITY.getOperationCallback(CcpEntityCrudOperationType.save);
		 new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusSaveAnswers.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionConflict.ENTITY).returnStatus(StatusSaveAnswers.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).executeAction(action)
 			.and().ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusSaveAnswers.missingPassword)
			
			.andFinallyReturningTheseFields("x")
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
		CcpJsonRepresentation renameField = CcpOtherConstants.EMPTY_JSON.getTransformedJson(JnJsonTransformerPutRandomTokenHash.INSTANCE).renameField("originalToken", "sessionToken").removeField(JnEntityLoginSessionValidation.Fields.token.name());
		CcpJsonRepresentation putAll = json.putAll(renameField);
		CcpJsonRepresentation result =  new CcpGetEntityId(putAll)
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

