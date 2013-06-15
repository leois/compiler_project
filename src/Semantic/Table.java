package Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table{
	private Map<Symbol, Clase> _table;
	protected List<Error> _errors;
	
	public Table(){
		_table = new HashMap<Symbol, Clase>();
		_errors = new ArrayList<Error>();
	}
	
	public void add(Symbol symbol, Clase clase){
		if( symbol.isMapped(_table.keySet() ) ){
			_errors.add(new Error(Types.CLASS_ALREADY_DEFINED.mss(), clase.getType().getLine()));
		}
		_table.put(symbol, clase);
	}

	public Map<Symbol, Clase> getTable() {
		return _table;
	}

	public void setTable(Map<Symbol, Clase> table) {
		this._table = table;
	}
	
	public void printTable(){
		for( Symbol s : _table.keySet() ){
			System.out.println("- " + s.getId());	
			for( Symbol v : _table.get(s).getVariables().keySet() ){
				System.out.println("-- " + v.getId() + " : " + _table.get(s).getVariables().get(v).toString());
			}
			for( Symbol m : _table.get(s).getMethods().keySet() ){
				System.out.println("-- " + m.getId());
				for( Symbol a : _table.get(s).getMethods().get(m).getArguments().keySet() ){
					System.out.println("---* " + a.getId() + " : " + _table.get(s).getMethods().get(m).getArguments().get(a).toString());
				}
				for( Symbol vv : _table.get(s).getMethods().get(m).getVariables().keySet() ){
					System.out.println("--- " + vv.getId() + " : " + _table.get(s).getMethods().get(m).getVariables().get(vv).toString());
				}
			}
		}
	}
	
	public void printErrors(){
		for( Error error : _errors){
			System.out.println("* " + error.mss());
		}
		for( Symbol s : _table.keySet() ){
			System.out.println("- " + s.getId());	
			for( Error e : _table.get(s).getErrors() ){
				System.out.println("** " + e.mss());
			}
			for( Symbol m : _table.get(s).getMethods().keySet() ){
				System.out.println("-- " + m.getId());
				for( Error e : _table.get(s).getMethods().get(m).getErrors() ){
					System.out.println("*** " + e.mss());
				}
			}
		}
	}

	public List<Error> getErrors() {
		return _errors;
	}

	public void setErrors(List<Error> errors) {
		this._errors = errors;
	}
}
