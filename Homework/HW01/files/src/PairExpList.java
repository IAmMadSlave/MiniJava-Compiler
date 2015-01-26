class PairExpList extends ExpList {
    final Exp     exp;
    final ExpList expList;

    PairExpList(Exp exp, ExpList expList) {
        this.exp     = exp;
        this.expList = expList;
    }
}
