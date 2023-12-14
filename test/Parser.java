package test;

import Expr_stmt_clases.*;

import java.text.ParseException;
import java.util.*;


public interface Parser {
    boolean parse() throws ParserException;
    List<Statement> getStatementTree();
}
