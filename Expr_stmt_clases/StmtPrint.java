package Expr_stmt_clases;
import test.*;
public class StmtPrint extends Statement {
    final Expression expression;

    public StmtPrint(Expression expression) {
        this.expression = expression;
    }
}
