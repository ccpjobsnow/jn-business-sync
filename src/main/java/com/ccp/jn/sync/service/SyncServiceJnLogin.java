package com.ccp.jn.sync.service;

import java.util.Map;
import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.jn.sync.business.utils.JnSyncMensageriaSender;
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
import com.jn.commons.entities.JnEntityPreRegistration;
import com.jn.commons.entities.JnEntityTokenTries;
import com.jn.commons.entities.JnEntityUserStats;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncServiceJnLogin{
	
	/*
	 * TODO 
	 * Fazer validação de token
	 * Preparar mensagem para enviar na fila
	 */
	public CcpJsonRepresentation executeLogin(Map<String, Object> json){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.executeLogin);

		CcpJsonRepresentation values = new CcpJsonRepresentation(json);

		CcpJsonRepresentation findById =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(ExecuteLogin.tokenBloqueado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(ExecuteLogin.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(ExecuteLogin.senhaBloqueada).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExecuteLogin.usuarioJaLogado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(ExecuteLogin.faltandoCadastrarSenha).and()
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
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(CreateLoginEmail.tokenBloqueado).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(CreateLoginEmail.senhaBloqueada).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(CreateLoginEmail.usuarioJaLogado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(CreateLoginEmail.faltandoCadastrarSenha).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(CreateLoginEmail.faltandoPreRegistration).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedureRetrievingTheResultingData();

		return result;
	}
	
	public void existsLoginEmail (String email){
		
		CcpJsonRepresentation values = new CcpJsonRepresentation(CcpConstants.EMPTY_JSON.put("email", email));

		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(ExistsLoginEmail.tokenBloqueado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(ExistsLoginEmail.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(ExistsLoginEmail.senhaBloqueada).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExistsLoginEmail.usuarioJaLogado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(ExistsLoginEmail.faltandoCadastrarSenha).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(ExistsLoginEmail.faltandoPreRegistration)
		.andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void executeLogout (String email){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.executeLogout);
		
		 new CcpGetEntityId(CcpConstants.EMPTY_JSON.put("email", email))
		.toBeginProcedureAnd()
			.ifThisIdIsNotPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(ExecuteLogout.usuarioNaoLogado).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}
	
	public void savePreRegistration (CcpJsonRepresentation values){
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnEntityPreRegistration.INSTANCE.createOrUpdate(valores);
		 new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(SavePreRegistration.tokenBloqueado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(SavePreRegistration.tokenFaltando).and()
			.ifThisIdIsPresentInEntity(JnEntityLogin.INSTANCE).returnStatus(SavePreRegistration.usuarioJaLogado).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedPassword.INSTANCE).returnStatus(SavePreRegistration.senhaBloqueada).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPassword.INSTANCE).returnStatus(SavePreRegistration.faltandoCadastrarSenha).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).executeAction(action).andFinallyReturningThisFields()
		.endThisProcedure()
		;
	}

	public CcpJsonRepresentation createLoginToken (String email, String language){
		
		CcpJsonRepresentation values = CcpConstants.EMPTY_JSON.put("email", email).put("language", language);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.sendUserToken);

		CcpJsonRepresentation result = new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(CreateLoginToken.tokenBloqueado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(CreateLoginToken.tokenFaltando).and()
			.ifThisIdIsNotPresentInEntity(JnEntityPreRegistration.INSTANCE).returnStatus(CreateLoginToken.faltandoPreRegistration).and()
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
		/*
		 * TODO 
		 * Fazer validação de token
		 * Preparar mensagem para enviar na fila
		 */
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = valores -> JnSyncMensageriaSender.INSTANCE.send(valores, JnAsyncBusiness.savePassword);

		CcpJsonRepresentation result =  new CcpGetEntityId(values)
		.toBeginProcedureAnd()
			.loadThisIdFromEntity(JnEntityUserStats.INSTANCE).and()
			.loadThisIdFromEntity(JnEntityTokenTries.INSTANCE).and()
			.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(UpdatePassword.tokenBloqueado).and()
			.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.INSTANCE).returnStatus(UpdatePassword.tokenFaltando).and()
			.executeAction(action).andFinallyReturningThisFields("sessionToken")	
		.endThisProcedureRetrievingTheResultingData();
		
		return result;
	}

}

