package com.ccp.jn.sync.service;

import static com.ccp.constantes.CcpConstants.DO_NOTHING;
import static com.ccp.jn.sync.common.business.JnProcessStatus.requestAlreadyAnswered;
import static com.ccp.jn.sync.common.business.JnProcessStatus.requestDoesNotExist;
import static com.ccp.jn.sync.common.business.JnProcessStatus.unauthorizedResponsible;
import static com.ccp.jn.sync.common.business.JnProcessStatus.loginTokenIsMissing;
import static com.jn.commons.JnConstants.PUT_EMAIL_TOKEN;
import static com.jn.commons.JnEntity.email_parameters_to_send;
import static com.jn.commons.JnEntity.email_template_message;
import static com.jn.commons.JnEntity.login_token;
import static com.jn.commons.JnEntity.request_token_again_answered;
import static com.jn.commons.JnEntity.request_unlock_token;
import static com.jn.commons.JnEntity.request_unlock_token_answered;
import static com.jn.commons.JnEntity.request_unlock_token_responsible;
import static com.jn.commons.JnTopic.requestTokenAgain;
import static com.jn.commons.JnTopic.requestUnlockToken;

import java.util.function.Function;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.dao.CalculateId;
import com.jn.commons.GetMessage;
import com.jn.commons.JnEntity;
import com.jn.commons.JnTopic;


public enum JnSupportService {
	unlockToken {
		@Override
		public CcpMapDecorator execute(Long chatId, String email) {
			CcpMapDecorator result = this.answerSupport(chatId, email, PUT_EMAIL_TOKEN, request_unlock_token_responsible, request_unlock_token_answered,
					request_unlock_token, requestUnlockToken);
			
			return result;
		}
	},
	resendToken {
		@Override
		public CcpMapDecorator execute(Long chatId, String email) {
			CcpMapDecorator result = this.answerSupport(chatId, email, DO_NOTHING, JnEntity.request_token_again_responsible, request_token_again_answered,
					JnEntity.request_token_again, requestTokenAgain);
			
			return result;
		}
	}
	;
	
	public abstract CcpMapDecorator execute(Long chatId, String email);
	
	public CcpMapDecorator getUnlockTokenMessage(Long chatId, String email) {
		
		
		CcpMapDecorator result = this.answerSupport(chatId, email, PUT_EMAIL_TOKEN, request_unlock_token_responsible, request_unlock_token_answered,
				request_unlock_token, requestUnlockToken);
		
		return result;
	}

	public CcpMapDecorator getRequestTokenAgainMessage(Long chatId, String email) {
		
		
		CcpMapDecorator result = this.answerSupport(chatId, email, DO_NOTHING, JnEntity.request_token_again_responsible, request_token_again_answered,
				JnEntity.request_token_again, requestTokenAgain);
		
		return result;
	}

	protected CcpMapDecorator answerSupport(
			Long chatId, String email, Function<CcpMapDecorator, CcpMapDecorator> transform, JnEntity responsibleEntity,
			JnEntity answerEntity, JnEntity requestEntity, JnTopic topic) {
		
		CcpMapDecorator valores = new CcpMapDecorator().put("email", email).put("chatId", chatId);
		CcpMapDecorator transformed = valores.getTransformed(transform);
		
		Function<CcpMapDecorator, CcpMapDecorator> action = values -> {
			CcpMapDecorator entities = values.getInternalMap("_entities");
			String language = entities.getInternalMap(requestEntity.name()).getAsString("language");
			GetMessage gm = new GetMessage();
			gm
			.addFlow(DO_NOTHING, email_parameters_to_send, email_template_message)
			.execute(topic, answerEntity, transformed, language);
			requestEntity.delete(valores);
			return values;
			
		};
		
		CcpMapDecorator result = new CalculateId(valores)
			.toBeginProcedureAnd()
				.ifThisIdIsNotPresentInEntity(login_token).returnStatus(loginTokenIsMissing).and()
				.ifThisIdIsNotPresentInEntity(responsibleEntity).returnStatus(unauthorizedResponsible).and()
				.ifThisIdIsNotPresentInEntity(requestEntity).returnStatus(requestDoesNotExist).and()
				.ifThisIdIsPresentInEntity(answerEntity).returnStatus(requestAlreadyAnswered).and()
				
				.executeAction(action).andFinally()
			.endThisProcedureRetrievingTheResultingData();
		return result;
	}
	
}
