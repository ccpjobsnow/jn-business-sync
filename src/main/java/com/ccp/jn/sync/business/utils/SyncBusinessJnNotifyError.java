package com.ccp.jn.sync.business.utils;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.mensageria.JnSyncMensageriaSender;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncBusinessJnNotifyError implements Function<Throwable, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(Throwable e) {
		JnSyncMensageriaSender.INSTANCE.send(e, JnAsyncBusiness.notifyError);
		return CcpConstants.EMPTY_JSON;
	}
	
}
