package com.ccp.jn.sync.recruiters.business.resumes;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;
import com.jn.commons.tables.fields.A3D_candidate;

public class AddPcdFilter implements CcpProcess {

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		Must must = values.getAsObject("_must");
		must = must.term(A3D_candidate.pcd, true);
		CcpMapDecorator put = values.put("_must", must);
		return put;
	}

}
