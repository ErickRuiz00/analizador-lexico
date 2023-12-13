package test;

import Expr_stmt_clases.*;
import java.util.*;


public interface Parser {
    boolean parse();
    List<Statement> getStatementTree();
}
