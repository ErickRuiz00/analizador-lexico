package Expr_stmt_clases;
import test.*;

public class ExprBinary extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    public ExprBinary(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

}
