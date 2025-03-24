package com.ccp.jn.sync.service;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpGetEntityId;
import com.ccp.exceptions.process.CcpFlowDisturb;
import com.jn.commons.entities.JnEntityLoginEmail;
import com.jn.commons.entities.JnEntityLoginPassword;
import com.jn.commons.entities.JnEntityLoginSessionValidation;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.status.StatusExecuteLogin;
import com.jn.commons.utils.JnDeleteKeysFromCache;

public class JnValidateSession implements Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private JnValidateSession() {}
	
	public static final JnValidateSession INSTANCE = new JnValidateSession();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) { 
		
		boolean isSessionLess = json.getAsString("sessionToken").trim().isEmpty(); 
		
		if(isSessionLess) {
			throw new CcpFlowDisturb(StatusExecuteLogin.missingSessionToken);
		}
		
		new CcpGetEntityId(json.duplicateValueFromField("sessionToken", JnEntityLoginSessionValidation.Fields.token.name())) 
		.toBeginProcedureAnd()
		.ifThisIdIsNotPresentInEntity(JnEntityLoginSessionValidation.ENTITY).returnStatus(StatusExecuteLogin.invalidSession).and()
		.ifThisIdIsPresentInEntity(JnEntityLoginToken.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedToken).and()
		.ifThisIdIsPresentInEntity(JnEntityLoginPassword.ENTITY.getTwinEntity()).returnStatus(StatusExecuteLogin.lockedPassword).and()
		.ifThisIdIsNotPresentInEntity(JnEntityLoginPassword.ENTITY).returnStatus(StatusExecuteLogin.missingSavePassword).and()
		.ifThisIdIsNotPresentInEntity(JnEntityLoginEmail.ENTITY).returnStatus(StatusExecuteLogin.missingSavingEmail)
		.andFinallyReturningTheseFields("sessionToken")
		.endThisProcedureRetrievingTheResultingData(CcpOtherConstants.DO_NOTHING, JnDeleteKeysFromCache.INSTANCE);
		//FIXME CATCH E PORQUE NAO ESTÁ VALIDANDO O TOKEN DE SESSÃO???
		return json; 
	}

}
