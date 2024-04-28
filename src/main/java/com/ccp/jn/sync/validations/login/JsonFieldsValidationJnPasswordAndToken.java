package com.ccp.jn.sync.validations.login;

import com.ccp.validation.annotations.ObjectTextSize;
import com.ccp.validation.annotations.Regex;
import com.ccp.validation.annotations.SimpleObject;
import com.ccp.validation.annotations.ValidationRules;
import com.ccp.validation.enums.ObjectTextSizeValidations;
import com.ccp.validation.enums.SimpleObjectValidations;

@ValidationRules(
		regex = {
				@Regex(value = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", fields = "password")
		},
		simpleObject = {
		@SimpleObject(rule = SimpleObjectValidations.requiredFields, fields = { "password", "token" }) },

		objectTextSize  = {
				@ObjectTextSize(rule = ObjectTextSizeValidations.equalsTo, fields = { "token" }, bound = 8) 
				,@ObjectTextSize(rule = ObjectTextSizeValidations.equalsOrGreaterThan, fields = { "password"}, bound = 8) }

)
public class JsonFieldsValidationJnPasswordAndToken {

}
