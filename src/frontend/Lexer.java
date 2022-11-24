package frontend;

import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lexer {
    private final String text;
    private int pos;
    public String curVal;
    public TokenType curType;
    public boolean end;
    public int line;
    public ArrayList<Token> tokens;

    private static final HashSet<String> KEYWORD =
            Stream.of("main", "const", "int", "break", "continue",
                      "if", "else", "while", "getint", "printf",
                      "return", "void")
                    .collect(Collectors.toCollection(HashSet::new));

    private static final HashMap<String, TokenType> MAPPER = new HashMap<String, TokenType>() {{
        put("main", TokenType.MAINTK);
        put("const", TokenType.CONSTTK);
        put("int", TokenType.INTTK);
        put("break", TokenType.BREAKTK);
        put("continue", TokenType.CONTINUETK);
        put("if", TokenType.IFTK);
        put("else", TokenType.ELSETK);
        put("!", TokenType.NOT);
        put("&&", TokenType.AND);
        put("||", TokenType.OR);
        put("while", TokenType.WHILETK);
        put("getint", TokenType.GETINTTK);
        put("printf", TokenType.PRINTFTK);
        put("return", TokenType.RETURNTK);
        put("+", TokenType.PLUS);
        put("-", TokenType.MINU);
        put("void", TokenType.VOIDTK);
        put("*", TokenType.MULT);
        put("/", TokenType.DIV);
        put("%", TokenType.MOD);
        put("<", TokenType.LSS);
        put("<=", TokenType.LEQ);
        put(">", TokenType.GRE);
        put(">=", TokenType.GEQ);
        put("==", TokenType.EQL);
        put("!=", TokenType.NEQ);
        put("=", TokenType.ASSIGN);
        put(";", TokenType.SEMICN);
        put(",", TokenType.COMMA);
        put("(", TokenType.LPARENT);
        put(")", TokenType.RPARENT);
        put("[", TokenType.LBRACK);
        put("]", TokenType.RBRACK);
        put("{", TokenType.LBRACE);
        put("}", TokenType.RBRACE);
    }};

    public static final String UNI_FIRST_SINGLE = "+-*/%;,()[]{}";

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        this.line = 1;
        this.end = false;
        this.tokens = new ArrayList<>();
        this.next();
    }

    public void next() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            if (text.charAt(pos) == '\n') { line++; }
            pos++;
        }
        if (pos == text.length()) { this.end = true; return; }
        if (pos < text.length() - 1 && text.charAt(pos) == '/') {
            if (text.charAt(pos + 1) == '/') {
                pos += 2;
                while (pos < text.length() && text.charAt(pos) != '\n') {
                    pos++;
                }
                line++;
                pos++;
                next();
                return;
            }
            else if (text.charAt(pos + 1) == '*') {
                pos += 3;
                while (pos < text.length() &&
                        !(text.charAt(pos) == '/' && text.charAt(pos - 1) == '*')) {
                    if (text.charAt(pos) == '\n') { line++; }
                    pos++;
                }
                pos++;
                next();
                return;
            }
        }
        if (UNI_FIRST_SINGLE.indexOf(text.charAt(pos)) != -1) {
            curVal = String.valueOf(text.charAt(pos++));
            curType = MAPPER.get(curVal);
        }
        else if ("|&".indexOf(text.charAt(pos)) != -1) {
            curVal = String.valueOf(text.charAt(pos++));
            curVal += text.charAt(pos++);
            curType = MAPPER.get(curVal);
        }
        else if ("!=<>".indexOf(text.charAt(pos)) != -1) {
            curVal = String.valueOf(text.charAt(pos++));
            if (pos < text.length() && text.charAt(pos) == '=') {
                curVal += text.charAt(pos++);
            }
            curType = MAPPER.get(curVal);
        }
        else if (Character.isDigit(text.charAt(pos))) {
            StringBuilder sb = new StringBuilder();
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                sb.append(text.charAt(pos++));
            }
            curVal = sb.toString();
            curType = TokenType.INTCON;
        }
        else if (Character.isLetter(text.charAt(pos)) || text.charAt(pos) == '_') {
            StringBuilder sb = new StringBuilder();
            while (pos < text.length() &&
                    (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_')) {
                sb.append(text.charAt(pos++));
            }
            curVal = sb.toString();
            if (KEYWORD.contains(curVal)) { curType = MAPPER.get(curVal); }
            else { curType = TokenType.IDENFR; }
        }
        else if (text.charAt(pos) == '"') {
            StringBuilder sb = new StringBuilder();
            sb.append(text.charAt(pos++));
            while (pos < text.length() && text.charAt(pos) != '"') {
                sb.append(text.charAt(pos++));
            }
            sb.append(text.charAt(pos++));
            curVal = sb.toString();
            curType = TokenType.STRCON;
        }
        else if(text.charAt(pos)=='#'){
            StringBuilder sb = new StringBuilder();
            pos++;
            while (pos < text.length() &&
                   (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_')) {
                sb.append(text.charAt(pos++));
            }
            curVal = sb.toString();
            curType = TokenType.TAG;
        }
        else { pos++; }
    }

    public void tokenize() {
        while (!end) {
            tokens.add(new Token(curType, curVal,line));
            next();
        }
    }
}
