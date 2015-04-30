// Skeleton file with hints about transient registers.

package sparc;

public class Codegen {

  SparcFrame frame;

  public Codegen(SparcFrame f) {frame = f;}

  // ilist holds the list of instructions generated so far.
  private assem.InstrList ilist = null, last = null;

  private void emit(assem.Instr inst) {
    // Add inst to the end of ilist.
    if (last != null)
      last = last.tail = new assem.InstrList(inst, null);
    else
      last = ilist = new assem.InstrList(inst, null);
  }

  // Two handy abbreviations:
  private void eOPER(String format, temp.TempList dst, temp.TempList src,
                            temp.LabelList jmp) {
    emit(new assem.OPER(format, dst, src, jmp));
  }

  private void eOPER(String format, temp.TempList dst, temp.TempList src) {
    emit(new assem.OPER(format, dst, src));
  }

  // It's handy to have an abbreviation for creating TempLists. (See p. 194.)
  static temp.TempList L(temp.Temp h, temp.TempList t) {
    return new temp.TempList(h, t);
  }

  // Since Sparc instructions are often limited to 13-bit signed constants,
  // it's useful to be able to check easily for this case.
  static boolean is13bitCONST(tree.Exp e) {
    if (e instanceof tree.CONST) {
      int val = ((tree.CONST) e).value;
      return (-4096 <= val && val < 4096);
    }
    else
      return false;
  }

  // Here we reserve three fixed "transient" registers to reuse:
  private temp.Temp transient1 = frame.g1;
  static private temp.Temp transient2 = new temp.Temp();
  static private temp.Temp transient3 = new temp.Temp();

  void munchStm(tree.Stm s) {
    if (s instanceof tree.MOVE) munchStm((tree.MOVE) s);
    else if (s instanceof tree.EXPR) munchStm((tree.EXPR) s);
    else if (s instanceof tree.JUMP) munchStm((tree.JUMP) s);
    else if (s instanceof tree.CJUMP) munchStm((tree.CJUMP) s);
    else if (s instanceof tree.LABEL) munchStm((tree.LABEL) s);
    // Since we've canonicalized, tree.SEQ should not be a possibility.
    else throw new Error("munchStm dispatch");
  }

  void munchStm(tree.MOVE s) {
    if (s.dst instanceof tree.TEMP)
      munchExp((tree.Exp) s.src, ((tree.TEMP) s.dst).temp);  // ???
    else if (s.dst instanceof tree.MEM) {
      tree.Exp e = ((tree.MEM) s.dst).exp;

      if (e instanceof tree.BINOP) {
        tree.BINOP b = (tree.BINOP) e;

        if (b.left instanceof tree.CONST) {
          if (b.binop == tree.BINOP.PLUS) {
            eOPER("\tst\t`s1, [`s0 + " + ((tree.CONST) b.left).value + "]\n", null,
                    L(munchExp(b.right), L(munchExp(s.src), null)));
          }
        }
        else if (b.right instanceof tree.CONST) {
          if (b.binop == tree.BINOP.PLUS) {
            eOPER("\tst\t`s1, [`s0 + " + ((tree.CONST) b.right).value + "]\n", null,
                    L(munchExp(b.left), L(munchExp(s.src), null)));
          }
        }
      }
      else if (e instanceof tree.CONST) {
        eOPER("\tst\t`s0, [`d0 + " + ((tree.CONST) e).value + "]", L(
                                 transient1, null), L(munchExp(s.src), null));
      }
      else if (e instanceof tree.MEM) {
        eOPER("\tld [`s0], `d0\n\tst `d0, [`s1]\n", null, 
                                    L(munchExp(((tree.MEM) s.src).exp), L(munchExp(e), null)));
      }
      // MOVE(MEM(e), s.src)
      //eOPER("\tst\t`s1, [`s0]\n", null,
	  //  L(munchExp(e, transient1), L(munchExp(s.src, transient2), null)));
      eOPER("\tst\t`s1, [`s0]\n", null, L(munchExp(e), 
                            L(munchExp(s.src), null)));
    }
    else
      throw new Error("Bad MOVE destination.");
  }

  void munchStm(tree.EXPR s) {
    munchExp(s.exp, null);
  }
  
  void munchStm(tree.JUMP s) {
    eOPER("\tba\t" + s.targets.head.toString() + "\n\tnop\n", null, null);		
  }
  
  void munchStm(tree.LABEL s) {
    emit(new assem.LABEL(s.label.toString() + ":\n", s.label));		
  }
  
  void munchStm(tree.CJUMP s) {
    if (s.left instanceof tree.CONST) {
      eOPER("\tcmp\t`s0, " + ((tree.CONST) s.left).value+ "\n", null,
	  L(munchExp(s.right), null));
    } 
    else if (s.right instanceof tree.CONST){
      eOPER("\tcmp\t`s0, " + ((tree.CONST) s.right).value+ "\n", null,
          L(munchExp(s.left), null));
    }
    else{
      eOPER("\tcmp\t`s0, `s1\n", null, L(munchExp(s.left), L(munchExp(s.right), null)));
    }
    switch(s.relop)                 {                 
      case tree.CJUMP.EQ:                         
	eOPER("\tbe\t" + s.iftrue.toString() + "\n\tnop\n", null, null);                       
        break;                 
      case tree.CJUMP.NE:                         
        eOPER("\tbne\t" + s.iftrue.toString() + "\n\tnop\n", null, null);                        
        break;                 
      case tree.CJUMP.LT:  
	eOPER("\tbl\t" + s.iftrue.toString() + "\n\tnop\n", null, null);                     
        break;
      default:
        throw new Error("Invalid operator for MINIJAVA");
    }
  }
  
  // Here is munchExp as specified by Appel on p. 193.
  temp.Temp munchExp(tree.Exp e) {
    return munchExp(e, null);
  }

  // I give munchExp an extra parameter r that can specify a Temp in which
  // the result can safely be put. If r is null, then munchExp must come up
  // with a suitable Temp on its own (usually by generating a fresh one).
  temp.Temp munchExp(tree.Exp e, temp.Temp r) {
    if (e instanceof tree.CONST) return munchExp((tree.CONST) e, r);
    if (e instanceof tree.NAME) return munchExp((tree.NAME) e, r);
    if (e instanceof tree.TEMP) return munchExp((tree.TEMP) e, r);
    if (e instanceof tree.BINOP) return munchExp((tree.BINOP) e, r);
    if (e instanceof tree.MEM) return munchExp((tree.MEM) e, r);
    if (e instanceof tree.CALL) return munchExp((tree.CALL) e, r);
    // Since we've canonicalized, tree.ESEQ should not be a possibility.
    else throw new Error("munchExp dispatch");
  }

  assem.InstrList codegen(tree.Stm s) {
    assem.InstrList l;

    munchStm(s);
    l = ilist;
    ilist = last = null;
    return l;
  }
}

