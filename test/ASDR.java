package test;

import java.util.*;
import Expr_stmt_clases.*;

public class ASDR implements Parser{
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;
    private List<Statement> statementTree;

    public List<Statement> getStatementTree() {
        return statementTree;
    }
    
    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }
    
    @Override
    public boolean parse() {
        statementTree = PROGRAM();
        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Linea correcta");
            return true;
        }else
            System.out.println("Linea incorrecta");
        
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PROGRAM -> DECLARATION 
    private List<Statement> PROGRAM(){
        List<Statement> statements = new ArrayList();
        DECLARATION(statements);
        return statements;
        
    }
    
    /* DECLARATION -> FUN_DECL DECLARATION
                      VAR_DECL DECLARATION
                      STATEMENT DECLARATION
                      EPSILON               */
    // Declaraciones -----------------------------------------------------------------------------------------
    private void DECLARATION(List<Statement> statements){
        if(hayErrores) 
            return;
        switch (preanalisis.tipo) {
            case FUN -> { 
                Statement stmt = FUN_DECL();
                statements.add(stmt);
                DECLARATION(statements);
            }
            case VAR -> { 
                Statement stmt = VAR_DECL();
                statements.add(stmt);
                DECLARATION(statements);
            }
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN, FOR, IF, PRINT, RETURN, WHILE, LEFT_BRACE -> {   
                Statement stmt = STATEMENT();
                statements.add(stmt);
                DECLARATION(statements);
            }
        }
    }
    
    // FUN_DECL -> fun FUNCTION
    private Statement FUN_DECL(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            return FUNCTION();
        }else{
            hayErrores = true;
            System.out.println("Se esperaba 'fun'");
            return null;
        }
    }
    
    // VAR_DECL -> var id VAR_INIT ;
    private Statement VAR_DECL(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.VAR){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            Expression initializer = VAR_INIT();
            match(TipoToken.SEMICOLON);
            Statement stmnt = new StmtVar(name,initializer);
            return stmnt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba 'var'");
            return null;
        }
    }
    
    // VAR_INIT -> = EXPRESSION 
    //          -> ε
    private Expression VAR_INIT(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Expression expr = EXPRESSION();
            return expr;
        }
        return null;
    }
    
    /* STATEMENT -> EXPR_STMT
                 -> FOR_STMT
                 -> IF_STMT
                 -> PRINT_STMT
                 -> RETURN_STMT
                 -> WHILE_STMT
                 -> BLOCK
    */
    // ------------------------------------------------------------------------------------------------
    // Sentencias -------------------------------------------------------------------------------------
    private Statement STATEMENT(){
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                return EXPR_STMT();
            }
            case FOR ->{
                return FOR_STMT();
            }
            case IF ->{ 
                return IF_STMT();
            }
            case PRINT ->{
                return PRINT_STMT();
            }
            case RETURN ->{
                return RETURN_STMT();
            }
            case WHILE ->{
                return WHILE_STMT();
            }
            case LEFT_BRACE ->{
                return BLOCK();
            }
            default -> {
                    hayErrores = true;
                    System.out.println("Se esperaba un statement");
                    return null;
            }
        }
    }

    // EXPR_STMT -> EXPRESSION;
    private Statement EXPR_STMT(){
        if(hayErrores)
            return null;   
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN -> {  
                Expression expr = EXPRESSION();
                match(TipoToken.SEMICOLON);
                Statement stexpr = new StmtExpression(expr);
                return stexpr;
            }
            default -> {
                hayErrores = true;    
                System.out.println("Se esperaba una expresión");
                return null;
            }
        }
    }
    
    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private Statement FOR_STMT(){
        if(hayErrores)
            return null;      
        if(preanalisis.tipo == TipoToken.FOR){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            Statement initializer = FOR_STMT_1();
            Expression condition = FOR_STMT_2();
            Expression increment = FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            Statement body = STATEMENT();
            if(increment != null){
                body = new StmtBlock(Arrays.asList(body,new StmtExpression(increment)));
            }
            if(condition == null){
                condition = new ExprLiteral(true);
            }
            body = new StmtLoop(condition, body);
            if(initializer != null){
                body = new StmtBlock(Arrays.asList(initializer,body));

            }
            return body;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba 'for'");
            return null;
        }
    } 
    /*
    FOR_STMT_1 -> VAR_DECL
               -> EXPR_STMT
               -> ;
    */
    private Statement FOR_STMT_1(){
        if(hayErrores)
            return null;        
        switch (preanalisis.tipo) {
            case VAR ->{
                Statement stmnt = VAR_DECL();
                return stmnt;
            }
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Statement stmnt = EXPR_STMT();
                return stmnt;
            }
            case SEMICOLON ->{ 
                match(TipoToken.SEMICOLON);
                return null;
            }
            default -> {
                hayErrores = true;
                System.out.println("Se esperaba 'var', una expresion o ';'");
                return null;
            }
        }
    }
    
    /*
    FOR_STMT_2 -> EXPRESSION;
               -> ;
    */
    private Expression FOR_STMT_2(){
        if(hayErrores)
            return null;     
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN -> {
                Expression expr = EXPRESSION();
                match(TipoToken.SEMICOLON);
                return expr;
            }
            case SEMICOLON ->{ 
                match(TipoToken.SEMICOLON);
                return null;
            }
            default -> {
                hayErrores = true;
                System.out.println("Se esperaba una expresión o ';'");
                return null;
            }
        }
    }
    
    /*
    FOR_STMT_3 -> EXPRESSION
               -> Ɛ         */
    private Expression FOR_STMT_3(){
        if(hayErrores)
            return null;    
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = EXPRESSION();
                return expr;
            }
        }
        return null;
    }
    
    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private Statement IF_STMT(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement thenBranch = STATEMENT();
            Statement elseBranch = ELSE_STATEMENT();
            Statement ifstmt = new StmtIf(condition, thenBranch, elseBranch);
            return ifstmt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba if");
            return null;
        }
    }
    /*
    ELSE_STATEMENT -> else STATEMENT
                   -> Ɛ         */
    private Statement ELSE_STATEMENT(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            Statement stmt = STATEMENT();
            return stmt;
        }
        return null;
    }
    // PRINT_STMT -> print EXPRESSION ;
    private Statement PRINT_STMT(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            Expression expr = EXPRESSION();
            Statement pstmt = new StmtPrint(expr);
            match(TipoToken.SEMICOLON);
            return pstmt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba print");
            return null;
        }
        
    }
    //RETURN_STMT -> return RETURN_EXP_OPC ;
    private Statement RETURN_STMT(){
         if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.RETURN){
            match(TipoToken.RETURN);
            Expression expr = RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
            Statement rstmt = new StmtReturn(expr);
            return rstmt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba return");
            return null;
        }
    }
    /*
    RETURN_EXP_OPC -> EXPRESSION
                   -> Ɛ         */
    private Expression RETURN_EXP_OPC(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
             case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = EXPRESSION();
                return expr;
             }
        }
        return null;
    }
    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    private Statement WHILE_STMT(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.WHILE){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement body = STATEMENT();
            Statement lstmt = new StmtLoop(condition, body);
            return lstmt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba un while");
            return null;
        }
    }
    // BLOCK -> { DECLARATION }
    private Statement BLOCK(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            match(TipoToken.LEFT_BRACE);
            List<Statement> statements = new ArrayList<>();      
            DECLARATION(statements);
            match(TipoToken.RIGHT_BRACE);
            Statement bstmt = new StmtBlock(statements);
            return bstmt;
        }else{
            hayErrores = true;
            System.out.println("Se esperaba un {");
            return null;
        }
    }
    
    
    // -------------------------------------------------------------------------------------------
    // Expresiones -------------------------------------------------------------------------------
    //EXPRESSION -> ASSIGNMENT
    private Expression EXPRESSION(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = ASSIGNMENT();
                return expr;
            }
            default -> {
                hayErrores = true;
                System.out.println("Error en EXPRESSION");
                return null;
            }
        }
    }
    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    private Expression ASSIGNMENT(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = LOGIC_OR();
                expr = ASSIGNMENT_OPC(expr);
                return expr;
            }
            default -> {
                    hayErrores = true;
                    System.out.println("Error en ASSIGMENT");
                    return null;
            }
        }
    }
    //Chechar esta clase -------------------------------------------------------------
    /*
    ASSIGNMENT_OPC -> = EXPRESSION
                   -> Ɛ             */
    private Expression ASSIGNMENT_OPC(Expression expr){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Token name = previous();
            Expression value = EXPRESSION();
            ExprAssign expra = new ExprAssign(name, value);
            return expra;
        }
        return expr;
    }
    // ------------------------------------------------------------------------------
    //LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    private Expression LOGIC_OR(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = LOGIC_AND();
                expr = LOGIC_OR_2(expr);
                return expr;
            }
            default -> {
                hayErrores = true;
                System.out.println("Error en LOGIC_OR");
                return null;
            }
        }
    }
    /*
    LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
               -> Ɛ         */
    private Expression LOGIC_OR_2(Expression expr){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            Token operator = previous();
            Expression expr2 = LOGIC_AND();
            ExprLogical expl = new ExprLogical(expr, operator, expr2);
            return LOGIC_OR_2(expl);
        }
        return expr;
    }
    //LOGIC_AND -> EQUALITY LOGIC_AND_2
    private Expression LOGIC_AND(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = EQUALITY();
                expr = LOGIC_AND_2(expr);
                return expr;
            }
            default -> {
                    hayErrores = true;
                    System.out.println("Error en LOGIC_AND");
                    return null;
            }
        }
    }
    /*
    LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
                -> Ɛ            */
    private Expression LOGIC_AND_2(Expression expr){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.AND){
            match(TipoToken.AND);
            Token operador = previous();
            Expression expr2 = EQUALITY();
            ExprLogical expl = new ExprLogical(expr, operador, expr2);
            return LOGIC_AND_2(expl);
        }
        return expr;
    }
    //EQUALITY -> COMPARISON EQUALITY_2
    private Expression EQUALITY(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = COMPARISON();
                expr = EQUALITY_2(expr);
                return expr;
            }
            default -> {
                    hayErrores = true;
                    System.out.println("Error en logic and");
                    return null;
            }
        }
    }
    /*
    EQUALITY_2 -> != COMPARISON EQUALITY_2
               -> == COMPARISON EQUALITY_2
               -> Ɛ         */
    private Expression EQUALITY_2(Expression expr){
        if(hayErrores)
            return null;
        switch(preanalisis.tipo){
            case BANG_EQUAL ->{
                match(TipoToken.BANG_EQUAL);
                Token operator = previous();
                Expression expr2 = COMPARISON();
                ExprBinary expb = new ExprBinary(expr, operator, expr2);
                return EQUALITY_2(expb);
            }
            case EQUAL_EQUAL->{
                match(TipoToken.EQUAL_EQUAL);
                Token operator = previous();
                Expression expr2 = COMPARISON();
                ExprBinary expb = new ExprBinary(expr, operator, expr2);
                return EQUALITY_2(expb);
            }
        }
        return expr;
    }
    //COMPARISON -> TERM COMPARISON_2
    private Expression COMPARISON(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = TERM();
                expr = COMPARISON_2(expr);
                return expr;
            }
            default -> {
                hayErrores = true;
                System.out.println("Error en logic and");
                return null;
            }
        }
    }
    /*
    COMPARISON_2 -> > TERM COMPARISON_2
                 -> >= TERM COMPARISON_2
                 -> < TERM COMPARISON_2
                 -> <= TERM COMPARISON_2
                 -> Ɛ           */
    private Expression COMPARISON_2(Expression expr){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case GREATER ->{
                match(TipoToken.GREATER);
                Token operator = previous();
                Expression expr2 = TERM();
                ExprBinary exprb = new ExprBinary(expr, operator, expr2);
                return COMPARISON_2(exprb);
            }
            case GREATER_EQUAL ->{
                match(TipoToken.GREATER_EQUAL);
                Token operator = previous();
                Expression expr2 = TERM();
                ExprBinary exprb = new ExprBinary(expr, operator, expr2);
                return COMPARISON_2(exprb);
            }
            case LESS ->{
                match(TipoToken.LESS);
                Token operator = previous();
                Expression expr2 = TERM();
                ExprBinary exprb = new ExprBinary(expr, operator, expr2);
                return COMPARISON_2(exprb);
            }
            case LESS_EQUAL ->{
                match(TipoToken.LESS_EQUAL);
                Token operator = previous();
                Expression expr2 = TERM();
                ExprBinary exprb = new ExprBinary(expr, operator, expr2);
                return COMPARISON_2(exprb);
            }
        }
        return expr;
    }
    //TERM -> FACTOR TERM_2
    private Expression TERM(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = FACTOR();
                expr = TERM_2(expr);
                return expr;
            }
            default -> {
                    hayErrores = true;
                    System.out.println("Error en TERM");
                    return null;
            }
        }
    }
    /*
    TERM_2 -> - FACTOR TERM_2
           -> + FACTOR TERM_2
           -> Ɛ             */
    private Expression TERM_2(Expression expr){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo) {
            case MINUS ->{
                match(TipoToken.MINUS);
                Token operador = previous();
                Expression expr2 = FACTOR();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return TERM_2(expb);
            }
            case PLUS ->{
                match(TipoToken.PLUS);
                Token operador = previous();
                Expression expr2 = FACTOR();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return TERM_2(expb);
            }
        }
        return expr;
    }
    //FACTOR -> UNARY FACTOR_2   
    private Expression FACTOR(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = UNARY();
                expr = FACTOR_2(expr);
                return expr;
            }
            default ->{
                hayErrores = true;
                System.out.println("Error en factor");
                return null;
            }
        }
    }
    /*
    FACTOR_2 -> / UNARY FACTOR_2
             -> * UNARY FACTOR_2
             -> Ɛ               */
    private Expression FACTOR_2(Expression expr){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case SLASH ->{
                match(TipoToken.SLASH);
                Token operador = previous();
                Expression expr2 = UNARY();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return FACTOR_2(expb);
            }
            case STAR ->{
                match(TipoToken.STAR);
                Token operador = previous();
                Expression expr2 = UNARY();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return FACTOR_2(expb);
            }
        }
        return expr;
    }
    /* UNARY -> ! UNARY
          -> - UNARY
          -> CALL           */
    private Expression UNARY(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case BANG ->{
                match(TipoToken.BANG);
                Token operator = previous();
                Expression expr = UNARY();
                return new ExprUnary(operator,expr);
            }
            case MINUS ->{
                match(TipoToken.MINUS);
                Token operator = previous();
                Expression expr = UNARY();
                return new ExprUnary(operator,expr);
            }
            case TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN->{
                return CALL();
            }
            default ->{
                hayErrores = true;
                System.out.println("Error en Unary");
                return null;
            }
        }
    }
    //CALL -> PRIMARY CALL_2
    private Expression CALL(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression exprPrimary = PRIMARY();
                return CALL_2(exprPrimary);
            }
            default ->{
                hayErrores = true;
                System.out.println("Error en call");
                return null;
            }
        }
    }
    /*
    CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
           -> Ɛ         */
    private Expression CALL_2(Expression expr){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            List<Expression> arguments = ARGUMENTS_OPC();
            match(TipoToken.RIGHT_PAREN);
            Expression exprCall = new ExprCallFunction(expr, arguments);
            return CALL_2(exprCall);
        }
        return expr;
    }
    /*
    PRIMARY -> true
            -> false
            -> null
            -> number
            -> string
            -> id
            -> ( EXPRESSION )           */
    private Expression PRIMARY(){
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case TRUE -> {
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            }
            case FALSE -> {
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            }
            case NULL -> {
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            }
            case NUMBER -> {
                match(TipoToken.NUMBER);
                Token number = previous();
                return new ExprLiteral(number.getLiteral());
            }
            case STRING -> {
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.getLiteral());
            }
            case IDENTIFIER -> {
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            }
            case LEFT_PAREN ->{
                match(TipoToken.LEFT_PAREN);
                Expression expr = EXPRESSION();
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
            }
            default ->{
                hayErrores = true;
                System.out.println("Error en PRIMARY");
                return null;
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    // OTRAS -------------------------------------------------------------------------------------
    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    private Statement FUNCTION(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            Token name = previous();
            match(TipoToken.IDENTIFIER);
            match(TipoToken.LEFT_PAREN);
            List<Token> params = PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            StmtBlock body = (StmtBlock) BLOCK();
            return new StmtFunction(name, params, body);
        }else{
            hayErrores = true;
            System.out.println("Error en FUNCTION");
            return null;
        }
    }
    /*
    PARAMETERS_OPC -> PARAMETERS
                   -> Ɛ         */
    private List<Token> PARAMETERS_OPC(){
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            return PARAMETERS();
        }
        return null;
    }
    //PARAMETERS -> id PARAMETERS_2
    private List<Token> PARAMETERS(){
        List<Token> lstprmtrs = new ArrayList<>();
        if(hayErrores)
            return null;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            lstprmtrs.add(previous());
            PARAMETERS_2(lstprmtrs);
            return lstprmtrs;

        }else{
            hayErrores = true;
            System.out.println("Error en PARAMETERS");
            return null;
        }
    }
    /*
    PARAMETERS_2 -> , id PARAMETERS_2
                 -> Ɛ           */
    private void PARAMETERS_2(List<Token> lstprmtrs){
        if(hayErrores)
            return;
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            lstprmtrs.add(previous());
            PARAMETERS_2(lstprmtrs);
        }
    }
    /*
    ARGUMENTS_OPC -> EXPRESSION ARGUMENTS
                  -> Ɛ          */
    private List<Expression> ARGUMENTS_OPC(){
        List<Expression> lstArguments = new ArrayList<>();
        if(hayErrores)
            return null;
        switch (preanalisis.tipo){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = EXPRESSION();
                lstArguments.add(expr);
                ARGUMENTS(lstArguments);
            }
        }
        return lstArguments;
    }
    /*
    ARGUMENTS -> , EXPRESSION ARGUMENTS
              -> Ɛ          */
    private void ARGUMENTS(List<Expression> lstArguments){
        if(hayErrores)
            return;
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            Expression expr = EXPRESSION();
            lstArguments.add(expr);
            ARGUMENTS(lstArguments);
        }
    }
    // -------------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
            
        }else{
            hayErrores = true;
            System.out.println("Error encontrado");
        }
    }
    /*
    private void match(TipoToken tt) throws ParserException {
        if(preanalisis.getTipo() ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error en la línea " +
                    preanalisis.getPosition().getLine() +
                    ". Se esperaba " + preanalisis.getTipo() +
                    " pero se encontró " + tt;
            throw new ParserException(message);
        }
    }
    */
    private Token previous(){
        return this.tokens.get(i-1);
    }    
}
