
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/*

Prog -->  ListaDecl

ListaDecl -->  DeclVar  ListaDecl
            |  DeclFun  ListaDecl
            |  / vazio /

DeclVar --> Tipo ListaIdent ';' DeclVar
         | / vazio /

Tipo --> int | double | boolean

ListaIdent --> IDENT ListaIdentResto

ListaIdentResto --> , ListaIdent
                  | / vazio /

DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
         | / vazio /

TipoOuVoid --> Tipo | VOID

FormalPar -> paramList | / vazio /

paramList --> Tipo IDENT paramListResto

paramListResto --> , ParamList
                  | / vazio /

Bloco --> { ListaCmd }

ListaCmd --> Cmd ListaCmd
   |    / vazio /

Cmd --> Bloco
   | while ( E ) Cmd
   | IDENT = E ;
   | if ( E ) Cmd RestoIf

RestoIf -> else Cmd
      |    / vazio /

E --> T ER

ER -> + T
   | - T
   | / vazio /

T --> F TR

TR -> * F
    | / F
    | / vazio /
   
F -->  IDENT
   | NUM
   | ( E )

// Terminais:

IDENT
NUM
(
)
{
}
+
-
;
=
,
int
double
boolean
while
if
else
FUNC
VOID
 */
public class AsdrSample {

    private static final int BASE_TOKEN_NUM = 301;

    public static final int IDENT = 301;
    public static final int NUM = 302;
    public static final int WHILE = 303;
    public static final int IF = 304;
    public static final int ELSE = 305;
    public static final int INT = 306;
    public static final int DOUBLE = 307;
    public static final int BOOLEAN = 308;
    public static final int FUNC = 309;
    public static final int VOID = 310;

    public static final String tokenList[]
            = {"IDENT",
                "NUM",
                "WHILE",
                "IF",
                "ELSE",
                "INT",
                "DOUBLE",
                "BOOLEAN",
                "FUNC",
                "VOID"};

    /* referencia ao objeto Scanner gerado pelo JFLEX */
    private Yylex lexer;

    public ParserVal yylval;

    private static int laToken;
    private boolean debug;

    /* construtor da classe */
    public AsdrSample(Reader r) {
        lexer = new Yylex(r, this);
    }

    private void Prog() {
        if (debug) {
            System.out.println("Prog --> Bloco");
        }
        ListaDecl();
        // if (laToken == '{') {
        //    if (debug) System.out.println("Prog --> ListaDecl");
        //    ListaDecl();
        // }
        // else yyerror("esperado '{'");
    }

    private void ListaDecl() {
        if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
            if (debug) {
                System.out.println("ListaDecl --> DeclVar ListaDecl");
            }
            DeclVar();
            ListaDecl();
        } else if (laToken == FUNC) {
            if (debug) {
                System.out.println("ListaDecl --> DeclFunc ListaDecl");
            }
            DeclFunc();
            ListaDecl();
        } else {
            if (debug) {
                System.out.println("ListaDecl -->  (*vazio*)  ");
            }
        }
    }

    private void DeclVar() {
        if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
            if (debug) {
                System.out.println("DeclVar --> Tipo ListaIdent ';' DeclVar");
            }
            Tipo();
            ListaIdent();
            verifica(';');
            DeclVar();
        } else {
            if (debug) {
                System.out.println("DeclVar -->  (*vazio*)  ");
            }
        }
    }

    private void Tipo() {
        if (laToken == INT) {
            if (debug) {
                System.out.println("Tipo --> int");
            }
            verifica(INT);
        } else if (laToken == DOUBLE) {
            if (debug) {
                System.out.println("Tipo --> double");
            }
            verifica(DOUBLE);
        } else if (laToken == BOOLEAN) {
            if (debug) {
                System.out.println("Tipo --> boolean");
            }
            verifica(BOOLEAN);
        } else {
            yyerror("Esperado tipo de dado");
        }
    }

    private void ListaIdent() {
        if (laToken == IDENT) {
            if (debug) {
                System.out.println("ListaIdent --> IDENT ListaIdentResto");
            }
            verifica(IDENT);
            ListaIdentResto();
        } else {
            if (debug) {
                System.out.println("ListaIdent -->  (*vazio*)  ");
            }
        }
    }

    private void ListaIdentResto() {
        if (laToken == ',') {
            if (debug) {
                System.out.println("ListaIdentResto --> , ListaIdent");
            }
            verifica(',');
            ListaIdent();
        } else {
            if (debug) {
                System.out.println("ListaIdentResto -->  (*vazio*)  ");
            }
        }
    }

    private void DeclFunc() {
        if (laToken == FUNC) {
            if (debug) {
                System.out.println("DeclFunc --> FUNC TipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFunc");
            }
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
            DeclFunc();
        } else {
            if (debug) {
                System.out.println("DeclFunc -->  (*vazio*)  ");
            }
        }
    }

    private void TipoOuVoid() {
        if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
            if (debug) {
                System.out.println("TipoOuVoid --> Tipo");
            }
            Tipo();
        } else if (laToken == VOID) {
            if (debug) {
                System.out.println("TipoOuVoid --> VOID");
            }
            verifica(VOID);
        }
    }

    private void FormalPar() {
        if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
            if (debug) {
                System.out.println("FormalPar --> Tipo IDENT");
            }
            Tipo();
            verifica(IDENT);
        } else {
            if (debug) {
                System.out.println("FormalPar -->  (*vazio*)  ");
            }
        }
    }

    private void ParamList() {
        if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
            if (debug) {
                System.out.println("ParamList --> Tipo IDENT paramListResto");
            }
            Tipo();
            verifica(IDENT);
            ParamListResto();
        } else {
            yyerror("Esperado tipo de dado");
        }
    }

    private void ParamListResto() {
        if (laToken == ',') {
            if (debug) {
                System.out.println("ParamListResto --> , ParamList");
            }
            verifica(',');
            ParamList();
        } else {
            if (debug) {
                System.out.println("ParamListResto -->  (*vazio*)  ");
            }
        }
    }

    private void Bloco() {
        if (laToken == '{') {
            if (debug) {
                System.out.println("Bloco --> { ListaCmd }");
            }
            verifica('{');
            ListaCmd();
            verifica('}');
        } else {
            yyerror("Esperado '{'");
        }
    }

    private void ListaCmd() {
        if (laToken == '{' || laToken == WHILE || laToken == IDENT || laToken == IF || laToken == IDENT) {
            if (debug) {
                System.out.println("ListaCmd --> Cmd ListaCmd");
            }
            Cmd();
            ListaCmd();
        } else {
            if (debug) {
                System.out.println("ListaCmd -->  (*vazio*)  ");
            }
        }
    }

    private void Cmd() {
        if (laToken == '{') {
            if (debug) {
                System.out.println("Cmd --> Bloco");
            }
            Bloco();
        } else if (laToken == WHILE) {
            if (debug) {
                System.out.println("Cmd --> while ( E ) Cmd");
            }
            verifica(WHILE);
            verifica('(');
            E();
            verifica(')');
            Cmd();
        } else if (laToken == IDENT) {
            if (debug) {
                System.out.println("Cmd --> IDENT = E ;");
            }
            verifica(IDENT);
            verifica('=');
            E();
            verifica(';');
        } else if (laToken == IF) {
            if (debug) {
                System.out.println("Cmd --> if (E) Cmd RestoIF");
            }
            verifica(IF);
            verifica('(');
            E();
            verifica(')');
            Cmd();
            RestoIF();
        } else {
            yyerror("Esperado {, if, while ou identificador");
        }
    }

    private void RestoIF() {
        if (laToken == ELSE) {
            if (debug) {
                System.out.println("RestoIF --> else Cmd");
            }
            verifica(ELSE);
            Cmd();
        } else {
            if (debug) {
                System.out.println("RestoIF -->  (*vazio*)  ");
            }
        }
    }

    private void E() {
        T();
        ER();
        // if (laToken == IDENT || laToken == NUM || laToken == '(') {
        //     if (debug) {
        //         System.out.println("E --> T R");
        //     }
        //     T();
        //     R();
        // } else {
        //     yyerror("Esperado operando (, identificador ou numero");
        // }
    }

    private void ER() {
        if (laToken == '+') {
            verifica('+');
            T();
            ER();
        } else if (laToken == '-') {
            verifica('-');
            T();
            ER();
        } else {
            if (debug) {
                System.out.println("ER -->  (*vazio*)  ");
            }
        }
    }

    private void T() {
        F();
        TR();
    }

    private void TR() {
        if (laToken == '*') {
            verifica('*');
            F();
            TR();
        } else if (laToken == '/') {
            verifica('/');
            F();
            TR();
        } else {
            if (debug) {
                System.out.println("TR -->  (*vazio*)  ");
            }
        }
    }

    private void F() {
        if (laToken == IDENT) {
            verifica(IDENT);
        } else if (laToken == NUM) {
            verifica(NUM);
        } else if (laToken == '(') {
            verifica('(');
            E();
            verifica(')');
        } else {
            yyerror("Esperado operando (, identificador ou numero");
        }
    }

    private void verifica(int expected) {
        if (laToken == expected) {
            laToken = this.yylex();
        } else {
            String expStr, laStr;

            expStr = ((expected < BASE_TOKEN_NUM)
                    ? "" + (char) expected
                    : tokenList[expected - BASE_TOKEN_NUM]);

            laStr = ((laToken < BASE_TOKEN_NUM)
                    ? Character.toString(laToken)
                    : tokenList[laToken - BASE_TOKEN_NUM]);

            yyerror("esperado token: " + expStr
                    + " na entrada: " + laStr);
        }
    }

    /* metodo de acesso ao Scanner gerado pelo JFLEX */
    private int yylex() {
        int retVal = -1;
        try {
            yylval = new ParserVal(0); //zera o valor do token
            retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
        } catch (IOException e) {
            System.err.println("IO Error:" + e);
        }
        return retVal; //retorna o token para o Parser 
    }

    /* metodo de manipulacao de erros de sintaxe */
    public void yyerror(String error) {
        System.err.println("Erro: " + error);
        System.err.println("Entrada rejeitada");
        System.out.println("\n\nFalhou!!!");
        System.exit(1);

    }

    public void setDebug(boolean trace) {
        debug = true;
    }

    /**
     * Runs the scanner on input files.
     *
     * This main method is the debugging routine for the scanner. It prints
     * debugging information about each returned token to System.out until the
     * end of file is reached, or an error occured.
     *
     * @param args the command line, contains the filenames to run the scanner
     * on.
     */
    public static void main(String[] args) {
        AsdrSample parser = null;
        try {
            if (args.length == 0) {
                parser = new AsdrSample(new InputStreamReader(System.in));
            } else {
                parser = new AsdrSample(new java.io.FileReader(args[0]));
            }

            parser.setDebug(false);
            laToken = parser.yylex();

            parser.Prog();

            if (laToken == Yylex.YYEOF) {
                System.out.println("\n\nSucesso!");
            } else {
                System.out.println("\n\nFalhou - esperado EOF.");
            }

        } catch (java.io.FileNotFoundException e) {
            System.out.println("File not found : \"" + args[0] + "\"");
        }
//        catch (java.io.IOException e) {
//          System.out.println("IO error scanning file \""+args[0]+"\"");
//          System.out.println(e);
//        }
//        catch (Exception e) {
//          System.out.println("Unexpected exception:");
//          e.printStackTrace();
//      }

    }

}
