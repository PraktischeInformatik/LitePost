package org.pi.litepost.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.pi.litepost.applicationLogic.SessionManager;

/**
 * @author Felix Breitweiser
 * class used to validate maps of parameters
 */
public class Validator
{
	private ArrayList<Validation> validations = new ArrayList<>();
	private Map<String, Boolean> results = new HashMap<>();
	private Map<String, String> values = new HashMap<>();
	private SessionManager sessionManager;
	
	public Validator(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	/**
	 * generates an input with a csrf token
	 * @return a hidden input woth the csrf token
	 */
	public Input csrfToken() {
		return ((Input) new Input("hidden", this)
			.class_("csrf-token"))
			.name("csrf_token")
			.value(sessionManager.csrfToken());
	}
	
	/**
	 * Adds a validation for a single parameter
	 * @param name the name of the validation. later used to identify failures/successes
	 * @param pred predicate that validates the parameter
	 * @param paramName the name of the parameter to be validated
	 * @return this
	 */
	public Validator validateSingle(
			String name,
			Predicate<String> pred,
			String paramName)
	{
		return validateSingle(name, id -> id, pred, paramName);
	}
	
	/**
	 * Adds a validation for a single parameter with a transformation
	 * @param name the name of the validation. later used to identify failures/successes
	 * @param pred predicate that validates the parameter
	 * @param trans a transformation to be applied to the parameter before validation
	 * @param paramName the name of the parameter to be validated
	 * @return this
	 */
	public Validator validateSingle(
			String name,
			Function<String, String> trans,
			Predicate<String> pred,
			String paramName)
	{
		return validateMultiple(
				name,
				trans,
				v -> pred.test(v[0]),
				new String[]{paramName});
	}
	
	/**
	 * Adds a validation for a multiple parameters 
	 * @param name the name of the validation. later used to identify failures/successes
	 * @param pred predicate that validates the parameters
	 * @param paramNames the names of the parameters to be validated
	 * @return this
	 */
	public Validator validateMultiple(
			String name,
			Predicate<String[]> pred,
			String... paramNames)
	{
		return validateMultiple(name, id -> id, pred, paramNames);
	}
	
	/**
	 * Adds a validation for a multiple parameters with a transformation
	 * @param name the name of the validation. later used to identify failures/successes
	 * @param pred predicate that validates the parameters
	 * @param trans a transformation to be applied to each parameter before validation
	 * @param paramNames the names of the parameters to be validated
	 * @return this
	 */
	public Validator validateMultiple(
			String name,
			Function<String, String> trans,
			Predicate<String[]> pred,
			String... paramNames)
	{
		validations.add(new Validation(name, trans, pred, paramNames));
		return this;	
	}
	
	/**
	 * Validates that all parameters are non empty
	 * @param name the name of the validation. later used to identify failures/successes 
	 * @param paramNames the parameters to be tested
	 * @return this
	 */
	public Validator validateExists(String name, String... paramNames)
	{
		return validateMultiple(name, id -> id, ss -> {
			for(String s : ss) if(s.isEmpty()) return false;
			return true;
		}, paramNames);
	}
	

	/**
	 * Validates a flag in the parameters. a flag is always valid, but its value is added to the values list
	 * @param name the name of the validation. later used to identify failures/successes 
	 * @param paramName the parameter to be tested
	 * @return this
	 */
	public Validator validateFlag(String string, String paramName) 
	{
		return validateSingle(string, s -> s.isEmpty()?"false":"true", s -> true, paramName);
	}
	
	/**
	 * validates the passed map of parameters
	 * @param params the parameters
	 * @return true if every validation succeeded
	 */
	public boolean validate(Map<String, String> params)
	{
		values.putAll(params);
		validations.forEach(v -> 
		{
			results.put(v.name, v.process(params));
			values.putAll(v.transformedValues);
		});
		return results.values().stream().allMatch(b -> b);
	}
	
	/**
	 * Manually insert a validation
	 * @param name the name of the validation
	 * @return this
	 */
	public Validator manual(String name)
	{
		return manual(name, true);
	}
	
	/**
	 * Manually insert a validation
	 * @param name the name of the validation
	 * @param successfull true, if it should be successful
	 * @return this
	 */
	public Validator manual(String name, boolean successful)
	{
		results.put(name, successful);
		return this;
	}
	
	/**
	 * retrieves the parameter value after transformation
	 * @param paramName the parameters name
	 * @return the parameters value. empty string if the value is not present
	 */
	public String value(String paramName) 
	{
		return values.getOrDefault(paramName, "");
	}
	

	/**
	 * retrieves a flag parameter
	 * @param paramName the parameters name
	 * @return true if flag is set
	 */
	public boolean flag(String paramName) 
	{
		return value(paramName).equals("true");
	}
	
	/**
	 * checks if a validation was valid or not
	 * @param name the name of the validation
	 * @return true if it was valid or if the validation does not exist
	 */
	public boolean valid(String name)
	{
		return results.getOrDefault(name, true);
	}
	
	/**
	 * @author Felix Breitweiser
	 * helper class to hold validations
	 */
	class Validation {
		Map<String, String> transformedValues = new HashMap<>();
		String name;
		String[] paramNames;
		Predicate<String[]> pred;
		Function<String, String> trans;

		public Validation(
				String name,
				Function<String, String> trans,
				Predicate<String[]> pred,
				String... paramNames)
		{
			this.name = name;
			this.trans = trans;
			this.pred = pred;
			this.paramNames = paramNames;
		}
		
		public Validation(
				String name,
				Function<String, String> trans,
				Predicate<String> pred,
				String paramName)
		{
			this(name, trans, p -> pred.test(p[0]), new String[]{paramName});
		}
		
		public boolean process(Map<String, String> params)
		{
			String[] paramValues = new String[paramNames.length];
			for(int i = 0; i < paramNames.length; i++)
			{
				String value = params.getOrDefault(paramNames[i], "");
				paramValues[i] = trans.apply(value);
				transformedValues.put(paramNames[i], paramValues[i]);
			}
			return pred.test(paramValues);
		}
	}
}