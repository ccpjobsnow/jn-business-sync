package com.ccp.jn.sync.validations.login;

import com.ccp.fields.validations.annotations.AllowedValues;
import com.ccp.fields.validations.annotations.ObjectText;
import com.ccp.fields.validations.annotations.ValidationRules;
import com.ccp.fields.validations.enums.AllowedValuesValidations;
import com.ccp.fields.validations.enums.ObjectTextSizeValidations;

@ValidationRules(objectTextsValidations = { 
		@ObjectText(rule = ObjectTextSizeValidations.equalsTo, fields = {
		"password", "token" }, bound = 8) },
	allowedValues = {
				@AllowedValues(rule = AllowedValuesValidations.arrayWithAllowedTexts, fields = {
						"goal" }, allowedValues = { "jobs", "recruiting" }),
				@AllowedValues(rule = AllowedValuesValidations.objectWithAllowedTexts, fields = {
						"channel" }, allowedValues = { "linkedin", "telegram", "friends", "others" }), }

)
public class JnFieldValidationPreRegistration {

}
