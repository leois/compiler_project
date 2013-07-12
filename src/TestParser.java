import Scanner.*;
import Semantic.E;
import Semantic.Checker.TypeChecker;
import Semantic.SymbolTable.TableBuilderVisitor;
import Parser.*;
import AST.*;
import AST.Visitor.*;
import Generation.Generator;
import java_cup.runtime.Symbol;

import java.awt.print.Printable;
import java.io.FileReader;
import java.util.*;

public class TestParser {
    public static void main(String [] args) {
        try {
            // create a scanner on the input file
        	String dir = "C://Users//Leois Linka//Desktop//ejemplos//";
        	String file = "mio.java";
            scanner s = new scanner(new FileReader(dir+file));
            parser p = new parser(s);
            Symbol root;
	    // replace p.parse() with p.debug_parse() in next line to see trace of
	    // parser shift/reduce actions during parse
            root = p.parse();
            Program program = (Program)root.value;
                //program.accept(new PrettyPrintVisitor());
                TableBuilderVisitor sv = new TableBuilderVisitor();
                program.accept(sv);
                
                System.out.println("===============Table Builder=================");
                List<E> errors = sv.getTable().getAllErrors();
                printErrors(errors);
                
                if ( errors.isEmpty() ){
                	TypeChecker tc = new TypeChecker(sv.getTable());
                    program.accept(tc);
                    System.out.println("===============Type Checker==================");
                    errors = tc.getErrors();
                    printErrors(errors);
                    sv.getTable().printAllUnused();
                    if ( errors.isEmpty() ){
                    	Generator g = new Generator(sv.getTable(), dir);
                        program.accept(g);
                        System.out.println("===============Code Generator=================");
                        System.out.println("Ok!");
                        System.out.println("COMPLETED!");
                    }
                }
                
                
				System.out.print("\n"); 
        } catch (Exception e) {
            // yuck: some kind of error in the compiler implementation
            // that we're not expecting (a bug!)
            System.err.println("Unexpected internal compiler error: " + 
                               e.toString());
            // print out a stack dump
            e.printStackTrace();
        }
    }
    
    public static void printErrors(List<E> errors){
    	boolean has = false;
    	for(E e: errors){
        	System.out.println(e.mss());
        	has = true;
        }
    	if ( !has ) System.out.println("OK!");
    }
}