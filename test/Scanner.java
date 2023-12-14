package test;

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
    
    public String aCadena(String lexema, char c){//Sirve para quitar las comillas a una cadena 
        String res = "";
        for(int i = 0; i < lexema.length(); i++){
            if(lexema.charAt(i) != c)
                res += lexema.charAt(i);
        }
        
        return res;
    }

    public List<Token> scan() throws Exception {
        int bandera = 0, estado = 0, line = 0, pos = 0;
        String lexema = "";
        Token t;
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);
            if(c == '\n'){
                line++;
                pos = 0;
            }
            else pos = i;
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
                        t = new Token(TipoToken.GREATER_EQUAL, lexema, pos, line);

                    } else {
                        t = new Token(TipoToken.GREATER, lexema, pos, line);
                        i--;
                    }
                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                case 4:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.LESS_EQUAL, lexema, pos, line);
                    } else {
                        t = new Token(TipoToken.LESS, lexema, pos, line);
                        i--;
                    }
                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                case 7:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.EQUAL_EQUAL, lexema, pos, line);
                    } else {
                        t = new Token(TipoToken.EQUAL, lexema, pos, line);
                        i--;
                    }

                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                case 10:
                    if (c == '=') {
                        lexema += c;
                        t = new Token(TipoToken.BANG_EQUAL, lexema, pos, line);
                    } else {
                        t = new Token(TipoToken.BANG, lexema, pos, line);
                        i--;
                    }

                    tokens.add(t);
                    lexema = "";
                    estado = 0;
                    break;
                case 13:
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        TipoToken tt = palabrasReservadas.get(lexema);
                        if (tt == null) {
                            t = new Token(TipoToken.IDENTIFIER, lexema, pos, line);
                        } else {
                            t = new Token(tt, lexema, pos, line);
                        }

                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
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
                        t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema), pos, line);
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
                        Interprete.error(line, "Se esperaba: [0-9] -> " + lexema);
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
                        t = new Token(TipoToken.NUMBER, lexema, Float.valueOf(lexema), pos, line);
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
                        Interprete.error(line, "Se  esperaba: [0-9, +, -] -> " + lexema);
                    }
                    break;
                case 19:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else {
                        Interprete.error(line, "Se esperaba: [0-9, +, -] -> " + lexema);
                    }
                    break;
                case 20:
                    if (Character.isDigit(c)) {
                        lexema += c;
                    } else {
                        // Token notación cientifica ---------------------------
                        t = new Token(TipoToken.NUMBER, lexema, Float.valueOf(lexema), pos, line);
                        tokens.add(t);
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;
                case 24:              
                    if (c == '\n') {
                        Interprete.error(line, "No se esperaba un salto de línea -> " + lexema);
                        estado = 0;
                        lexema = "";
                    }
                    else if(c != '"'){
                        estado = 24;
                        lexema += c;
                        bandera = 1;
                    }
                    else if(c == '"'){
                        estado = 25;
                        lexema += c;
                    }
                    break;
                case 25:
                    Token t13 = new Token(TipoToken.STRING, lexema, aCadena(lexema, '"'), pos, line);
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
                        t = new Token(TipoToken.SLASH, lexema, pos, line);
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
                        System.out.println("Comentario multilinea");
                    } else if (c != '*') {
                        estado = 27;
                    }
                    break;
                case 30:
                    if (c == '\n') {
                        estado = 0;
                        lexema = "";
                        System.out.println("Comentario en linea");
                        line++;
                    }
                    break;
                case 33:
                    //System.out.println("Se detecto un espacio");
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                case 34:
                    // Tokens de un caracter
                    TipoToken tt1 = Token_Caracter.get(lexema);
                    if(tt1 != null){
                        Token t21 = new Token(tt1, lexema, pos, line);
                        tokens.add(t21);
                    }                        
                    else if(c == ' ') ;
                    else if(c == '\n') line++;
                    else Interprete.error(line, "No se reconoce el caracter: " + lexema);
                    estado = 0;
                    lexema = "";
                    i--;
                    break;
            }
        }
        tokens.add(new Token(TipoToken.EOF, "", source.length(), pos, line));
        
        return tokens;
    }
}
