package Generation;

import AST.*;
import AST.Visitor.Visitor;
import Semantic.SymbolTable.*;
import Semantic.SymbolTable.Structure.*;

public class Generator implements Visitor {

	private Table _table;
	private String _currentClass;
	private String _currentMethod;
	private String _dir;

	private int stackSize, maxStackSize;

	public Generator(Table table, String folder) {
		_table = table;
		_dir = folder;
	}

	private void resetStack() {
		stackSize = maxStackSize = 0;
	}

	private void incrStack() {
		incrStack(1);
	}

	private void incrStack(int n) {
		stackSize += n;
		maxStackSize = Math.max(maxStackSize, stackSize);
	}

	private void decrStack() {
		decrStack(1);
	}

	private void decrStack(int n) {
		stackSize -= n;
		if (stackSize < 0)
			throw new IllegalArgumentException("Generator::decrStack");
	}

	private void saveStack() {
		JasminAux.newline();
		JasminAux.directive(".limit stack " + maxStackSize);

		if (stackSize != 0)
			throw new IllegalArgumentException("Generator::saveStack");
	}

	private void emitStandardConstructor(String baseClass) {
		JasminAux.directive(".method public <init>()V");

		JasminAux.code(withConstant("aload", 0));
		JasminAux.code("invokenonvirtual " + baseClass + "/<init>()V");
		JasminAux.code("return");

		JasminAux.directive(".end method");
	}

	private int setupLocalsArray(Method md) {
		int pos = 0;

		for (Type t : md.getArgumentList())
			t.setOffset(++pos);

		for (Type t : md.getVariables().values())
			t.setOffset(++pos);

		return pos + 1;
	}

	private Method getMethodDescriptor() {
		return _table.getMethod(_currentMethod, _currentClass);
	}

	private boolean isReferenceType(Type t) {
		return !(t instanceof IntegerType || t instanceof BooleanType);
	}

	private boolean isLocalVar(String var) {
		Method method = getMethodDescriptor();

		return method.getVarType(var) != null;
	}

	private String withConstant(String cmd, int c) {
		if (cmd.equals("iconst")) {
			if (c >= -1 && c <= 5)
				return "iconst_" + (c == -1 ? "m1" : c);
			else if (c >= -128 && c <= 127)
				return "bipush " + c;
			else
				return "ldc " + c;
		}

		return cmd + (c >= 0 && c <= 3 ? '_' : ' ') + c;
	}

	private String typeDescriptor(Type t) {
		if (t instanceof IntegerType)
			return "I";
		else if (t instanceof BooleanType)
			return "B";
		else if (t instanceof IntArrayType)
			return "[I";
		else if (t instanceof IdentifierType)
			return "L" + (IdentifierType) t + ";";

		return null;
	}

	private String methodSignatureDescriptor(Method md) {
		String d = "(";

		for (Type t : md.getArgumentList())
			d += typeDescriptor(t);

		d += ")" + typeDescriptor(md.getReturnType());
		return d;
	}

	public void visit(Program n) {
		JasminAux.init();
		n.m.accept(this);
		JasminAux.save();

		for (int i = 0; i < n.cl.size(); i++) {
			JasminAux.init();
			n.cl.get(i).accept(this);
			JasminAux.save();
		}
	}

	public void visit(MainClass n) {
//		String claseName = n.i1.s;
//		JasminAux.setClassName(_dir + claseName);
//		_currentClass = claseName;
//
//		JasminAux.directive(".class public " + claseName);
//		JasminAux.directive(".super java/lang/Object");
//
//		JasminAux.newline2();
//		emitStandardConstructor("java/lang/Object");
//
//		Method method = _table.getMethod("main", claseName);
//
//		JasminAux.newline2();
//		JasminAux.directive(".method public static main([Ljava/lang/String;)V");
//		JasminAux.directive(".limit locals " + setupLocalsArray(method));
//		JasminAux.newline();
//
//		_currentMethod = "main";
//		Util.reset();
//		resetStack();
//
//		n.s.accept(this);
//
//		JasminAux.code("return");
//
//		saveStack();
//		JasminAux.directive(".end method");
	}

	public void visit(ClassDeclSimple n) {
		Identifier idSuperClass = new Identifier("java/lang/Object", n.getLine());
		ClassDeclExtends cde = new ClassDeclExtends(n.i, idSuperClass, n.vl, n.ml, n.getLine());
		visit(cde);
	}

	public void visit(ClassDeclExtends n) {
		String claseName = n.i.s;
		String superClase = n.j.s;
		_currentClass = claseName;
		JasminAux.setClassName(_dir+claseName);

		JasminAux.directive(".class public " + claseName);
		JasminAux.directive(".super " + superClase);

		if (n.vl.size() > 0)
			JasminAux.newline();
		for (int i = 0; i < n.vl.size(); i++)
			n.vl.get(i).accept(this);

		JasminAux.newline2();
		emitStandardConstructor(superClase);

		for (int i = 0; i < n.ml.size(); i++) {
			JasminAux.newline2();
			n.ml.get(i).accept(this);
		}
	}

	public void visit(VarDecl n) {
		String variable = n.i.s;
		String fieldT = typeDescriptor(n.t);

		JasminAux.directive(".field protected " + variable + " " + fieldT);
	}

	public void visit(MethodDecl n) {
		String methodName = n.i.s;
		Method method = _table.getMethod(methodName, _currentClass);
		String methodSig = methodSignatureDescriptor(method);
		_currentMethod = methodName;

		JasminAux.directive(".method public " + methodName + methodSig);
		JasminAux.directive(".limit locals " + setupLocalsArray(method));
		JasminAux.newline();

		Util.reset();
		resetStack();

		for (int i = 0; i < n.sl.size(); i++)
			n.sl.get(i).accept(this);

		n.e.accept(this);

		if (isReferenceType(n.t))
			JasminAux.code("areturn");
		else
			JasminAux.code("ireturn");

		decrStack();
		saveStack();
		JasminAux.directive(".end method");
	}

	public void visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++)
			n.sl.get(i).accept(this);
	}

	public void visit(If n) {
		String trueL = Util.nextName("if_true");
		String falseL = Util.nextName("if_false");
		String nextL = Util.nextName("if_next");

		evalBooleanJump(n.e, trueL, falseL);

		JasminAux.label(trueL);
		n.s1.accept(this);
		JasminAux.code("goto " + nextL);

		JasminAux.label(falseL);
		n.s2.accept(this);

		JasminAux.label(nextL);
	}

	public void visit(While n) {
		String loop = Util.nextName("loop");
		String trueL = Util.nextName("while_true");
		String falseL = Util.nextName("while_false");

		JasminAux.label(loop);
		evalBooleanJump(n.e, trueL, falseL);

		JasminAux.label(trueL);
		n.s.accept(this);
		JasminAux.code("goto " + loop);

		JasminAux.label(falseL);
	}

	public void visit(Print n) {
		JasminAux.code("getstatic java/lang/System/out Ljava/io/PrintStream;");
		incrStack();
		n.e.accept(this);

		JasminAux
				.code("invokestatic java/lang/String/valueOf(I)Ljava/lang/String;");

		JasminAux
				.code("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
		decrStack(2);
	}

	public void visit(Assign n) {
		String id = n.i.s;
		Clase clase = _table.searchClassByName(_currentClass);

		if (id.equals("void@")) {
			n.e.accept(this);
			JasminAux.code("pop");
			decrStack();
		} else if (!isLocalVar(id)) {
			// field var
			JasminAux.code(withConstant("aload", 0));
			incrStack();
			n.e.accept(this);

			Type t = clase.searchVariableByName(id);
			String varTypeD = typeDescriptor(t);

			JasminAux.code("putfield " + _currentClass + "/" + id + " "
					+ varTypeD);
			decrStack(2);
		} else {
			// local var
			n.e.accept(this);

			Type t = getMethodDescriptor().getVarType(id);

			String cmd = isReferenceType(t) ? "astore" : "istore";
			JasminAux.code(withConstant(cmd, t.getOffset()));
			decrStack();
		}
	}

	public void visit(ArrayAssign n) {
		n.i.accept(this);
		n.e1.accept(this);
		n.e2.accept(this);
		JasminAux.code("iastore");
		decrStack(3);
	}

	public void visit(And and) {
		and.e1.accept(this);
		and.e2.accept(this);
		JasminAux.code("iand");
		decrStack();
	}

	public void visit(Not n) {
		JasminAux.code(withConstant("iconst", 1));
		incrStack();
		
		n.e.accept(this);

		JasminAux.code("isub");
		decrStack();
	}

	public void visit(LessThan n) {
		visitComparison(n, "less_than");
	}

	public void visit(Plus n) {
		n.e1.accept(this);
		n.e2.accept(this);
		JasminAux.code("iadd");
		decrStack();
	}

	public void visit(Minus n) {
		n.e1.accept(this);
		n.e2.accept(this);
		JasminAux.code("isub");
		decrStack();
	}

	public void visit(Times n) {
		n.e1.accept(this);
		n.e2.accept(this);
		JasminAux.code("imul");
		decrStack();
	}

	public void visit(ArrayLookup n) {
		n.e1.accept(this);
		n.e2.accept(this);
		JasminAux.code("iaload");
		decrStack();
	}

	public void visit(ArrayLength n) {
		n.e.accept(this);
		JasminAux.code("arraylength");
	}
	
	public void visit(Call n) {
		IdentifierType t = (IdentifierType) n.e.getType();
		String claseName = t.s;
		String methodName = n.i.s;
		Method method = _table.getMethod(methodName, claseName);

		String callDescriptor = claseName + "/" + methodName
				+ methodSignatureDescriptor(method);
		
		n.e.accept(this);
		
		for(int i=0; i<n.el.size(); i++)
			n.el.get(i).accept(this);

		JasminAux.code("invokevirtual " + callDescriptor);
		decrStack(n.el.size());
	}

	public void visit(IntegerLiteral n) {
		JasminAux.code(withConstant("iconst", n.i));
		incrStack();
	}

	public void visit(True trueV) {
		JasminAux.code(withConstant("iconst", 1));
		incrStack();
	}

	public void visit(False falseV) {
		JasminAux.code(withConstant("iconst", 0));
		incrStack();
	}

	public void visit(This thisV) {
		JasminAux.code(withConstant("aload", 0));
		incrStack();
	}

	public void visit(NewArray n) {
		n.e.accept(this);
		JasminAux.code("newarray int");
	}

	public void visit(NewObject n) {
		String claseName = n.i.s;

		JasminAux.code("new " + claseName);
		incrStack();

		JasminAux.code("dup");
		incrStack();

		JasminAux.code("invokespecial " + claseName + "/<init>()V");
		decrStack();
	}

	public void visit(Identifier n) {
		String variable = n.s;

		if (!isLocalVar(variable)) {
			Clase clase = _table.searchClassByName(_currentClass);
			
			Type t = clase.searchVariableByName(variable);
			String tipo = typeDescriptor(t);

			JasminAux.code(withConstant("aload", 0));
			incrStack();
			JasminAux.code("getfield " + _currentClass + "/" + variable + " "
					+ tipo);
		} else {
			Method method = getMethodDescriptor();
			Type t = method.getVarType(variable);

			String cmd = isReferenceType(t) ? "aload" : "iload";
			JasminAux.code(withConstant(cmd, t.getOffset()));
			incrStack();
		}
	}
	
	public void visit(IdentifierExp n) {
		String variable = n.s;

		if (!isLocalVar(variable)) {
			Clase clase = _table.searchClassByName(_currentClass);
			Type t = clase.searchVariableByName(variable);
			String tipo = typeDescriptor(t);

			JasminAux.code(withConstant("aload", 0));
			incrStack();
			JasminAux.code("getfield " + _currentClass + "/" + variable + " "
					+ tipo);
		} else {
			Method method = getMethodDescriptor();
			Type t = method.getVarType(variable);

			String cmd = isReferenceType(t) ? "aload" : "iload";
			JasminAux.code(withConstant(cmd, t.getOffset()));
			incrStack();
		}
	}

	public void visit(Formal n) {
		throw new IllegalArgumentException("BytecodeEmitter::Formal");
	}

	public void visit(IntArrayType n) {
		throw new IllegalArgumentException("BytecodeEmitter::IntArrayType");
	}

	public void visit(BooleanType n) {
		throw new IllegalArgumentException("BytecodeEmitter::BooleanType");
	}

	public void visit(IntegerType n) {
		throw new IllegalArgumentException("BytecodeEmitter::IntegerType");
	}

	public void visit(IdentifierType n) {
		throw new IllegalArgumentException("BytecodeEmitter::IdentifierType");
	}

	private void evalBooleanJump(Exp e, String trueL, String falseL) {
		if (e instanceof False) {
			JasminAux.code("goto " + trueL);
		} else if (e instanceof False) {
			JasminAux.code("goto " + falseL);
		} else if (e instanceof Not) {
			evalBooleanJump(((Not) e).e, falseL, trueL);
		} else if (e instanceof And) {
			And and = (And) e;
			String cont = Util.nextName("and_cont");

			evalBooleanJump(and.e1, cont, falseL);
			JasminAux.label(cont);
			evalBooleanJump(and.e2, trueL, falseL);
		}  else if (e instanceof LessThan) {
			LessThan l = (LessThan) e;
			evalConditionJump("lt", l.e1, l.e2, trueL, falseL);
		}  else if (e instanceof IdentifierExp) {
			IdentifierExp e1 = (IdentifierExp) e;
			IntegerLiteral e2 = new IntegerLiteral(0, e1.getLine()); 
			evalConditionJump("ne", e1, e2, trueL, falseL);
		} else if (e instanceof Call) {
			Call e1 = (Call) e;
			IntegerLiteral e2 = new IntegerLiteral(0, e1.getLine()); 
			evalConditionJump("ne", e1, e2, trueL, falseL);
		} else {
			throw new IllegalArgumentException("evalBooleanJump");
		}
	}

	private void evalConditionJump(String cond, Exp e1, Exp e2, String trueL,
			String falseL) {

		e1.accept(this);
		e2.accept(this);

		char typeCode = isReferenceType(e1.getType()) ? 'a' : 'i';
		String cmd = "if_" + typeCode + "cmp" + cond;

		JasminAux.code(cmd + ' ' + trueL);
		decrStack(2);
		JasminAux.code("goto " + falseL);
	}

	private void visitComparison(Exp cmp, String s) {
		String trueL = Util.nextName(s);
		String falseL = Util.nextName("not_" + s);
		String nextL = Util.nextName("next");

		evalBooleanJump(cmp, trueL, falseL);

		JasminAux.label(trueL);
		JasminAux.code(withConstant("iconst", 1));
		incrStack();
		JasminAux.code("goto " + nextL);

		JasminAux.label(falseL);
		JasminAux.code(withConstant("iconst", 0));
		incrStack();

		JasminAux.label(nextL);
	}

	@Override
	public void visit(Display n) {
		JasminAux.code("getstatic java/lang/System/out Ljava/io/PrintStream;");
	    incrStack();

	    n.e.accept(this);

	    JasminAux.code("invokestatic java/lang/String/valueOf(I)Ljava/lang/String;");

	    JasminAux.code("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
	    decrStack(2);
	}
	@Override
	public void visit(Empty n) {}
	
	//===========================================================================//
	
	
	
}
