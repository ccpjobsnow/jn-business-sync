package com.ccp.jn.sync.commons;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.sender.CcpMensageriaSender;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.commons.utils.JnGenerateRandomToken;

public class JnSyncMensageriaSender {
	public final CcpMensageriaSender mensageriaSender = CcpDependencyInjection.getDependency(CcpMensageriaSender.class);
	
	public static final JnSyncMensageriaSender INSTANCE = new JnSyncMensageriaSender();
	
	private JnSyncMensageriaSender() {
		
	}
	
	private CcpJsonRepresentation send(CcpJsonRepresentation values, String topic, CcpEntity entity) {
		
		String formattedCurrentDateTime = new CcpTimeDecorator().getFormattedDateTime("dd/MM/yyyy HH:mm:ss");
	
		CcpJsonRepresentation messageDetails = CcpConstants.EMPTY_JSON
				.put("started", System.currentTimeMillis())
				.put("data", formattedCurrentDateTime)
				.put("request", values)
				.put("topic", topic)
				.putAll(values)
				;
		JnGenerateRandomToken transformer = new JnGenerateRandomToken(20, "messageId");
		CcpJsonRepresentation transformed = messageDetails.getTransformed(transformer);
		
		String messageId = transformed.getAsString("messageId");
		entity.createOrUpdate(transformed);
		
		this.mensageriaSender.send(topic, transformed);
		CcpJsonRepresentation put = messageDetails.put("messageId", messageId);
		return put;
	}

	public CcpJsonRepresentation send(CcpJsonRepresentation values, Enum<?> topic) {
		String topicName = topic.name();
		CcpJsonRepresentation send = this.send(values, topicName, JnEntityAsyncTask.INSTANCE);
		return send;
	}
}
