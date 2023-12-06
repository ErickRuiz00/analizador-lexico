package Expr_stmt_clases;
import test.*;

public class ExprGet extends Expression{
    final Expression object;
    final Token name;

    ExprGet(Expression object, Token name) {
        this.object = object;
        this.name = name;
    }
}
