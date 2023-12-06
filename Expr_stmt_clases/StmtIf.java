package Expr_stmt_clases;
import test.*;

public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
