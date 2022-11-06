package com.ccp.jn.sync;

import org.mindrot.jbcrypt.BCrypt;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.especifications.db.crud.CcpDbCrud;
import com.ccp.exceptions.db.CcpRecordNotFound;
import com.ccp.process.CcpNextStep;
import com.ccp.process.CcpProcess;
import com.ccp.process.CcpStepResult;
import com.jn.commons.JnBusinessEntity;

public class Login implements CcpProcess{

	private CcpProcess decisionTree = values ->{
		
		return new ValidatePassword()
				.addStep(200, new ExecuteLogin())
				.addStep(401, new EvaluateTries()
						.addStep(429, new LockTheUserPassword())
				)
				.goToTheNextStep(values).data;
		
	};

	
	@Override
	public CcpMapDecorator execute(CcpMapDecorator values) {

		JnBusinessEntity loginRequest = JnBusinessEntity.login_request;
		
		String id = loginRequest.getId(values);
		
		CcpDbCrud crud = loginRequest.getCrud();
		
		CcpMapDecorator findById = crud.findById(values, id, 
				    new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_token).put("status", 403)
         		   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.login_request).put("status", 404)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.locked_password).put("status", 401)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.login).put("status", 409)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.pre_registration).put("status", 201)
				   ,new CcpMapDecorator().put("found", false).put("table", JnBusinessEntity.password).put("status", 202)
				   ,new CcpMapDecorator().put("found", true).put("table", JnBusinessEntity.password).put("action", this.decisionTree)
				);
		

		return findById;
		
	}

}

class ValidatePassword extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		CcpMapDecorator pass = values.getInternalMap("record");
		CcpMapDecorator valores = values.getInternalMap("values");
	
		String password = valores.getAsString("password");
		String passwordDb = pass.getAsString("password");
		
		boolean senhaIncorreta = BCrypt.checkpw(passwordDb, password) == false;
		
		if(senhaIncorreta) {
			return new CcpStepResult(valores, 401, this);
		}
		return new CcpStepResult(valores, 200, this);
	}
}

class LockTheUserPassword extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {

		JnBusinessEntity.locked_password.save(values.getSubMap("email", "userAgend", "macAddress", "coordinates", "ip"));
		
		return new CcpStepResult(values, 429, this);
	}
	
}


class EvaluateTries extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		try {
			CcpMapDecorator tries = JnBusinessEntity.password_tries.get(values);
			Integer tentativas = tries.getAsIntegerNumber("tries");
			if(tentativas > 3) {
				return new CcpStepResult(values.put("tries", tentativas), 429, this);
			}
			tries = tries.put("tries", tentativas + 1);
			JnBusinessEntity.password_tries.save(tries);
			
		} catch (CcpRecordNotFound | NullPointerException e) {
			CcpMapDecorator tries = values.getSubMap("email").put("tries", 1);
			JnBusinessEntity.password_tries.save(tries);
		}
		
		return new CcpStepResult(values, 401, this);
	}
}



class ExecuteLogin extends CcpNextStep{

	@Override
	public CcpStepResult executeThisStep(CcpMapDecorator values) {
		
		JnBusinessEntity.login.save(values.getSubMap("email", "userAgend", "macAddress", "coordinates", "ip"));

		CcpTextDecorator textDecorator = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%*&_-+=()").text();
		String token = textDecorator.generateToken(8);
		values = values.put("token", token);
		CcpMapDecorator userStats = JnBusinessEntity.job_user_stats.get(values).getSubMap("loginCount", "lastLogin");
		values = values.putAll(userStats);
		return new CcpStepResult(values, 200, this);
	}
	
}

