package com.ccp.jn.sync.mensageria;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.ccp.validation.CcpJsonFieldsValidations;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnGenerateRandomToken;
import com.jn.commons.utils.JnTopic;

public class JnSyncMensageriaSender {
	private final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	public static final JnSyncMensageriaSender INSTANCE = new JnSyncMensageriaSender();
	
	private JnSyncMensageriaSender() {
		
	}
	
	private CcpJsonRepresentation send(CcpJsonRepresentation json, String topic, CcpEntity entity) {
		
		String formattedCurrentDateTime = new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss");
	
		CcpJsonRepresentation messageDetails = CcpConstants.EMPTY_JSON
				.put("started", System.currentTimeMillis())
				.put("data", formattedCurrentDateTime)
				.put("request", json)
				.put("topic", topic)
				.putAll(json)
				;
		JnGenerateRandomToken transformer = new JnGenerateRandomToken(20, "messageId");
		CcpJsonRepresentation transformed = messageDetails.getTransformed(transformer);
		
		String messageId = transformed.getAsString("messageId");
		entity.createOrUpdate(transformed);
		
		this.mensageriaSender.send(topic, transformed);
		CcpJsonRepresentation put = messageDetails.put("messageId", messageId);
		return put;
	}

	public CcpJsonRepresentation send(CcpJsonRepresentation json, JnTopic topic) {
		String topicName = topic.name();
		Class<? extends JnTopic> validationClass = topic.getClass();
		CcpJsonFieldsValidations.validate(validationClass, json.content, topicName);
		CcpJsonRepresentation send = this.send(json, topicName, JnEntityAsyncTask.INSTANCE);
		CcpJsonRepresentation result = CcpConstants.EMPTY_JSON.put(topicName, send);
		return result;
	}
	
	public void send(Throwable e, JnTopic topic) {
		CcpJsonRepresentation transformed = new CcpJsonRepresentation(e);
		String name = topic.name();
		this.mensageriaSender.send(name, transformed);
		
	}
}
