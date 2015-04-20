package semant;

import syntaxtree.*;

public class TranslatorVisitor extends visitor.ExpDepthFirstVisitor {

  private SymbolTable  classTable;
  private frame.Frame  currFrame;
  private ClassInfo    currClass;
  private MethodInfo   currMethod; 
  private tree.Exp     currThis;
  private Frag         frags;		// Linked list of accumlated fragments.
  private boolean      optimize;	// Do we want to optimize?

  public TranslatorVisitor(SymbolTable t, frame.Frame f, boolean optim) {
    classTable = t;
    currFrame  = f;
    currClass  = null;
    currMethod = null; 
    currThis   = null;
    frags      = null;
    optimize   = optim;
  }  

  public Frag getResult() {
    // Reverse frags and return it.
    Frag old = frags;
    frags = null;
    while (old != null) {
      Frag temp = old.next;
      old.next = frags;
      frags = old;
      old = temp;
    }
    return frags;
  }

  // Identifier i1,i2;
  // Statement s;
  // Here I give you the complete code for MainClass:
  public semant.Exp visit(MainClass n) {    
    String id = n.i1.toString();

    currClass = classTable.get(id);

    currFrame = currFrame.newFrame(new temp.Label("main"), null);
    
    semant.Exp body = n.s.accept(this);
    procEntryExit(body, currFrame);

    return null; 
  }

  // Exp e1, e2;
  public semant.Exp visit(And n) {
    IfThenElseExp and = new IfThenElseExp(n.e1.accept(this), n.e2.accept(this),
            new Ex(new tree.CONST(0)));

    return and;
  }

  // Identifier i;
  // Exp e1, e2;
  public semant.Exp visit(ArrayAssign n) {
    // base address of array
    tree.Exp arrayAddress = n.i.accept(this).unEx();

    tree.Exp index = n.e1.accept(this).unEx();

    // account for length field
    index = plus(index, new tree.CONST(1));

    // account for memory used
    tree.Exp allocation = mul(new tree.CONST(currFrame.wordSize()), index);

    // move new value into correct spot
    tree.Stm arr = new tree.MOVE(new tree.MEM(plus(arrayAddress, allocation)),
            n.e2.accept(this).unEx());

    return new Nx(arr);
  }

  // Exp e1, e2;
  public semant.Exp visit(ArrayLookup n) {
    // base address of array
    tree.Exp arrayAddress = n.e1.accept(this).unEx(); 

    tree.Exp index = n.e2.accept(this).unEx();

    // acount for length field
    index = plus(index, new tree.CONST(1));

    // acount for memory used
    tree.Exp allocation = mul(new tree.CONST(currFrame.wordSize()), index);

    // return value from correct spot
    return new Ex(new tree.MEM(plus(arrayAddress, allocation)));
  }

  // Exp e;
  public semant.Exp visit(ArrayLength n) {
    // first field is length
    tree.Exp length = n.e.accept(this).unEx();

    return new Ex(length);
  }

  // Identifier i;
  // Exp e;
  public semant.Exp visit(Assign n) {
    // simple move
    tree.Stm assignment = new tree.MOVE(n.i.accept(this).unEx(),
            n.e.accept(this).unEx());

    return new Nx(assignment);
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public semant.Exp visit(Call n)
	{
		tree.Exp caller = n.e.accept(this).unEx();
		tree.ExpList params = buildExpList(n.el, 0);
		params = new tree.ExpList(caller, params); //add implicit parameter
		tree.Exp call  = new tree.CALL(new tree.NAME(new temp.Label(n.fullname)), params);
		return new Ex(call);
	}
  public tree.ExpList buildExpList(syntaxtree.ExpList lst, int pos)
	{
	if(lst.size() == 0)
		return null;
	if(pos == lst.size() - 1)
		return new tree.ExpList(lst.elementAt(pos).accept(this).unEx(), null);

	return new tree.ExpList(lst.elementAt(pos).accept(this).unEx(),
				buildExpList(lst, pos + 1));
	}

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public semant.Exp visit(ClassDeclSimple n) {
    VariableInfo variableInfo = null;
    int index = 0;

    currClass = classTable.get(n.i.s);

    for (int i = 0; i < n.vl.size(); i++) {
      variableInfo = currClass.getField(n.vl.elementAt(i).i.s);
      variableInfo.access = new InHeap(index);
      index += currFrame.wordSize();
    }

    for (int i = 0; i < n.ml.size(); i++) {
      n.ml.elementAt(i).accept(this);
    }

    return null;
  }

  // False
  public semant.Exp visit(False n) {
    return new Ex(new tree.CONST(0)); 
  }

  // String s;
  public semant.Exp visit(Identifier n) {
    VariableInfo variableInfo = null;

    if (currMethod != null) {
      variableInfo = currMethod.getVar(n.s);
      if (variableInfo != null) {
        return new Ex(variableInfo.access.exp(new tree.TEMP(currFrame.FP())));
      }

      variableInfo = currClass.getField(n.s);
      return new Ex(variableInfo.access.exp(currThis));
    }

    variableInfo = currClass.getField(n.s);
    tree.Exp identifierValue = variableInfo.access.exp(currThis);
    return new Ex(identifierValue);
  }

  // String s;
  public semant.Exp visit(IdentifierExp n) {
    VariableInfo variableInfo = null;

    if (currMethod != null) {
      variableInfo = currMethod.getVar(n.s);
      if (variableInfo != null) {
        if (variableInfo.access == null) {
          System.out.println(variableInfo.type.toString() + " is null");
        }
        return new Ex(variableInfo.access.exp(new tree.TEMP(currFrame.FP())));
      }
      variableInfo = currClass.getField(n.s);
      return new Ex(variableInfo.access.exp(currThis));
    }
    variableInfo = currClass.getField(n.s);
    return new Ex(variableInfo.access.exp(currThis));
  }

  // Exp e;
  // Statement s1, s2;
  public semant.Exp visit(If n) {
    IfThenElseExp ifThenElseExp = new IfThenElseExp(n.e.accept(this),
            n.s1.accept(this), n.s2.accept(this));

    return ifThenElseExp;
  }

  // int i;
  public semant.Exp visit(IntegerLiteral n) {
    return new Ex(new tree.CONST(n.i));
  }

  // Exp e1, e2;
  public semant.Exp vist(LessThan n) {
    tree.Exp left = n.e1.accept(this).unEx();
    tree.Exp right = n.e2.accept(this).unEx();

    return new RelCx(tree.CJUMP.LT, left, right);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public semant.Exp visit(MethodDecl n) {
	currMethod = currClass.getMethod(n.i.s);
	String fullname = currClass.getName() + "$" + n.i.s; 
	currFrame = currFrame.newFrame(new temp.Label(fullname), generateFalseBL(n.fl.size() + 1));

	int cnt = 0;
	frame.AccessList curr = currFrame.formals.tail;
	while(curr != null) //allocate space for formals
	{
	    VariableInfo vInfo = currMethod.getVar(n.fl.elementAt(cnt++).i.s);
	    vInfo.access = curr.head;
	    curr = curr.tail;
	}

	currThis = currFrame.formals.head.exp(new tree.TEMP(currFrame.FP())); //set up currThis for future references

	for(int i = 0; i < n.vl.size(); i++) //allocate space for local variables
	{
	    VariableInfo vInfo = currMethod.getVar(n.vl.elementAt(i).i.s);
	    vInfo.access = currFrame.allocLocal(false);
	}

	tree.Stm bodySeq = buildSEQ(n.sl, 0); //build SEQ from statements in method
	if(bodySeq == null)
	    bodySeq = new tree.MOVE(new tree.TEMP(currFrame.RVCallee()),n.e.accept(this).unEx());
	else
	    bodySeq = new tree.SEQ(bodySeq, new tree.MOVE(new tree.TEMP(currFrame.RVCallee()),n.e.accept(this).unEx()));

	Exp methodman = new Nx(bodySeq);
	procEntryExit(methodman, currFrame);
	return methodman;
  }
	public tree.Stm buildSEQ(StatementList sl, int pos)
    {
	if(sl.size() <= 0)
	    return new tree.EXPR(new tree.CONST(0));
	if(pos == sl.size()-1)
	    return sl.elementAt(pos).accept(this).unNx();

	tree.Stm exp = sl.elementAt(pos).accept(this).unNx();
	return new tree.SEQ(exp, buildSEQ(sl, pos+1));
    }
	public util.BoolList generateFalseBL(int size)
    {
	util.BoolList blist = new util.BoolList(false, null);
	util.BoolList end = blist;
	for(int i = 0; i < size-1; i++)
	{
	    end.tail = new util.BoolList(false, null);
	    end = end.tail;
	}
	return blist;
    }

  // Exp e1, e2;
  public semant.Exp visit(Minus n) {
    tree.Exp left = ((Ex)n.e1.accept(this)).unEx();
    tree.Exp right = ((Ex)n.e2.accept(this)).unEx();

    return new Ex(plus(left, right));
  }

  // Exp e;
  public semant.Exp visit(NewArray n) {
    tree.Exp arrAt = n.e.accept(this).unEx();
    tree.ExpList parameters = null;

    if (arrAt instanceof tree.CONST) {
      tree.CONST length = (tree.CONST)arrAt;
      parameters = new tree.ExpList(new tree.CONST(length.value + 1),
                                    new tree.ExpList(new
                                        tree.CONST(currFrame.wordSize()),
                                        null));
    }
    else {
      parameters = new tree.ExpList(plus(arrAt, new tree.CONST(1)),
                                    new tree.ExpList(new
                                        tree.CONST(currFrame.wordSize()),
                                        null));
    }
    temp.Temp temp = new temp.Temp();
    tree.Stm arr = new tree.MOVE(new tree.TEMP(temp), new tree.MEM(
                currFrame.externalCall("calloc", parameters)));
    tree.Exp moveLength = new tree.ESEQ(arr, new tree.TEMP(temp));
      
    return new Ex(moveLength);
  }

  // Exp e;
  public semant.Exp visit(NewObject n) {
    int numberOfFields = classTable.get(n.i.s).getFieldsCount();
    tree.ExpList parameters = new tree.ExpList(new tree.CONST(numberOfFields),
                                               new tree.ExpList(new
                                                   tree.CONST(currFrame.wordSize()),
                                                   null));
    tree.Exp obj = currFrame.externalCall("calloc", parameters);

    return new Ex(obj);
  }

  // Exp e;
  public semant.Exp visit(Not n) {
    tree.Exp value = n.e.accept(this).unEx();

    if (optimize) {
      tree.CONST negative = (tree.CONST)value;

      if (negative.value == 0) {
        return new Ex(new tree.CONST(1));
      }
      return new Ex(new tree.CONST(0));
    }
    return new RelCx(tree.CJUMP.EQ, value, new tree.CONST(0));
  }

  // Exp e1, e2;
  public semant.Exp visit(Plus n) {
    tree.Exp left = ((Ex)n.e1.accept(this)).unEx();
    tree.Exp right = ((Ex)n.e2.accept(this)).unEx();

    return new Ex(plus(left, right));
  }

  // Exp e;
  public semant.Exp visit(Print n) {
    semant.Exp semantExp = n.e.accept(this);
    tree.ExpList expList = new tree.ExpList(semantExp.unEx(), null);
    tree.Exp print = currFrame.externalCall("printInt", expList);

    return new Ex(print);
  }

  // This
  public semant.Exp visit(This n) {
    return new Ex(currThis); 
  }

  // Exp e1, e2;
  public semant.Exp visit(Times n) {
    tree.Exp left = ((Ex)n.e1.accept(this)).unEx();
    tree.Exp right = ((Ex)n.e2.accept(this)).unEx();

    return new Ex(plus(left, right));
  }

  // True
  public semant.Exp visit(True n) {
    return new Ex(new tree.CONST(1)); 
  }

  // Exp e;
  // Statement s;
  public semant.Exp visit(While n) {
    temp.Label condition = new temp.Label();
    temp.Label body = new temp.Label();
    temp.Label finished = new temp.Label();

    Exp expression = n.e.accept(this);
    Exp statement = n.s.accept(this);
    tree.Stm bodyStatement = statement.unNx();

    if (bodyStatement == null) {
      return new Nx(new tree.SEQ(new tree.LABEL(condition),
                                 new tree.SEQ(statement.unCx(condition, finished),
                                 new tree.LABEL(finished))));
    }

    return new Nx(new tree.SEQ(new tree.LABEL(condition),
                               new tree.SEQ(statement.unCx(body, finished),
                                            new tree.SEQ(new tree.LABEL(body),
                                                         new tree.SEQ(bodyStatement,
                                                         new tree.SEQ(new tree.JUMP(condition),
                                                                      new tree.LABEL(finished)))))));
  }

  // Now we have some auxiliary functions:

  // Create a fragment for a function and add it to the front of frags.
  private void procEntryExit(Exp body, frame.Frame funcFrame) {
    Frag func = new ProcFrag(funcFrame.procEntryExit1(body.unNx()), funcFrame);
    func.next = frags;
    frags = func;
  }

  // plus and mul are useful abbreviations that could do simple optimizations.

  private tree.Exp plus(tree.Exp e1, tree.Exp e2) {
    return new tree.BINOP(tree.BINOP.PLUS, e1, e2);
  }

  private tree.Exp mul(tree.Exp e1, tree.Exp e2) {
    return new tree.BINOP(tree.BINOP.MUL, e1, e2);
  }

  // Finally, we have several nested auxiliary classes:

  class InHeap extends frame.Access {
    int offset;

    InHeap(int o) {offset=o;}

    // Here the base pointer will be the "this" pointer to the object.
    public tree.Exp exp(tree.Exp basePtr) {
      return new tree.MEM(plus(basePtr, new tree.CONST(offset)));
    }
  }

  // The subclasses of semant.Exp (Ex, Nx, Cx, RelCx, IfThenElseExp, ...)
  // naturally represent the various phrases of the abstract syntax.
  // They let us hold off on generating tree code for a phrase until
  // we see the *context* in which it is used.

  class Ex extends Exp { 			// page 141
    tree.Exp exp;
    Ex(tree.Exp e) {exp=e;}

    tree.Exp unEx() {return exp;}

    tree.Stm unNx() {return new tree.EXPR(exp);}

    tree.Stm unCx(temp.Label t, temp.Label f) {
      return new tree.CJUMP(tree.CJUMP.NE, exp, new tree.CONST(0), t, f);
    }
  }

  class Nx extends Exp { 			// page 141
    tree.Stm stm;
    Nx(tree.Stm s) {stm=s;}

    tree.Exp unEx() {throw new Error("unEx applied to Nx");}

    tree.Stm unNx() {return stm;}

    tree.Stm unCx(temp.Label t, temp.Label f) {
      throw new Error("unCx applied to Nx");
    }
  }

  abstract class Cx extends Exp {  		// page 142
    tree.Exp unEx() {
      temp.Temp r = new temp.Temp();
      temp.Label t = new temp.Label();
      temp.Label f = new temp.Label();

      return new tree.ESEQ(
	new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(1)),
          new tree.SEQ(this.unCx(t,f),
            new tree.SEQ(new tree.LABEL(f),
              new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(0)),
                           new tree.LABEL(t))))),
        new tree.TEMP(r));
    }

    abstract tree.Stm unCx(temp.Label t, temp.Label f);

    tree.Stm unNx() {
      // ...?...
      temp.Label t = new temp.Label();
      temp.Label f = new temp.Label();
      return this.unCx(t, f);
    }
  }

  class RelCx extends Cx { 			// page 149
    int relop;
    tree.Exp left;
    tree.Exp right;
    RelCx(int rel, tree.Exp l, tree.Exp r) {relop=rel; left=l; right=r;}

    tree.Stm unCx(temp.Label t, temp.Label f) {
      // ...?...
      return new tree.CJUMP(relop, left, right, t, f);
    }
  }

  class IfThenElseExp extends Exp {     	// page 150
    Exp cond, a, b;
    temp.Label t = new temp.Label();
    temp.Label f = new temp.Label();
    temp.Label join = new temp.Label();
    IfThenElseExp(Exp cc, Exp aa, Exp bb) {cond=cc; a=aa; b=bb;}

    tree.Exp unEx() {
      // ...?...
      temp.Temp r = new temp.Temp();

      return new tree.ESEQ(new tree.SEQ( cond.unCx(t,f),
                                         new tree.SEQ(
                                             new tree.LABEL(t),
                                             new tree.SEQ(
                                                 new tree.MOVE(
                                                     new tree.TEMP(r),
                                                     a.unEx()),
                                                     new tree.SEQ(
                                                         new tree.JUMP(join),
                                                         new tree.SEQ(
                                                             new tree.LABEL(f),
                                                             new tree.SEQ(
                                                                 new tree.MOVE(
                                                                     new
                                                                     tree.TEMP(r),
                                                                     b.unEx()),
                                                                 new
                                                                 tree.LABEL(join))))))),
              new tree.TEMP(r));
    }

    tree.Stm unNx() {
      // ...?...
      return new tree.SEQ(cond.unCx(t,f),
              new tree.SEQ(
                  new tree.LABEL(t),
                  new tree.SEQ(
                      a.unNx(),
                      new tree.SEQ(
                          new tree.JUMP(join),
                          new tree.SEQ(
                              new tree.LABEL(f),
                              new tree.SEQ(
                                  b.unNx(),
                                  new tree.LABEL(join)))))));
    }

    tree.Stm unCx(temp.Label tt, temp.Label ff) {
      // ...?...
      return new tree.SEQ(
              cond.unCx(t,f),
              new tree.SEQ(
                  new tree.LABEL(t),
                  new tree.SEQ(
                      a.unCx(tt, ff),
                      new tree.SEQ(new tree.LABEL(f), b.unCx(tt, ff)))));
    }
  }

}
