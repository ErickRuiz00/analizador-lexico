import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

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

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source) {
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        int estado = 0;
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);
            switch (estado) {
                case 0:
                    if( c == '>'){
                        estado = 1;
                        lexema += c;
                    }
                    else if(c == '<'){
                        estado = 4;
                        lexema += c;
                    }
                    else if(c == '='){
                        estado = 7;
                        lexema += c;
                    }
                    else if(c == '!'){
                        estado = 10;
                        lexema += c;
                    }
                    else if(Character.isLetter(c)){
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
                    }
                    break;
                case 1:
                    if (c == '=') {
                        estado = 2;
                        lexema += c;
                    }
                    else{
                        estado = 3;
                        //lexema += c;
                    }
                    break;
                case 2:
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
                case 4:
                    if (c == '=') {
                        estado = 5;
                        lexema += c;
                    }
                    else{
                        estado = 6;
                        //lexema += c;
                    }
                    break;
                case 5:
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
                    break;
                case 7:
                    if (c == '=') {
                        estado = 8;
                        lexema += c;
                    }
                    else{
                        estado = 9;
                        lexema += c;
                    }
                    break;
                case 8:
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
                    break;
                case 10:
                    if (c == '=') {
                        estado = 11;
                        lexema += c;
                    }
                    else{
                        estado = 12;
                        //lexema += c;
                    }
                    break;
                case 11:
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
                    break;
                case 13:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        estado = 14;
                        //lexema += c;
                    }
                    break;
                case 14:
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
                        i--;
                case 24:
                    if (c == '"') {
                        estado = 25;
                        lexema += c;
                    } else if (c == '\n') {
                        Interprete.error(1, "No se esperaba un salto de línea");
                        System.out.println("No se esperaba un salto de línea");
                        estado = 0;
                        lexema = "";
                    } else lexema += c;
                    break;
                case 25:
                    Token t0 = new Token(TipoToken.STRING, lexema);
                    tokens.add(t0);

                    estado = 0;
                    lexema = "";
                    break;
                case 26:
                    if (c == '*') {
                        estado = 27;
                        lexema += c;
                    } else if (c == '/') {
                        estado = 30;
                        lexema += c;
                    } else {
                        estado = 32;
                    }
                    break;
                case 27:
                    // En este estado estamos dentro de un comentario multilinea, no
                    // es necesario guardar el lexema porque no se generará un token.
                    if (c == '*') estado = 28;
                    break;
                case 28:
                    if (c == '/') estado = 29;
                    else if (c != '*') estado = 27;
                    break;
                case 29:
                    // Estado de aceptación comentario multilinea
                    estado = 0;
                    lexema = "";
                    System.out.println("Comentario multilinea");
                    break;
                case 30:
                    if (c == '\n') estado = 31;
                    break;
                case 31:
                    // Estado de aceptación comentario en línea
                    estado = 0;
                    lexema = "";
                    System.out.println("Comentario en línea");
                    break;
                case 32:
                    Token t01 = new Token(TipoToken.SLASH, lexema);
                    tokens.add(t01);
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                case 15:
                    if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    else if(c == '.'){
                        estado = 16;
                        lexema += c;
                        
                    }
                    else if(c == 'E' || c== 'e'){
                        estado = 18;
                        lexema += c;
                    }
                    else{
                        estado = 22;
                        //lexema += c;
                    }
                    break;
                case 16:
                    if(Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    }
                    break;
                case 17:
                    if(Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    }
                    else if(c == 'E' || c== 'e'){
                        estado = 18;
                        lexema += c;
                    }
                    else{
                        estado = 23;
                    }
                    break;
                case 18:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else if(c == '+' || c == '-'){
                        estado = 19;
                        lexema += c;
                    }
                    break;
                case 19:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    break;
                case 20:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        estado = 21;
                    }
                    break;
                case 21:
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
                    break;
            }
        }
        return tokens;
    }
}
