package com.ccp.jn.sync.business.utils;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.sync.commons.JnSyncMensageriaSender;
import com.jn.commons.utils.JnAsyncBusiness;

public class SyncBusinessJnNotifyError implements Function<Throwable, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(Throwable e) {
		CcpJsonRepresentation md = new CcpJsonRepresentation(e);
		JnSyncMensageriaSender.INSTANCE.send(md, JnAsyncBusiness.notifyError);
		return md;
	}
	
}
