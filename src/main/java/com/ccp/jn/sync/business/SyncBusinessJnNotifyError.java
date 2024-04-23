package com.ccp.jn.sync.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.business.utils.JnSyncMensageriaSender;
import com.jn.commons.utils.JnTopics;

public class SyncBusinessJnNotifyError implements Function<Throwable, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(Throwable e) {
		CcpJsonRepresentation md = new CcpJsonRepresentation(e);
		JnSyncMensageriaSender jnMensageria = new JnSyncMensageriaSender();
		CcpJsonRepresentation send = jnMensageria.send(md, JnTopics.notifyError);
		return send;
	}
	
}
