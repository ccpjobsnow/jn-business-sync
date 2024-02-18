package com.ccp.jn.sync.validations.login;

import com.ccp.validation.annotations.ObjectRules;
import com.ccp.validation.annotations.ObjectText;
import com.ccp.validation.annotations.ValidationRules;
import com.ccp.validation.enums.ObjectTextSizeValidations;
import com.ccp.validation.enums.ObjectValidations;

@ValidationRules(simpleObjectRules = {
		@ObjectRules(rule = ObjectValidations.requiredFields, fields = { "password", "token" }) },

		objectTextsValidations = {
				@ObjectText(rule = ObjectTextSizeValidations.equalsTo, fields = { "password", "token" }, bound = 8) }

)
public class JnFieldValidationPasswordAndToken {

}
