package com.ccp.jn.sync.validations.login;

import com.ccp.validation.annotations.AllowedValues;
import com.ccp.validation.annotations.ObjectText;
import com.ccp.validation.annotations.ValidationRules;
import com.ccp.validation.enums.AllowedValuesValidations;
import com.ccp.validation.enums.ObjectTextSizeValidations;

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
