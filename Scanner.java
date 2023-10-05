import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;
    private static final Map<String, TipoToken> Token_Caracter;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.AND);
        palabrasReservadas.put("else", TipoToken.ELSE);
        palabrasReservadas.put("false", TipoToken.FALSE);
        palabrasReservadas.put("for", TipoToken.FOR);
        palabrasReservadas.put("fun", TipoToken.FUN);
        palabrasReservadas.put("if", TipoToken.IF);
        palabrasReservadas.put("null", TipoToken.NULL);
        palabrasReservadas.put("or", TipoToken.OR);
        palabrasReservadas.put("print", TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true", TipoToken.TRUE);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("while", TipoToken.WHILE);
    }

    // LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    //    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR
    static {
        Token_Caracter = new HashMap<>();
        Token_Caracter.put("(", TipoToken.LEFT_PAREN);
        Token_Caracter.put(")", TipoToken.RIGHT_PAREN);
        Token_Caracter.put("{", TipoToken.LEFT_BRACE);
        Token_Caracter.put("}", TipoToken.RIGHT_BRACE);
        Token_Caracter.put(",", TipoToken.COMMA);
        Token_Caracter.put(".", TipoToken.DOT);
        Token_Caracter.put("-", TipoToken.MINUS);
        Token_Caracter.put("+", TipoToken.PLUS);
        Token_Caracter.put(";", TipoToken.SEMICOLON);
        Token_Caracter.put("/", TipoToken.SLASH);
        Token_Caracter.put("*", TipoToken.STAR);
    }
    ;

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source) {
        this.source = source + " ";
    }
    
    public String aCadena(String lexema, char c){
        String res = "";
        for(int i = 0; i < lexema.length(); i++){
            if(lexema.charAt(i) != c)
                res += lexema.charAt(i);
        }
        
        return res;
    }
    /*
    public String aNumbers(String lexema, char c){
        String res = "";
        int i = 0;
        
        // Ceros por la izquierda;
        while(lexema.charAt(i) == c) i++;
  
        for(; i < lexema.length() ; i++)
            res += lexema.charAt(i);
        
        if(res.charAt(0) == '.')
            res = '0' + res;
        
        return res;
    }
    */

    public List<Token> scan() throws Exception {
        int bandera = 0;
        String lexema = "";
        Token t;
        int estado = 0;
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);
            switch (estado) {
                case 0:
                    if (c == '>') {
                        estado = 1;
                        lexema += c;
                    } else if (c == '<') {
                        estado = 4;
                        lexema += c;
                    } else if (c == '=') {
                        estado = 7;
                        lexema += c;
                    } else if (c == '!') {
                        estado = 10;
                        lexema += c;
                    } else if (Character.isLetter(c)) {
                        estado = 13;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 15;
                        lexema += c;
                    } else if (c == '"') {
                        estado = 24;
                        lexema += c;
                    } else if (c == '/') {
                        estado = 26;
                        lexema += c;
                    } else if(c == ' ' || c == '\n'){
                        estado = 33;
                        lexema += c;
                    } else {
                        estado = 34;
                        lexema += c;
                    }
                    break;
                case 1:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.GREATER_EQUAL, lexema);

                    } else {
                        t = new Token(TipoToken.GREATER, lexema);
                        i--;
                    }
                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                /* case 2:
                    // Vamos a crear el Token de mayor o igual -----------------
                    Token t = new Token(TipoToken.GREATER_EQUAL, lexema, null);
                    tokens.add(t);
                    estado = 0;
                    lexema = "";
                    //i--;
                    break;
                case 3:
                    // Vamos a crear el Token de mayor -------------------------
                    Token t1 = new Token(TipoToken.GREATER, lexema, null);
                    tokens.add(t1);
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                 */

                case 4:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.LESS_EQUAL, lexema);
                    } else {
                        t = new Token(TipoToken.LESS, lexema);
                        i--;
                    }
                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                /*case 5:
                    // Vamos a crear el Token de menor o igual -----------------
                    Token t2 = new Token(TipoToken.LESS_EQUAL, lexema, null);
                    tokens.add(t2);
                    estado = 0;
                    lexema = "";
                    //i--;
                    break;
                case 6:
                    // Vamos a crear el Token de menor -------------------------
                    Token t3 = new Token(TipoToken.LESS, lexema, null);
                    tokens.add(t3);
                    estado = 0;
                    lexema = "";
                    i--;
                    break; */
                case 7:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                    } else {
                        t = new Token(TipoToken.EQUAL, lexema);
                        i--;
                    }

                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                /*case 8:
                    // Vamos a crear el Token de igual igual -------------------
                    Token t4 = new Token(TipoToken.EQUAL_EQUAL, lexema, null);
                    tokens.add(t4);
                    estado = 0;
                    lexema = "";
                    //i--;
                    break;
                case 9:
                    // Vamos a crear el Token de igual -------------------------
                    Token t5 = new Token(TipoToken.EQUAL, lexema, null);
                    tokens.add(t5);
                    estado = 0;
                    lexema = "";
                    i--;
                    break; */
                case 10:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.BANG_EQUAL, lexema);
                    } else {
                        t = new Token(TipoToken.BANG, lexema);
                        i--;
                    }

                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                /*case 11:
                    // Vamos a crear el Token de diferente -------------------
                    Token t6 = new Token(TipoToken.BANG_EQUAL, lexema, null);
                    tokens.add(t6);
                    estado = 0;
                    lexema = "";
                    //i--;
                    break;
                case 12:
                    // Vamos a crear el Token de igual igual -------------------
                    Token t7 = new Token(TipoToken.BANG, lexema, null);
                    tokens.add(t7);
                    estado = 0;
                    lexema = "";
                    i--;
                    break; */
                case 13:
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        TipoToken tt = palabrasReservadas.get(lexema);
                        if (tt == null) {
                            t = new Token(TipoToken.IDENTIFIER, lexema);
                        } else {
                            t = new Token(tt, lexema);
                        }

                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                /*case 14:
                    // Vamos a crear el Token de id o palabra reservada --------
                    TipoToken tt = palabrasReservadas.get(lexema);
                    if(tt == null){
                        Token t8 = new Token(TipoToken.IDENTIFIER, lexema);
                        tokens.add(t8);
                    }
                    else{
                        Token t9 = new Token(tt, lexema);
                        tokens.add(t9);
                    }
                    estado = 0;
                    lexema = "";
                    //i--;
                    break; */
                case 15:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else if (c == '.') {
                        estado = 16;
                        lexema += c;

                    } else if (c == 'E' || c == 'e') {
                        estado = 18;
                        lexema += c;
                    } else {
                        t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else {
                        Interprete.error(1, "Se esperaba un digito");
                        System.out.println("Se esperaba un dígito");
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 18;
                        lexema += c;
                    } else {
                        // Token numero flotante - Equivalente estado 23
                        t = new Token(TipoToken.NUMBER, lexema, Float.valueOf(lexema));
                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                case 18:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else if (c == '+' || c == '-') {
                        estado = 19;
                        lexema += c;
                    } else {
                        Interprete.error(1, "Se  esperaba un digito, '+' o '-'");
                        System.out.println("Se  esperaba un digito, '+' o '-'");
                    }
                    break;
                case 19:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else {
                        Interprete.error(1, "Se esperaba un digito");
                        System.out.println("Se esperaba un digito");
                    }
                    break;
                case 20:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        // Token notación cientifica ---------------------------
                        t = new Token(TipoToken.NUMBER, lexema, Float.valueOf(lexema));
                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                /*case 21:
                    //Generacion de token con notacion cientifica
                    Token t12 = new Token(TipoToken.NUMBER, lexema,lexema);
                    tokens.add(t12);
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                case 22:
                    //Vamos a crear el token de un numero entero ---------------
                    Token t10 = new Token(TipoToken.NUMBER, lexema,Integer.valueOf(lexema));
                    tokens.add(t10);
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                case 23:
                    //Vamos a crear el token de un numero flotante -------------
                    Token t11 = new Token(TipoToken.NUMBER, lexema,Float.valueOf(lexema));
                    tokens.add(t11);
                    estado = 0;
                    lexema = "";
                    i--;
                    break; */
                case 24:              
                    if (c == '\n') {
                        Interprete.error(1, "No se esperaba un salto de línea");
                        System.out.println("No se esperaba un salto de línea");
                        estado = 0;
                        lexema = "";
                    }
                    else if(c != '"'){
                        estado = 24;
                        lexema += c;
                        bandera = 1;
                    }
                    else if( c == '"'){
                        estado = 25;
                        lexema += c;
                    }
                    break;
                case 25:
                    Token t13 = new Token(TipoToken.STRING, lexema, aCadena(lexema, '"'));
                    tokens.add(t13);
                    estado = 0;
                    lexema = "";
                    i--;
                    bandera = 0;
                    break;
                case 26:
                    if (c == '*') {
                        estado = 27;
                        lexema += c;
                    } else if (c == '/') {
                        estado = 30;
                        lexema += c;
                    } else {
                        t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                case 27:
                    // En este estado estamos dentro de un comentario multilinea, no
                    // es necesario guardar el lexema porque no se generará un token.
                    if (c == '*') {
                        estado = 28;
                    }
                    break;
                case 28:
                    if (c == '/') {
                        estado = 0;
                        lexema = "";
                        //System.out.println("Comentario multilinea");
                    } else if (c != '*') {
                        estado = 27;
                    }
                    break;
                /*case 29:
                    // Estado de aceptación comentario multilinea
                    estado = 0;
                    lexema = "";
                    System.out.println("Comentario multilinea");
                    break; */
                case 30:
                    if (c == '\n') {
                        estado = 0;
                        lexema = "";
                        System.out.println("Comentario en linea");
                    }
                    break;
                /*case 31:
                    // Estado de aceptación comentario en línea
                    estado = 0;
                    lexema = "";
                    System.out.println("Comentario en línea");
                    break;*/
                /*case 32:
                    Token t01 = new Token(TipoToken.SLASH, lexema);
                    tokens.add(t01);
                    estado = 0;
                    lexema = "";
                    i--;
                    break; */
                case 33:
                    //System.out.println("Se detecto un espacio");
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                case 34:
                    TipoToken tt1 = Token_Caracter.get(lexema);
                    if(tt1 != null){
                        Token t21 = new Token(tt1, lexema);
                        tokens.add(t21);
                    }                        
                    else{
                        System.out.println("Error, caracter inválido ");                        
                    }
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
            }
        }
        if(bandera == 1){
            System.out.println("ERROR");
        }
        return tokens;
    }
}
