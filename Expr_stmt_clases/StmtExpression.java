package Expr_stmt_clases;
import test.*;
public class StmtExpression extends Statement {
    final Expression expression;

    public StmtExpression(Expression expression) {
        this.expression = expression;
    }
}
