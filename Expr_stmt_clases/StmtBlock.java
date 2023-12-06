package Expr_stmt_clases;
import test.*;
import java.util.List;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    public StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }
}
