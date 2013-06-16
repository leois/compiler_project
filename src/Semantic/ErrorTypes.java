package Semantic;

public enum ErrorTypes {
	CLASS_ALREADY_DEFINED("Class already was defined"),
	METHOD_ALREADY_DEFINED("Method already was defined"),
	ARGUMENT_ALREADY_DEFINED("Method already was defined"),
	VARIABLE_ALREADY_DEFINED("Variable already was defined"),
	UNDECLARED_TYPE("Undeclared type"),
	WRONG_RETURN("Wrong return type"),
	WRONG_NUMBER_ARGUMENTS("Wrong number of arguments"),
	WRONG_TYPE_ARGUMENT("Wrong type of arguments"),
	WRONG_TYPE_ASSIGN("Wrong type assignment"),
	WRONG_INDEX("Wrong index"),
	NOT_BOOLEAN("Not a boolean");
	
	private final String _type;
	private ErrorTypes(String type){
		_type = type;
	}
	
	public String mss(){
	   return _type;
	}
}