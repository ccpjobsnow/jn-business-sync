package com.ccp.jn.sync.validations.login;

import com.ccp.fields.validations.annotations.ObjectRules;
import com.ccp.fields.validations.annotations.ObjectText;
import com.ccp.fields.validations.annotations.ValidationRules;
import com.ccp.fields.validations.enums.ObjectTextSizeValidations;
import com.ccp.fields.validations.enums.ObjectValidations;

@ValidationRules(simpleObjectRules = {
		@ObjectRules(rule = ObjectValidations.requiredFields, fields = { "password", "token" }) },

		objectTextsValidations = {
				@ObjectText(rule = ObjectTextSizeValidations.equalsTo, fields = { "password", "token" }, bound = 8) }

)
public class JnFieldValidationPasswordAndToken {

}
