package com.ccp.jn.sync.business.recruiters.resumes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.db.query.CcpDbQueryExecutor;
import com.ccp.especifications.db.query.CcpQueryExecutorDecorator;
import com.ccp.especifications.db.query.ElasticQuery;
import com.ccp.especifications.db.query.Must;
import com.ccp.especifications.db.table.CcpDbTableField;
import com.ccp.process.CcpProcess;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.tables.fields.A3D_candidate;

public class GetResumesStatis implements CcpProcess {

	private final CcpDbQueryExecutor requestExecutor;
	
	
	public GetResumesStatis(CcpDbQueryExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}


	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		Must must = values.getAsObject("_must");
		
		ElasticQuery query = must.endMustAndBackToBool().endBoolAndBackToQuery().endQueryAndBackToRequest();
		
		CcpQueryExecutorDecorator selectFrom = query.selectFrom(this.requestExecutor, JnBusinessEntity.candidate.name());
		
		CcpMapDecorator aggregations = selectFrom.getAggregations();

		CcpMapDecorator total = aggregations.getInternalMap("total");
		
		Integer doc_count = total.getAsIntegerNumber("doc_count");
		
		CcpMapDecorator entireCountry = aggregations.put("doc_count", doc_count).put("key", 0);
		
		CcpMapDecorator allRegions = aggregations.getInternalMap("ddd");
		
		List<CcpMapDecorator> eachRegion = allRegions.getAsMapList("buckets")
				.stream().map(ddd ->  this.getMapDecorator(ddd, 
						A3D_candidate.experience, 
						A3D_candidate.clt, 
						A3D_candidate.btc,
						A3D_candidate.pj
						)).collect(Collectors.toList());

		ArrayList<CcpMapDecorator> results = new ArrayList<>(eachRegion);
		 
		results.add(entireCountry);
		
		CcpMapDecorator put = new CcpMapDecorator().put("results", results);
		
		return put;
	}

	
	private CcpMapDecorator getMapDecorator(CcpMapDecorator ddd, CcpDbTableField... fields) {
		
		CcpMapDecorator values = new CcpMapDecorator()
				.put("ddd", ddd.getAsIntegerNumber("key"))
				.put("total", ddd.getAsIntegerNumber("doc_count"))
				;
		
		for (CcpDbTableField field : fields) {
			String fieldName = field.name();
			CcpMapDecorator internalMap = ddd.getInternalMap(fieldName);
			Double value = internalMap.getAsDoubleNumber("value");
			values = values.put(fieldName, value);
		}
		
		return values;
	}
}
