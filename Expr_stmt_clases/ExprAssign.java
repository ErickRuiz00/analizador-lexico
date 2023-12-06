package Expr_stmt_clases;
import test.*;

public class ExprAssign extends Expression{
    final Token name;
    final Expression value;

    public ExprAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
