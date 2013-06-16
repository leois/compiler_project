package Semantic.SymbolTable.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Semantic.E;
import Semantic.ErrorTypes;


public class Table{
	private Map<Symbol, Clase> _table;
	protected List<E> _errors;
	
	public Table(){
		_table = new HashMap<Symbol, Clase>();
		_errors = new ArrayList<E>();
	}
	
	public void add(Symbol symbol, Clase clase){
		if( symbol.isMapped(_table.keySet() ) ){
			_errors.add(new E(ErrorTypes.CLASS_ALREADY_DEFINED.mss(), clase.getType().getLine()) );
		}
		_table.put(symbol, clase);
	}

	public Map<Symbol, Clase> getTable() {
		return _table;
	}

	public void setTable(Map<Symbol, Clase> table) {
		this._table = table;
	}
	
	public Clase searchClassByName(String var){
		for(Symbol s : _table.keySet()){
			if(s.getId().equals(var))
				return _table.get(s);
		}
		return null;
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
	
	public List<E> getAllErrors(){
		List<E> _all = _errors;
		for( Symbol s : _table.keySet() ){
			_all.addAll(_table.get(s).getErrors());	
			for( Symbol m : _table.get(s).getMethods().keySet() ){
				_all.addAll(_table.get(s).getMethods().get(m).getErrors());
			}
		}
		return _errors;
	}
	
	public void printErrors(){
		for( E error : _errors){
			System.out.println("* " + error.mss());
		}
		for( Symbol s : _table.keySet() ){
			System.out.println("- " + s.getId());	
			for( E e : _table.get(s).getErrors() ){
				System.out.println("** " + e.mss());
			}
			for( Symbol m : _table.get(s).getMethods().keySet() ){
				System.out.println("-- " + m.getId());
				for( E e : _table.get(s).getMethods().get(m).getErrors() ){
					System.out.println("*** " + e.mss());
				}
			}
		}
	}

	public List<E> getErrors() {
		return _errors;
	}

	public void setErrors(List<E> errors) {
		this._errors = errors;
	}
}
