package com.ccp.jn.sync.recruiters.business.resumes;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.Must;
import com.ccp.process.CcpProcess;
import com.jn.commons.tables.fields.A3D_candidate;

public class AddSeniorityFilter implements CcpProcess {

	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {
		
		Must must = values.getAsObject("_must");
		Object seniority = values.get(A3D_candidate.seniority.name());
		must = must.term(A3D_candidate.seniority, seniority);
		CcpMapDecorator put = values.put("_must", must);

		return put;
	}

}
