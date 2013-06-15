package Semantic;

public class Error {
	private int _linea;
	private String _mss;
	
	public Error(String mss, int linea){
		_mss = mss;
		_linea = linea;
	}
	
	public String mss(){
		return _mss + ". Line: " + _linea;
	}
}

enum Types{
	CLASS_ALREADY_DEFINED("Class already was defined"),
	METHOD_ALREADY_DEFINED("Method already was defined"),
	ARGUMENT_ALREADY_DEFINED("Method already was defined"),
	VARIABLE_ALREADY_DEFINED("Variable already was defined");
	
	private final String _type;
	private Types(String type){
		_type = type;
	}
	
	public String mss(){
	   return _type;
	}
}

enum Levels{
	WARM("Warm"),
	ERROR("Error");
	
	private final String _level;
	private Levels(String level){
		_level = level;
	}
	
	public String mss(){
		return _level;
	}
}