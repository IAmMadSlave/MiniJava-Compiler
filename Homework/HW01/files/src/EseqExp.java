class EseqExp extends Exp {
    final Stm stm;
    final Exp exp;

    EseqExp(Stm stm, Exp exp) {
        this.stm = stm;
        this.exp = exp;
    }
}
