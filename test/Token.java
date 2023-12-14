
package test;

//import domain.*;

public class Token {
    final TipoToken tipo;
    final String lexema;
    final Object literal;
    final int position;
    final int line;

    public Token(TipoToken tipo, String lexema, int position, int line) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.position = position;
        this.line = line;
        this.literal = null;
    }

    public Token(TipoToken tipo, String lexema, Object literal, int position, int line) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.position = position;
        this.line = line;
        this.literal = literal;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) {
            return false;
        }

        if(this.tipo == ((Token)o).tipo){
            return true;
        }

        return false;
    }

    public String toString() {
        return "<" + tipo + " " + lexema + " " + literal + ">";
    }
    
    public Object getLiteral(){
        return this.literal;
    }

    public TipoToken getTipo(){
        return tipo;
    }
    public int getPosition() {
        return position;
    }
    public int getLine(){
        return line;
    }

}
