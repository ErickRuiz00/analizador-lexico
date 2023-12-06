package Expr_stmt_clases;
import test.*;

public class ExprVariable extends Expression {
    final Token name;

    public ExprVariable(Token name) {
        this.name = name;
    }
}