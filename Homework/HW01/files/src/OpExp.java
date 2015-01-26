class OpExp extends Exp {
    final Exp exp1,
              exp2;
    final int oper;

    static final int Plus = 1, Minus = 2, Times = 3, Div = 4;

    OpExp(Exp exp1, int oper, Exp exp2) {
        this.exp1 = exp1;
        this.oper = oper;
        this.exp2 = exp2;
    }
}
