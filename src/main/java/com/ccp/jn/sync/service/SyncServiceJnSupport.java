package com.ccp.jn.sync.service;

import static com.ccp.constantes.CcpConstants.DO_BY_PASS;
import static com.ccp.jn.sync.business.JnProcessStatus.loginTokenIsMissing;
import static com.ccp.jn.sync.business.JnProcessStatus.requestAlreadyAnswered;
import static com.ccp.jn.sync.business.JnProcessStatus.requestToUnlockDoesNotExist;
import static com.ccp.jn.sync.business.JnProcessStatus.thisUserIsNotAllowedToDoSupport;
import static com.jn.commons.utils.JnConstants.PUT_PASSWORD;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.dao.CcpDaoProcedure;
import com.ccp.especifications.db.dao.CcpGetEntityId;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.sync.business.JnProcessStatus;
import com.jn.commons.business.utils.JnCommonsBusinessUtilsGetMessage;
import com.jn.commons.entities.JnEntityEmailParametersToSend;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.entities.JnEntityFailedUnlockToken;
import com.jn.commons.entities.JnEntityLockedToken;
import com.jn.commons.entities.JnEntityLoginToken;
import com.jn.commons.entities.JnEntityRequestTokenAgain;
import com.jn.commons.entities.JnEntityRequestTokenAgainAnswered;
import com.jn.commons.entities.JnEntityRequestTokenAgainResponsible;
import com.jn.commons.entities.JnEntityRequestUnlockToken;
import com.jn.commons.entities.JnEntityRequestUnlockTokenAnswered;
import com.jn.commons.entities.JnEntityRequestUnlockTokenResponsible;
import com.jn.commons.utils.JnTopics;

public enum SyncServiceJnSupport {
	unlockToken {
		@Override
		public CcpJsonRepresentation execute(Long chatId, String email) {
			
			
			CcpJsonRepresentation result = this.answerSupport(chatId, email, PUT_PASSWORD, JnEntityRequestUnlockTokenResponsible.INSTANCE, JnEntityRequestUnlockTokenAnswered.INSTANCE, JnEntityRequestUnlockToken.INSTANCE, JnTopics.requestUnlockToken.name());
			
			return result;
		}

		@Override
		public CcpDaoProcedure getValidations(CcpJsonRepresentation valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity) {
			CcpDaoProcedure rules = new CcpGetEntityId(valores)
					.toBeginProcedureAnd()
						.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
						.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(thisUserIsNotAllowedToDoSupport).and()
						.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(loginTokenIsMissing).and()
						.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
						.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestToUnlockDoesNotExist).and();
			return rules;
		}
	},
	resendToken {
		@Override
		public CcpJsonRepresentation execute(Long chatId, String email) {
			
			
			CcpJsonRepresentation result = this.answerSupport(chatId, email, DO_BY_PASS, JnEntityRequestTokenAgainResponsible.INSTANCE, JnEntityRequestTokenAgainAnswered.INSTANCE, JnEntityRequestTokenAgain.INSTANCE, JnTopics.requestTokenAgain.name());
			
			return result;
		}

		@Override
		public CcpDaoProcedure getValidations(CcpJsonRepresentation valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity) {
			CcpDaoProcedure validations = new CcpGetEntityId(valores)
					.toBeginProcedureAnd()
						.ifThisIdIsPresentInEntity(JnEntityFailedUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenHasFailed).and()
						.ifThisIdIsPresentInEntity(JnEntityRequestUnlockToken.INSTANCE).returnStatus(JnProcessStatus.unlockTokenAlreadyRequested).and()
						.ifThisIdIsPresentInEntity(JnEntityLockedToken.INSTANCE).returnStatus(JnProcessStatus.loginTokenIsLocked).and()
						.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(thisUserIsNotAllowedToDoSupport).and()
						.ifThisIdIsNotPresentInEntity(JnEntityLoginToken.INSTANCE).returnStatus(loginTokenIsMissing).and()
						.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
						.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestToUnlockDoesNotExist).and();
			return validations;
		}
	}
	;
	
	public abstract CcpJsonRepresentation execute(Long chatId, String email);
	
	public abstract CcpDaoProcedure getValidations(CcpJsonRepresentation valores, CcpEntity responsibleEntity, CcpEntity answerEntity, CcpEntity requestEntity);
	
	protected CcpJsonRepresentation answerSupport(Long chatId, String email, Function<CcpJsonRepresentation, CcpJsonRepresentation> transform, CcpEntity responsibleEntity,
			CcpEntity answerEntity, CcpEntity requestEntity, String topic) {
		
		CcpJsonRepresentation valores = CcpConstants.EMPTY_JSON.put("email", email).put("chatId", chatId);
		CcpJsonRepresentation transformed = valores.getTransformed(transform);
		
		Function<CcpJsonRepresentation, CcpJsonRepresentation> action = values -> {
			CcpJsonRepresentation entities = values.getInnerJson("_entities");
			String language = entities.getInnerJson(requestEntity.getEntityName()).getAsString("language");
			JnCommonsBusinessUtilsGetMessage gm = new JnCommonsBusinessUtilsGetMessage();
			gm
			.addOneStep(DO_BY_PASS, JnEntityEmailParametersToSend.INSTANCE, JnEntityEmailTemplateMessage.INSTANCE)
			.executeAllSteps(topic, answerEntity, transformed, language);
			requestEntity.delete(valores);
			return values;
			
		};
		
		CcpDaoProcedure validations = this.getValidations(valores, responsibleEntity, answerEntity, requestEntity);
		
		CcpJsonRepresentation result = validations
				.executeAction(action).andFinallyReturningThisFields()
			.endThisProcedureRetrievingTheResultingData();
		return result;
	}
	
}
