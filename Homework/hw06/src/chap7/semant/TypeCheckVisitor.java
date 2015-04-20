package semant;

import syntaxtree.*;
import java.util.Vector;

public class TypeCheckVisitor extends visitor.TypeDepthFirstVisitor {
    // By extending TypeDepthFirstVisitor, we only have to override those
    // methods that differ from the generic visitor.

    private errormsg.ErrorMsg errorMsg;
    private SymbolTable classTable;
    private ClassInfo currClass;
    private MethodInfo currMethod;
    private String mainMethod;

    // Type constants
    final IntegerType INTTY = new IntegerType();
    final IntArrayType INTARRTY = new IntArrayType();
    final BooleanType BOOLTY = new BooleanType();

    public TypeCheckVisitor(errormsg.ErrorMsg e, SymbolTable s){
        errorMsg = e;
        classTable = s;
        currClass = null;
        currMethod = null;
        mainMethod = null;
    }

    // Identifier i1,i2;
    // Statement s;
    public Type visit(MainClass n) {
        // Mostly you just need to typecheck the body of 'main' here.
        // But as shown in Foo.java, you need care concerning 'this'.
        String id = n.i1.toString();
        currClass = classTable.get(id);

        mainMethod = id;

        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);

        mainMethod = null;
        return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclSimple n) {
        String id = n.i.toString();

        if (!n.duplicate) {
            currClass = classTable.get(id);
            if (currClass == null) {
                errorMsg.error(n.pos, "cannot find symbol " + id);
            }
            else {
                for (int i = 0; i < n.vl.size(); i++) {
                    n.vl.elementAt(i).accept(this);
                }
                for (int i = 0; i < n.ml.size(); i++) {
                    n.ml.elementAt(i).accept(this);
                }
            }
        }
        currMethod = null;

        return null;
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public Type visit(MethodDecl n) {
        String id = n.i.toString();

        if (!n.duplicate) {
            currMethod = currClass.getMethod(id);

            n.t.accept(this);
            n.i.accept(this);

            if (currMethod == null) {
                errorMsg.error(n.pos, "cannot find symbol " + id);
            }
            for (int i = 0; i < n.fl.size(); i++) {
                n.fl.elementAt(i).accept(this);
            }
            for (int i = 0; i < n.vl.size(); i++) {
                n.vl.elementAt(i).accept(this);
            }
            for (int i = 0; i < n.sl.size(); i++) {
                n.sl.elementAt(i).accept(this);
            }

            Type t1 = n.e.accept(this);
            if(!equal(t1, n.t, t1)) {
                errorMsg.error(n.e.pos, eIncompTypes(t1.toString(), n.t.toString()));
            }
        }
        return null;
    }

    // Exp e1,e2;
    public Type visit(Plus n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!equal(t1, t2, INTTY))
            errorMsg.error(n.pos, eIncompBiop("+", t1.toString(), t2.toString()));
        return INTTY;
    }

    // Exp e1,e2;
    public Type visit(Minus n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!equal(t1, t2, INTTY))
            errorMsg.error(n.pos, eIncompBiop("-", t1.toString(), t2.toString()));
        return INTTY;
    }

    // Exp e1,e2;
    public Type visit(Times n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!equal(t1, t2, INTTY))
            errorMsg.error(n.pos, eIncompBiop("*", t1.toString(), t2.toString()));
        return INTTY;
    }

    // Exp e1,e2;
    public Type visit(And n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!equal(t1, t2, BOOLTY))
            errorMsg.error(n.pos, eIncompBiop("&&", t1.toString(), t2.toString()));
        return BOOLTY;
    }

    // Exp e1,e2;
    public Type visit(LessThan n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!equal(t1, t2, INTTY))
            errorMsg.error(n.pos, eIncompBiop("<", t1.toString(), t2.toString()));
        return BOOLTY;
    }

    // Exp e1;
    public Type visit(Not n) {
        Type t1 = n.e.accept(this);
        if (!(t1 instanceof BooleanType))
            errorMsg.error(n.pos, "operator ! cannot be applied to " + t1.toString()); 
        return BOOLTY;
    }

    // Exp e;
    // Statement s1,s2;
    public Type visit(If n) {
        Type t1 = n.e.accept(this);

        if (!(t1 instanceof BooleanType)) {
            errorMsg.error(n.pos, eIncompTypes(t1.toString(), BOOLTY.toString()));
        }
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }
    
    // Exp e;
    // Statement s;
    public Type visit(While n) {
        Type t1 = n.e.accept(this);

        if (!(t1 instanceof BooleanType)) {
            errorMsg.error(n.pos, eIncompTypes(t1.toString(), BOOLTY.toString()));
        }
        n.s.accept(this);
        return null;
    }

    // Identifier i;
    // Exp e;
    public Type visit(Assign n) {
        Type t1 = n.i.accept(this);
        Type t2 = n.e.accept(this);

        if (t1 == null){
            errorMsg.error(n.pos, "cannot resolve symbol: " + n.i.toString() + " in " + currClass.getName());
        }
        if (!equal(t1, t2, t1)) {
            errorMsg.error(n.e.pos, eIncompTypes(t2.toString(), t1.toString()));
        }
        return null;
    }

    // Identifier i;
    // Exp e1, e2;
    public Type visit(ArrayAssign n) {
        Type t1 = n.i.accept(this);
        Type t2 = n.e1.accept(this);
        Type t3 = n.e2.accept(this);
        
        if (t1 == null || !(t1 instanceof IntArrayType)) {
            if (t1 == null) {
                errorMsg.error(n.pos, " undeclared identifier '" + n.i.toString() + "'");
            }
            else {
                errorMsg.error(n.pos, " array required, but " + t1.toString() + " found");
            }
        }
        if (t2 == null || !(t2 instanceof IntegerType)) {
            if (t2 == null) {
                errorMsg.error(n.e1.pos, eIncompTypes("null", INTTY.toString()));
            }
            else {
                errorMsg.error(n.e1.pos, eIncompTypes(t2.toString(), INTTY.toString()));
            }
        }
        if (t3 == null || !(t3 instanceof IntegerType)) {
            if (t3 == null) {
                errorMsg.error(n.e2.pos, eIncompTypes("null", INTTY.toString()));
            }
            else {
                errorMsg.error(n.e2.pos, eIncompTypes(t3.toString(), INTTY.toString()));
            }
        }

        return null;
    }

    // Exp e1, e2;
    public Type visit(ArrayLookup n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);

        if(!(t1 instanceof IntArrayType)){
            errorMsg.error(n.e1.pos, eIncompTypes(t1.toString(), INTARRTY.toString()));
        }
        if (!(t2 instanceof IntegerType)) {
            errorMsg.error(n.e2.pos, eIncompTypes(t2.toString(), INTTY.toString()));
        }
        return INTTY;
    }        

    // Exp e;
    public Type visit(ArrayLength n) {
        Type t1 = n.e.accept(this);
        
        if (!(t1 instanceof IntArrayType)) {
            errorMsg.error(n.pos, " array required, but " + t1.toString() + " found");
        }
        return INTTY;
    }

    //True
    public Type visit(True n) {
        return BOOLTY;
    }

    //False
    public Type visit(False n) {
        return BOOLTY;
    }

    // int
    public Type visit(IntegerLiteral n) {
        return INTTY;
    }

    // String
    public Type visit(IdentifierType n) {
        String id = n.toString();
        if (classTable.get(id) == null) {
            errorMsg.error(n.pos, " cannot find symbol " + id); 
        }
        return n;
    }

    //IntegerType
    public Type visit(IntegerType n) {
        return INTTY;        
    }

    //BooleanType
    public Type visit(BooleanType n) {
        return BOOLTY;
    }

    //IntArrayType
    public Type visit(IntArrayType n) {
        return INTARRTY;
    }

    // String
    public Type visit(Identifier n) {
        String id = n.toString();

        VariableInfo v1 = null;
        if (currMethod != null) {
            v1 = currMethod.getVar(id);
            if (v1 != null) {
                return v1.type;
            }
        }

        v1 = currClass.getField(id);
        if (v1 != null) {
            return v1.type;
        }


        return null;            
    }

    // IdentiferExp
    public Type visit(IdentifierExp n) {
        String id = n.s.toString();

        VariableInfo v1 = null;
        if (currMethod != null) {
            v1 = currMethod.getVar(id);
            if (v1 != null) {
                return v1.type;
            }
        }

        v1 = currClass.getField(id);
        if (v1 != null) {
            return v1.type;
        }

        errorMsg.error(n.pos, "cannot resolve symbol: " + id + " in " + currClass.getName());
        return null;
    }

    // This
    public Type visit(This n) {
        if (mainMethod != null && currClass == classTable.get(mainMethod)) {
            errorMsg.error(n.pos, "non-static variable cannot be referenced from a static context");
        }
        if (currClass == null) {
            errorMsg.error(n.pos, " illegal use of this");
        }
        return new IdentifierType(currClass.getName());
    }

    // Exp e;
    public Type visit(NewArray n) {
        Type t1 = n.e.accept(this);

        if (!(t1 instanceof IntegerType)) {
            errorMsg.error(n.e.pos, eIncompTypes(t1.toString(), INTTY.toString()));
        }
        return INTARRTY;
    }

    // Identifier i;
    public Type visit(NewObject n) {
        String id = n.i.toString();
        if (classTable.get(id) == null) {
            errorMsg.error(n.pos, " unknown class type " + id);
        }
        return new IdentifierType(id);
    }

    // Exp e;
    // Identifier i;
    // ExpList e1;
    public Type visit(Call n) {
        String id = n.i.toString();

        MethodInfo mi = null;

        Type t1 = n.e.accept(this);

        if (t1 == null) {
            return null;
        }

        if (!(t1 instanceof IdentifierType)) {
            errorMsg.error(n.e.pos, t1.toString() + " cannot be called");
        }
        else {
            ClassInfo ci = classTable.get(t1.toString());
            if (ci != null) {
                n.fullname = ci.getName() + "$" + id;

                String temp = "(";
                for (int i = 0; i < n.el.size(); i++) {
                    Type t2 = n.el.elementAt(i).accept(this);
                    temp += t2.toString() + ", ";
                }
                if (temp.length() > 2) {
                    temp = temp.substring(0, temp.length()-2);
                }
                temp += ")";

                mi = ci.getMethod(id);
                if (mi == null) {
                    errorMsg.error(n.pos, "cannot resolve symbol: " + id + temp + "location: " + ci.getName());
                }
                else {
                    if (!(temp.equals(mi.getFormalsTypes()))) {
                        errorMsg.error(n.e.pos, currClass.getName() + "." + id + " cannot be applied to " + id + temp);
                    }
                }
            }
        }

        if (mi == null) {
            return null;
        } 

        return mi.getReturnType();
    }

    // Check whether t1 == t2 == target, but suppress error messages if
    // either t1 or t2 is null.
    private boolean equal(Type t1, Type t2, Type target) {
        if ( t1 == null || t2 == null )
            return true;

        if (target == null)
            throw new Error("target argument in method equal cannot be null");

        if (target instanceof IdentifierType && t1 instanceof IdentifierType
                && t2 instanceof IdentifierType)
            return ((IdentifierType) t1).s.equals(((IdentifierType) t2).s );

        if (!(target instanceof IdentifierType) &&
                t1.toString().equals(target.toString()) &&
                t2.toString().equals(target.toString()))
            return true;

        return false;
    }

    // Methods for error reporting:

    private String eIncompTypes(String t1, String t2) {
        return "incompatible types \nfound   : " + t1
            + "\nrequired: " + t2 ;
    }

    private String eIncompBiop(String op, String t1, String t2) {
        return "operator " + op + " cannot be applied to " + t1 + "," + t2 ;
    }

}
