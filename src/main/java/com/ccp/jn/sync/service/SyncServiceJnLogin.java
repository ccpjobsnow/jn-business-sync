package com.ccp.jn.sync.service;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
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

public class SyncServiceJnLogin{
	
	private SyncServiceJnLogin() {}
	
	public static final SyncServiceJnLogin INSTANCE = new SyncServiceJnLogin();
	
	public CcpJsonRepresentation executeLogin(CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateTries =
				new EvaluateAttempts(
						JnEntityLoginPasswordAttempts.ENTITY, 
						JnEntityLoginPassword.ENTITY, 
						"password", 
						"password", 
						StatusExecuteLogin.passwordLockedRecently,
						StatusExecuteLogin.wrongPassword, 
						JnAsyncBusiness.lockPassword, 
						JnAsyncBusiness.executeLogin
						);


		CcpJsonRepresentation findById =  new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginPassword.ENTITY).and()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginPasswordAttempts.ENTITY).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExecuteLogin.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExecuteLogin.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusExecuteLogin.missingPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY).executeAction(evaluateTries).andFinallyReturningThisFields("sessionToken")
		.endThisProcedureRetrievingTheResultingData(CcpConstants.DO_NOTHING)
		;
		return findById; 
	}
	
	public CcpJsonRepresentation createLoginEmail (CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginEmail.ENTITY.createOrUpdate(valores);

		CcpJsonRepresentation result = new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusCreateLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusCreateLoginEmail.missingPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusCreateLoginEmail.missingAnswers).andFinallyReturningThisFields("x")
		.endThisProcedureRetrievingTheResultingData(CcpConstants.DO_NOTHING);

		return result;
	}
	
	public void existsLoginEmail (CcpJsonRepresentation json){
		
		 new CcpGetEntityId(json) 
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExistsLoginEmail.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExistsLoginEmail.missingEmail).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExistsLoginEmail.lockedPassword).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExistsLoginEmail.loginConflict).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusExistsLoginEmail.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusExistsLoginEmail.missingPassword).andFinallyReturningThisFields("x")
		.endThisProcedure(CcpConstants.DO_NOTHING)
		;
	}
	
	
	public void executeLogout (CcpJsonRepresentation sessionValues){
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = new JnSyncMensageriaSender(JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(sessionValues)
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusExecuteLogout.missingLogin).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).executeAction(action).andFinallyReturningThisFields("x")
		.endThisProcedure(CcpConstants.DO_NOTHING)
		;
	}
	
	public void saveAnswers (CcpJsonRepresentation json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityLoginAnswers.ENTITY.createOrUpdate(valores);
		 new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusSaveAnswers.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginSessionCurrent.ENTITY).returnStatus(StatusSaveAnswers.loginConflict).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusSaveAnswers.lockedPassword).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).executeAction(action).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusSaveAnswers.missingPassword).andFinallyReturningThisFields("x")
		.endThisProcedure(CcpConstants.DO_NOTHING)
		;
	}

	public CcpJsonRepresentation createLoginToken(CcpJsonRepresentation json){
		

		CcpJsonRepresentation result = new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusCreateLoginToken.statusLockedToken).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY).returnStatus(StatusCreateLoginToken.statusAlreadySentToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusUpdatePassword.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginAnswers.ENTITY).returnStatus(StatusCreateLoginToken.missingAnswers).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.ENTITY).executeAction(new JnSyncMensageriaSender(JnAsyncBusiness.sendUserToken)).andFinallyReturningThisFields("x")
		.endThisProcedureRetrievingTheResultingData(CcpConstants.DO_NOTHING);

		return result;
	}

	public CcpJsonRepresentation updatePassword (CcpJsonRepresentation json){

		Function<CcpJsonRepresentation, CcpJsonRepresentation> evaluateAttempts =
				new EvaluateAttempts(
						JnEntityLoginTokenAttempts.ENTITY, 
						JnEntityLoginToken.ENTITY, 
						"tokenHash", 
						"token", 
						StatusUpdatePassword.tokenLockedRecently,
						StatusUpdatePassword.wrongToken, 
						JnAsyncBusiness.lockToken,
						JnAsyncBusiness.updatePassword 
						);
		
		CcpJsonRepresentation result =  new CcpGetEntityId(json)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityLoginStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityLoginTokenAttempts.ENTITY).and()
			.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusUpdatePassword.lockedToken).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusUpdatePassword.missingEmail).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.ENTITY).returnStatus(StatusUpdatePassword.missingToken).and()
			.executeAction(evaluateAttempts).andFinallyReturningThisFields("sessionToken")	
		.endThisProcedureRetrievingTheResultingData(CcpConstants.DO_NOTHING);
		
		return result;
	}
}

