package Expr_stmt_clases;
import test.*;
public class StmtReturn extends Statement {
    final Expression value;

    StmtReturn(Expression value) {
        this.value = value;
    }
}
