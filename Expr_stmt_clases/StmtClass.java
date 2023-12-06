package Expr_stmt_clases;
import test.*;
import java.util.List;

public class StmtClass extends Statement {
    final Token name;
    final ExprVariable superclass;
    final List<StmtFunction> methods;

    public StmtClass(Token name, ExprVariable superclass, List<StmtFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }
}
