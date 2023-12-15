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
    public boolean parse() throws ParserException {
        try{
            statementTree = PROGRAM();
            if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
                System.out.println("Linea correcta");
                return true;
            }else
                System.out.println("Linea incorrecta");
        }catch (ParserException e){
            System.out.println(e.getMessage());
            return false;
        }

        
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PROGRAM -> DECLARATION 
    private List<Statement> PROGRAM() throws ParserException{
        List<Statement> statements = new ArrayList();
        DECLARATION(statements);
        return statements;
        
    }
    
    /* DECLARATION -> FUN_DECL DECLARATION
                      VAR_DECL DECLARATION
                      STATEMENT DECLARATION
                      EPSILON               */
    // Declaraciones -----------------------------------------------------------------------------------------
    private void DECLARATION(List<Statement> statements) throws ParserException {
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
    private Statement FUN_DECL() throws ParserException{
        if(preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            return FUNCTION();
        }
        return null;
    }
    
    // VAR_DECL -> var id VAR_INIT ;
    private Statement VAR_DECL() throws ParserException{
        if(preanalisis.tipo == TipoToken.VAR){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            Expression initializer = VAR_INIT();
            match(TipoToken.SEMICOLON);
            return new StmtVar(name,initializer);
        }
        return null;
    }
    
    // VAR_INIT -> = EXPRESSION 
    //          -> ε
    private Expression VAR_INIT() throws ParserException{
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            return EXPRESSION();
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
    private Statement STATEMENT() throws ParserException{
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
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba un statement";
                throw new ParserException(message);
            }
        }
    }

    // EXPR_STMT -> EXPRESSION;
    private Statement EXPR_STMT() throws ParserException{
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN -> {  
                Expression expr = EXPRESSION();
                match(TipoToken.SEMICOLON);
                return new StmtExpression(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba un expr_stmt";
                throw new ParserException(message);
            }
        }
    }
    
    // FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private Statement FOR_STMT() throws ParserException{
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
            return null;
        }
    } 
    /*
    FOR_STMT_1 -> VAR_DECL
               -> EXPR_STMT
               -> ;
    */
    private Statement FOR_STMT_1() throws ParserException{
        switch (preanalisis.tipo) {
            case VAR ->{
                return VAR_DECL();
            }
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                return EXPR_STMT();
            }
            case SEMICOLON ->{ 
                match(TipoToken.SEMICOLON);
                return null;
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba un inicializador o ;";
                throw new ParserException(message);
            }
        }
    }
    
    /*
    FOR_STMT_2 -> EXPRESSION;
               -> ;
    */
    private Expression FOR_STMT_2() throws ParserException{
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
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba una condición o ;";
                throw new ParserException(message);
            }
        }
    }
    
    /*
    FOR_STMT_3 -> EXPRESSION
               -> Ɛ         */
    private Expression FOR_STMT_3() throws ParserException{
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                return EXPRESSION();
            }
        }
        return null;
    }
    
    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private Statement IF_STMT() throws ParserException{
        if(preanalisis.tipo == TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement thenBranch = STATEMENT();
            Statement elseBranch = ELSE_STATEMENT();
            return new StmtIf(condition, thenBranch, elseBranch);
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Se esperaba una condición";
            throw new ParserException(message);
        }
    }
    /*
    ELSE_STATEMENT -> else STATEMENT
                   -> Ɛ         */
    private Statement ELSE_STATEMENT() throws ParserException{
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            return STATEMENT();
        }
        return null;
    }
    // PRINT_STMT -> print EXPRESSION ;
    private Statement PRINT_STMT() throws ParserException{
        if(preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            Expression expr = EXPRESSION();
            Statement pstmt = new StmtPrint(expr);
            match(TipoToken.SEMICOLON);
            return pstmt;
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Se esperaba un print_stmt";
            throw new ParserException(message);
        }
        
    }
    //RETURN_STMT -> return RETURN_EXP_OPC ;
    private Statement RETURN_STMT() throws ParserException{
        if(preanalisis.tipo == TipoToken.RETURN){
            match(TipoToken.RETURN);
            Expression expr = RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
            return new StmtReturn(expr);
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Se esperaba un return";
            throw new ParserException(message);
        }
    }
    /*
    RETURN_EXP_OPC -> EXPRESSION
                   -> Ɛ         */
    private Expression RETURN_EXP_OPC() throws ParserException{
        switch (preanalisis.tipo) {
             case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                return EXPRESSION();
             }
        }
        return null;
    }
    // WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    private Statement WHILE_STMT() throws ParserException{
        if(preanalisis.tipo == TipoToken.WHILE){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            Expression condition = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement body = STATEMENT();
            return new StmtLoop(condition, body);
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Error en ciclo while";
            throw new ParserException(message);
        }
    }
    // BLOCK -> { DECLARATION }
    private Statement BLOCK() throws ParserException{
        if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            match(TipoToken.LEFT_BRACE);
            List<Statement> statements = new ArrayList<>();      
            DECLARATION(statements);
            match(TipoToken.RIGHT_BRACE);
            return new StmtBlock(statements);
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Falta '{' o '}'";
            throw new ParserException(message);
        }
    }
    
    
    // -------------------------------------------------------------------------------------------
    // Expresiones -------------------------------------------------------------------------------
    //EXPRESSION -> ASSIGNMENT
    private Expression EXPRESSION() throws ParserException{
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                return ASSIGNMENT();
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba una expresión";
                throw new ParserException(message);
            }
        }
    }
    // ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    private Expression ASSIGNMENT() throws ParserException{
        switch (preanalisis.tipo) {
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = LOGIC_OR();
                return ASSIGNMENT_OPC(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error de asignación";
                throw new ParserException(message);
            }
        }
    }
    //Chechar esta clase -------------------------------------------------------------
    /*
    ASSIGNMENT_OPC -> = EXPRESSION
                   -> Ɛ             */
    private Expression ASSIGNMENT_OPC(Expression expr) throws ParserException{
        if(preanalisis.tipo == TipoToken.EQUAL){
            Token name = previous();
            match(TipoToken.EQUAL);
            Expression value = EXPRESSION();
            return new ExprAssign(name, value);
        }
        return expr;
    }
    // ------------------------------------------------------------------------------
    //LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    private Expression LOGIC_OR() throws ParserException{
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = LOGIC_AND();
                return LOGIC_OR_2(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error lógico OR";
                throw new ParserException(message);
            }
        }
    }
    /*
    LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
               -> Ɛ         */
    private Expression LOGIC_OR_2(Expression expr) throws ParserException{
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
    private Expression LOGIC_AND() throws ParserException{
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = EQUALITY();
                return LOGIC_AND_2(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error lógico and";
                throw new ParserException(message);
            }
        }
    }
    /*
    LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
                -> Ɛ            */
    private Expression LOGIC_AND_2(Expression expr) throws ParserException{
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
    private Expression EQUALITY() throws ParserException{
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = COMPARISON();
                return EQUALITY_2(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error en la igualdad";
                throw new ParserException(message);
            }
        }
    }
    /*
    EQUALITY_2 -> != COMPARISON EQUALITY_2
               -> == COMPARISON EQUALITY_2
               -> Ɛ         */
    private Expression EQUALITY_2(Expression expr) throws ParserException{
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
    private Expression COMPARISON() throws ParserException{
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = TERM();
                return COMPARISON_2(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error de comparación";
                throw new ParserException(message);
            }
        }
    }
    /*
    COMPARISON_2 -> > TERM COMPARISON_2
                 -> >= TERM COMPARISON_2
                 -> < TERM COMPARISON_2
                 -> <= TERM COMPARISON_2
                 -> Ɛ           */
    private Expression COMPARISON_2(Expression expr) throws ParserException{
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
    private Expression TERM() throws ParserException{
        switch (preanalisis.tipo) {
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = FACTOR();
                return TERM_2(expr);
            }
            default -> {
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". error en term";
                throw new ParserException(message);
            }
        }
    }
    /*
    TERM_2 -> - FACTOR TERM_2
           -> + FACTOR TERM_2
           -> Ɛ             */
    private Expression TERM_2(Expression expr) throws ParserException{
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
    private Expression FACTOR() throws ParserException{
        switch (preanalisis.tipo){
            case BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression expr = UNARY();
                return FACTOR_2(expr);
            }
            default ->{
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error en el factor";
                throw new ParserException(message);
            }
        }
    }
    /*
    FACTOR_2 -> / UNARY FACTOR_2
             -> * UNARY FACTOR_2
             -> Ɛ               */
    private Expression FACTOR_2(Expression expr) throws ParserException{
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
    private Expression UNARY() throws ParserException{
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
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba un token unario";
                throw new ParserException(message);
            }
        }
    }
    //CALL -> PRIMARY CALL_2
    private Expression CALL() throws ParserException{
        switch (preanalisis.tipo){
            case TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN ->{
                Expression exprPrimary = PRIMARY();
                return CALL_2(exprPrimary);
            }
            default ->{
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Error en la llamada";
                throw new ParserException(message);
            }
        }
    }
    /*
    CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
           -> Ɛ         */
    private Expression CALL_2(Expression expr) throws ParserException{
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
    private Expression PRIMARY() throws ParserException{
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
                String message = "Error en la linea " + preanalisis.getLine() +
                        preanalisis.getPosition() + ". Se esperaba un token primario";
                throw new ParserException(message);
            }
        }
    }
    // -------------------------------------------------------------------------------------------
    // OTRAS -------------------------------------------------------------------------------------
    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    private Statement FUNCTION() throws ParserException{
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            match(TipoToken.LEFT_PAREN);
            List<Token> params = PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            StmtBlock body = (StmtBlock) BLOCK();
            return new StmtFunction(name, params, body);
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Se esperaba un identificador de función";
            throw new ParserException(message);
        }
    }
    /*
    PARAMETERS_OPC -> PARAMETERS
                   -> Ɛ         */
    private List<Token> PARAMETERS_OPC() throws ParserException{
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            return PARAMETERS();
        }
        return null;
    }
    //PARAMETERS -> id PARAMETERS_2
    private List<Token> PARAMETERS() throws ParserException{
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            List<Token> lstprmtrs = new ArrayList<>();
            match(TipoToken.IDENTIFIER);
            lstprmtrs.add(previous());
            PARAMETERS_2(lstprmtrs);
            return lstprmtrs;
        }else{
            String message = "Error en la linea " + preanalisis.getLine() +
                    preanalisis.getPosition() + ". Se esperaba una lista de parametros";
            throw new ParserException(message);
        }
    }
    /*
    PARAMETERS_2 -> , id PARAMETERS_2
                 -> Ɛ           */
    private void PARAMETERS_2(List<Token> lstprmtrs) throws ParserException{
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
    private List<Expression> ARGUMENTS_OPC() throws ParserException{
        List<Expression> lstArguments = new ArrayList<>();
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
    private void ARGUMENTS(List<Expression> lstArguments) throws ParserException{
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            Expression expr = EXPRESSION();
            lstArguments.add(expr);
            ARGUMENTS(lstArguments);
        }
    }
    // -------------------------------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void match(TipoToken tt) throws ParserException{
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
            
        }else{
            hayErrores = true;
            System.out.println("Error encontrado");
            String message = "Error en la linea " +
                    preanalisis.getLine() +
                    ". Se esperaba " + preanalisis.getTipo() +
                    " pero se encontró " + tt;
            throw new ParserException(message);
        }
    }
    private Token previous(){
        return this.tokens.get(i-1);
    }    
}
