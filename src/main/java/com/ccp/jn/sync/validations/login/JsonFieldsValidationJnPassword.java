package com.ccp.jn.sync.validations.login;

import com.ccp.validation.annotations.ObjectTextSize;
import com.ccp.validation.annotations.SimpleObject;
import com.ccp.validation.annotations.ValidationRules;
import com.ccp.validation.enums.ObjectTextSizeValidations;
import com.ccp.validation.enums.SimpleObjectValidations;

@ValidationRules(simpleObject = { @SimpleObject(rule = SimpleObjectValidations.requiredFields, fields = { "password" }) },
		objectTextSize  = {
				@ObjectTextSize(rule = ObjectTextSizeValidations.equalsOrGreaterThan, fields = { "password"}, bound = 8) }
)
public class JsonFieldsValidationJnPassword {

}
