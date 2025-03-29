import java.io.*;

public class AsdrSample {

   private static final int BASE_TOKEN_NUM = 301;
   
   public static final int IDENT = 301;
   public static final int NUM = 302;
   public static final int WHILE = 303;
   public static final int IF = 304;
   public static final int ELSE = 305;
   public static final int FUNC = 306;
   public static final int INT = 307;
   public static final int DOUBLE = 308;
   public static final int BOOLEAN = 309;
   public static final int VOID = 310;
   public static final int FI = 311;

   public static final String tokenList[] = {"IDENT", "NUM", "WHILE", "IF", "ELSE", "FUNC", "INT", "DOUBLE", "BOOLEAN", "VOID"};
   
   private Yylex lexer;
   public ParserVal yylval;
   private static int laToken;
   private boolean debug;

   public AsdrSample(Reader r) {
       lexer = new Yylex(r, this);
   }

   private void Prog() {
       if (debug) System.out.println("Prog --> ListaDecl");
       ListaDecl();
   }

   private void ListaDecl() {
       if (debug) System.out.println("ListaDecl: laToken = " + laToken);
       if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
           if (debug) System.out.println("ListaDecl --> DeclVar ListaDecl");
           DeclVar();
           ListaDecl();
       } else if (laToken == FUNC) {
           if (debug) System.out.println("ListaDecl --> DeclFun ListaDecl");
           DeclFun();
           ListaDecl();
       }
   }

   private void DeclVar() {
       if (debug) System.out.println("DeclVar: laToken = " + laToken);
       if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
           if (debug) System.out.println("DeclVar --> Tipo ListaIdent ; DeclVar");
           Tipo();
           ListaIdent();
           verifica(';');
           DeclVar();
       }
   }

   private void Tipo() {
       if (debug) System.out.println("Tipo: laToken = " + laToken);
       if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
           if (debug) System.out.println("Tipo --> " + tokenList[laToken - BASE_TOKEN_NUM]);
           verifica(laToken);
       } else {
           yyerror("Tipo esperado");
       }
   }

   private void ListaIdent() {
       if (debug) System.out.println("ListaIdent: laToken = " + laToken);
       verifica(IDENT);
       if (laToken == ',') {
           verifica(',');
           ListaIdent();
       }
   }

   private void DeclFun() {
       if (debug) System.out.println("DeclFun: laToken = " + laToken);
       if (laToken == FUNC) {
           if (debug) System.out.println("DeclFun --> FUNC TipoOuVoid IDENT ( FormalPar ) { DeclVar ListaCmd } DeclFun");
           verifica(FUNC);
           TipoOuVoid();
           verifica(IDENT);
           verifica('(');
           FormalPar();
           verifica(')');
           verifica('{');
           DeclVar();
           ListaCmd();
           verifica('}');
           DeclFun();
       }
   }

   private void TipoOuVoid() {
       if (debug) System.out.println("TipoOuVoid: laToken = " + laToken);
       if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || laToken == VOID) {
           verifica(laToken);
       } else {
           yyerror("Esperado tipo ou VOID");
       }
   }

   private void FormalPar() {
       if (debug) System.out.println("FormalPar: laToken = " + laToken);
       if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
           paramList();
       }
   }

   private void paramList() {
       if (debug) System.out.println("paramList: laToken = " + laToken);
       Tipo();
       verifica(IDENT);
       if (laToken == ',') {
           verifica(',');
           paramList();
       }
   }

   private void ListaCmd() {
       if (debug) System.out.println("ListaCmd: laToken = " + laToken);
       if (laToken == '{' || laToken == WHILE || laToken == IDENT || laToken == IF) {
           Cmd();
           ListaCmd();
       }
   }

   private void Bloco() {
       if (debug) System.out.println("Bloco --> { ListaCmd }");
       verifica('{');
       ListaCmd();
       verifica('}');
   }

   private void Cmd() {
       if (debug) System.out.println("Cmd: laToken = " + laToken);
       if (laToken == '{') {
           Bloco();
       } else if (laToken == WHILE) {
           verifica(WHILE);
           verifica('(');
           E();
           verifica(')');
           Cmd();
       } else if (laToken == IDENT) {
           verifica(IDENT);
           verifica('=');
           E();
           verifica(';');
       } else if (laToken == IF) {
           verifica(IF);
           verifica('(');
           E();
           verifica(')');
           Cmd();
           RestoIf();
       }
   }

   private void RestoIf() {
       if (debug) System.out.println("RestoIf: laToken = " + laToken);
       if (laToken == ELSE) {
           verifica(ELSE);
           Cmd();
       }
   }

   private void E() {
       if (debug) System.out.println("E: laToken = " + laToken);
       T();
       while (laToken == '+' || laToken == '-') {
           verifica(laToken);
           T();
       }
   }

   private void T() {
       if (debug) System.out.println("T: laToken = " + laToken);
       F();
       while (laToken == '*' || laToken == '/') {
           verifica(laToken);
           F();
       }
   }

   private void F() {
       if (debug) System.out.println("F: laToken = " + laToken);
       if (laToken == IDENT) {
           verifica(IDENT);
       } else if (laToken == NUM) {
           verifica(NUM);
       } else if (laToken == '(') {
           verifica('(');
           E();
           verifica(')');
       } else {
           yyerror("Esperado identificador, número ou expressão entre parênteses");
       }
   }

   private void verifica(int expected) {
       if (debug) System.out.println("verifica: laToken = " + laToken + ", expected = " + expected);
       if (laToken == expected)
           laToken = this.yylex();
       else
           yyerror("Token inesperado");
   }

   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0);
           retVal = lexer.yylex();
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
       }
       return retVal;
   }

   public void yyerror(String error) {
       System.err.println("Erro: " + error);
       System.exit(1);
   }

   public void setDebug(boolean trace) {
       debug = trace;
   }

   public static void main(String[] args) {
       try {
           AsdrSample parser = args.length == 0 ? new AsdrSample(new InputStreamReader(System.in)) : new AsdrSample(new FileReader(args[0]));
           parser.setDebug(true);
           laToken = parser.yylex();
           parser.Prog();
           if (laToken == Yylex.YYEOF)
               System.out.println("\n\nSucesso!");
           else
               System.out.println("Esperado EOF, mas encontrou: " + laToken);
       } catch (FileNotFoundException e) {
           System.out.println("Arquivo não encontrado: " + args[0]);
       }
   }
}
