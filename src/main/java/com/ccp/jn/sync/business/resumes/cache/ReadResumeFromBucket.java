package com.ccp.jn.sync.business.resumes.cache;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.process.CcpMapTransform;
import com.jn.commons.JnConstants;

public class ReadResumeFromBucket implements CcpMapTransform<String>{

	private final CcpFileBucket bucket;
	private final String resume;
	private final String type;

	public ReadResumeFromBucket(CcpFileBucket bucket, String resume, String type) {
		this.bucket = bucket;
		this.resume = resume;
		this.type = type;
	}

	@Override
	public String transform(CcpMapDecorator values) {
		String resumeInBase64 = this.bucket.read(JnConstants.TENANT, JnConstants.RESUMES_BUCKET + this.type, this.resume);
		return resumeInBase64;
	}

}
