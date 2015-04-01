package semant;

import syntaxtree.*;

public class BuildSymbolTableVisitor extends visitor.DepthFirstVisitor {
    // By extending DepthFirstVisitor, we only have to override those
    // methods that differ from the generic visitor.

    private errormsg.ErrorMsg errorMsg; 
    private SymbolTable classTable;
    private ClassInfo  currClass;
    private MethodInfo currMethod; 

    public BuildSymbolTableVisitor(errormsg.ErrorMsg e) {
        errorMsg   = e; 
        classTable = new SymbolTable();
        currClass  = null;
        currMethod = null; 
    }  

    public SymbolTable getSymbolTable() {
        return classTable; 
    }

    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {    
        String id = n.i1.toString();
        classTable.addClass(id, new ClassInfo(id));
        // No fields or methods in the Main class.
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        String id = n.i.toString();

        if (currMethod == null) {
            if (!currClass.addField(id, new VariableInfo(n.t)))
                errorMsg.error(n.pos, id + " is already defined in " + 
                        currClass.getName());
        } else if (!currMethod.addVar(id, new VariableInfo(n.t)))
            errorMsg.error(n.pos, id + " is already defined in " + 
                    currClass.getName() + "." + currMethod.getName() +
                    "(...)");
    }

    // Identifier i;
    // VarDeclStar vds;
    // MethodDeclStar mds;
    public void visit(ClassDeclSimple n) {
        String id = n.i.toString();

        if (currClass == null) {
            currClass = new ClassInfo(id);
            if (classTable.addClass(id, currClass)) {
                for (int i = 0; i < n.vl.size(); i++) {
                    n.vl.elementAt(i).accept(this);
                }
                for (int i = 0; i < n.ml.size(); i++) {
                    n.ml.elementAt(i).accept(this);
                }
            }
            else {
                n.duplicate = true;
                errorMsg.error(n.pos, " duplicate class: " + id);
            }
            currClass = null;
        }
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclStar vds;
    // StatementList sL;
    // Exp e;
    public void visit(MethodDecl n) {
        String id = n.i.toString();

        if (currMethod == null) {
            currMethod = new MethodInfo(id, n.t);
            if(classTable.addMethod(id, currMethod)) {
                for (int i = 0; i < n.fl.size(); i++) {
                    n.fl.elementAt(i).accept(this);
                }
                for (int i = 0; i < n.vl.size(); i++) {
                    n.vl.elementAt(i).accept(this);
                }
                for (int i = 0; i < n.sl.size(); i++) {
                    n.sl.elementAt(i).accept(this);
                }
            }
            else {
                n.duplicate = true;
                errorMsg.error(n.pos, id + " is already defined in " + currClass.getName());
            }
            currMethod = null;
        }
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        String id = n.i.toString();

        if (!currMethod.addFormal(id, new VariableInfo(n.t))) {
            errorMsg.error(n.pos, id + " is already defined in " + 
                    currClass.getName() + "." + currMethod.getName() +
                    "." + currMethod.getFormalTypes())
    }
}
