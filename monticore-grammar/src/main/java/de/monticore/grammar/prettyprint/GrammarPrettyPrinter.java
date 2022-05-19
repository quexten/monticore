/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;

import java.util.Iterator;

public class GrammarPrettyPrinter implements GrammarVisitor2, GrammarHandler {

  protected static final String QUOTE = "\"";

  protected GrammarTraverser traverser;
  
  protected IndentPrinter printer;

  public GrammarPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    printer.setIndentLength(2);
  }

  @Override
  public void handle(ASTSemanticpredicateOrAction a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isPresentExpressionPredicate()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getExpressionPredicate().accept(getTraverser());
      getPrinter().unindent();
      print("}");
      print(" ?");
    }
    if (a.isPresentAction()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getAction().accept(getTraverser());
      getPrinter().unindent();
      print("}");
    }

    getPrinter().print(" ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTExternalProd a) {

    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printList(a.getGrammarAnnotationList().iterator(), "");
    print("external ");

    printList(a.getSymbolDefinitionList().iterator(), " ");
    getPrinter().print(a.getName());

    if (a.isPresentMCType()) {
      a.getMCType().accept(getTraverser());
    }
    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();

  }

  @Override
  public void handle(ASTGrammarOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    println("options {");
    getPrinter().indent();

    printList(a.getFollowOptionList().iterator(), "");

    getPrinter().unindent();
    print("}");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
    println();

  }

  @Override
  public void handle(ASTNonTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
    }

    print(a.getName());
    if (a.isPresentReferencedSymbol()) {
      print("@");
      print(a.getReferencedSymbol());
    }

    if (a.isGenSymbol()) {
      print("!!");
      if (a.isPresentSymbolName()) {
        print(a.getSymbolName());
      }
    }

    if (a.isPlusKeywords()) {
      print("& ");
    }

    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTKeyTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
    }
    a.getKeyConstant().accept(getTraverser());
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTKeyConstant a) {
    print(" key(");
    String sep = "";
    for (String name: a.getStringList()) {
      print(sep);
      print("\"" + name + "\"");
      sep = " | ";
    }
    print(")");
  }

  @Override
  public void handle(ASTTokenTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
    }
    a.getTokenConstant().accept(getTraverser());
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTokenConstant a) {
    print(" token(");
    print(a.getString());
    print(")");
  }

  @Override
  public void handle(ASTSplitRule a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("split_token ");
    String sep = "";
    for (String s: a.getStringList()) {
      print(sep);
      sep = ", ";
      print(s);
    }
    println (";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    // output("ASTTerminal Iteration " + a.getIteration());
    if (a.isPresentUsageName()) {
      print("" + a.getUsageName() + ":");
    }
    print("\"" + a.getName() + "\"");
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  @Override
  public void handle(ASTBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("(");

    if (a.isPresentOption()) {
      print("options {");

      for (ASTOptionValue x : a.getOption().getOptionValueList()) {
        print(x.getKey() + "=" + x.getValue() + ";");
      }

      print("} ");
    }

    if (a.isPresentInitAction()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getTraverser());
      getPrinter().unindent();
      print("}");
    }

    if (a.isPresentInitAction() || a.isPresentOption()) {
      print(": ");
    }

    printList(a.getAltList().iterator(), "| ");
    print(")");
    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    getPrinter().optionalBreak();

  }

  /**
   * Visiting an ASTConcept #not sure for complete children methods
   *
   * @param a
   */
  @Override
  public void handle(ASTConcept a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    println("concept " + a.getName() + "{ ");

    a.getConcept().accept(getTraverser());

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    println("}");
  }

  /**
   * #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTConstant a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentUsageName()) {
      print(a.getUsageName() + ":");
    }
    if (a.isPresentKeyConstant()) {
      a.getKeyConstant().accept(getTraverser());
    }else if (a.isPresentTokenConstant()) {
      a.getTokenConstant().accept(getTraverser());
    } else {
      print(QUOTE + a.getName() + QUOTE);
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * #complete children calls is usagename ever used??
   *
   * @param a
   */
  @Override
  public void handle(ASTConstantGroup a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentUsageName()) {
      print(a.getUsageName());
      print(":");
    }
    print("[");
    printList(a.getConstantList().iterator(), " | ");
    print("]");
    outputIteration(a.getIteration());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTAlt a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isRightAssoc()) {
      getPrinter().print(" <rightassoc> ");
    }
    if (a.isPresentGrammarAnnotation()) {
      a.getGrammarAnnotation().accept(getTraverser());
    }
    printList(a.getComponentList().iterator(), " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTInterfaceProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    printList(a.getGrammarAnnotationList().iterator(), "");
    print("interface ");

    printList(a.getSymbolDefinitionList().iterator(), " ");

    print(a.getName());

    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      getPrinter().print(" extends ");
      String comma = "";
      for (ASTRuleReference x : a.getSuperInterfaceRuleList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTMCType x : a.getASTSuperInterfaceList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getAltList().isEmpty()) {
      println(" =");

      getPrinter().indent();
      printList(a.getAltList().iterator(), " | ");
    }

    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTEnumProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printList(a.getGrammarAnnotationList().iterator(), "");
    print("enum ");
    print(a.getName());

    getPrinter().print(" = ");
    String sep = "";
    for (ASTConstant ref : a.getConstantList()) {
      print(sep);
      ref.accept(getTraverser());
      sep = " | ";
    }
    getPrinter().print(" ");

    getPrinter().print(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTASTRule a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("astrule ");

    print(a.getType());

    if (!a.getASTSuperClassList().isEmpty()) {
      getPrinter().print(" astextends ");
      String comma = "";
      for (ASTMCType x : a.getASTSuperClassList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astimplements ");
      String comma = "";
      for (ASTMCType x : a.getASTSuperInterfaceList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getGrammarMethodList().isEmpty() || !a.getAdditionalAttributeList().isEmpty()) {

      println(" = ");
      getPrinter().indent();
      printList(a.getAdditionalAttributeList().iterator(), "");
      printList(a.getGrammarMethodList().iterator(), "");
    }

    getPrinter().print(";");
    getPrinter().unindent();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();

  }

  @Override
  public void handle(ASTSymbolRule a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("symbolrule ");

    print(a.getType());

    if (!a.getSuperClassList().isEmpty()) {
      getPrinter().print(" extends ");
      String comma = "";
      for (ASTMCType x : a.getSuperClassList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astimplements ");
      String comma = "";
      for (ASTMCType x : a.getSuperInterfaceList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getGrammarMethodList().isEmpty() || !a.getAdditionalAttributeList().isEmpty()) {

      println(" = ");
      getPrinter().indent();
      printList(a.getAdditionalAttributeList().iterator(), "");
      printList(a.getGrammarMethodList().iterator(), "");
    }

    getPrinter().print(";");
    getPrinter().unindent();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTScopeRule a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("scoperule ");

    if (!a.getSuperClassList().isEmpty()) {
      getPrinter().print(" extends ");
      String comma = "";
      for (ASTMCType x : a.getSuperClassList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astimplements ");
      String comma = "";
      for (ASTMCType x : a.getSuperInterfaceList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }
    }

    if (!a.getGrammarMethodList().isEmpty() || !a.getAdditionalAttributeList().isEmpty()) {

      println(" = ");
      getPrinter().indent();
      printList(a.getAdditionalAttributeList().iterator(), "");
      printList(a.getGrammarMethodList().iterator(), "");
    }

    getPrinter().print(";");
    getPrinter().unindent();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
    getPrinter().println();

  }

  @Override
  public void handle(ASTGrammarMethod a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("method ");

    if (a.isFinal()) {
      print("final ");
    }
    if (a.isStatic()) {
      print("static ");
    }
    if (a.isPrivate()) {
      print("private ");
    }
    if (a.isPublic()) {
      print("public ");
    }
    if (a.isProtected()) {
      print("protected ");
    }

    a.getMCReturnType().accept(getTraverser());

    print(" " + a.getName() + "(");

    String comma = "";
    for (ASTMethodParameter x : a.getMethodParameterList()) {
      getPrinter().print(comma);
      x.getType().accept(getTraverser());
      getPrinter().print(" " + x.getName());
      comma = ", ";
    }

    print(")");

    if (!a.getExceptionList().isEmpty()) {

      print("throws ");
      comma = "";
      for (ASTMCType x : a.getExceptionList()) {
        getPrinter().print(comma);
        x.accept(getTraverser());
        comma = ", ";
      }

    }

    // a.getBody());
    print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getBody().accept(getTraverser());
    getPrinter().unindent();
    print("}");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();

  }

  @Override
  public void handle(ASTNonTerminalSeparator node) {
    if (node.isPresentUsageName()) {
      getPrinter().print(node.getUsageName());
      getPrinter().print(":");
    }
    getPrinter().print(" (");
    getPrinter().print(node.getName());
    if (node.isPresentReferencedSymbol()) {
      getPrinter().print("@");
      getPrinter().print(node.getReferencedSymbol());
    }
    if (node.isPlusKeywords()) {
      getPrinter().print("&");
    }
    getPrinter().print(" || \"");
    getPrinter().print(node.getSeparator());
    getPrinter().print("\" )");
    outputIteration(node.getIteration());
  }

  @Override
  public void visit(ASTMethodParameter a) {
    a.accept(getTraverser());
    print(a.getName());
  }

  @Override
  public void handle(ASTAdditionalAttribute a) {

    if (a.isPresentName()) {
      getPrinter().print(a.getName());
      getPrinter().print(":");
    }
    a.getMCType().accept(getTraverser());
    if (a.isPresentCard()) {
      ASTCard card = a.getCard();
      if (card.getIteration() != ASTConstantsGrammar.DEFAULT) {
        outputIteration(card.getIteration());
      }
      if (card.isPresentMin()) {
        print(" min = " + card.getMin());
      }
      if (card.isPresentMax()) {
        print(" max = " + card.getMax());
      }
    }
    println();
  }

  /**
   * Visiting an ASTRule #complete children calls
   *
   * @param a
   */
  @Override
  public void handle(ASTClassProd a) {

    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printList(a.getGrammarAnnotationList().iterator(), "");
    printList(a.getSymbolDefinitionList().iterator(), " ");
    getPrinter().print(a.getName());

    if (!a.getSuperRuleList().isEmpty()) {
      getPrinter().print(" extends ");
      printList(a.getSuperRuleList().iterator(), " ");
    }

    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      getPrinter().print(" implements ");
      printList(a.getSuperInterfaceRuleList().iterator(), ", ");
    }

    if (!a.getASTSuperClassList().isEmpty()) {
      getPrinter().print(" astextends ");
      printMCSimpleGenericList(a.getASTSuperClassList().iterator(), "");
    }

    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print(" astimplements ");
      printMCSimpleGenericList(a.getASTSuperInterfaceList().iterator(), ", ");
    }

    if (a.isPresentAction()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getAction().accept(getTraverser());
      getPrinter().unindent();
      print("}");
    }

    if (!a.getAltList().isEmpty()) {
      println(" =");

      getPrinter().indent();
      printList(a.getAltList().iterator(), " | ");
    }
    println(";");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().unindent();
    getPrinter().println();
  }

  /**
   * Visiting a LexRule #complete children calls
   *
   * @param a the LexRule
   */
  @Override
  public void handle(ASTLexProd a) {

    printList(a.getGrammarAnnotationList().iterator(), "");

    if (a.isFragment()) {
      print("fragment ");
    }
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("token ");

    println(a.getName());
    getPrinter().indent();

    if (a.isPresentMode()) {
      print("(");
      print(a.getMode());
      print(")");
    }

    if (a.isPresentLexOption()) {
      a.getLexOption().accept(getTraverser());
    }
    if (a.isPresentInitAction()) {
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getTraverser());
      getPrinter().unindent();
      print("}");
    }

    getPrinter().print("=");

    printList(a.getAltList().iterator(), " | ");

    if (a.isPresentLexerCommand() || a.isPresentEndAction() || a.isPresentVariable()) {
      getPrinter().print(" : ");

      if(a.isPresentLexerCommand()) {
        getPrinter().print("->");
        getPrinter().print(a.getLexerCommand());
      }

      if (a.isPresentEndAction()) {
        print(" {");
        getPrinter().println();
        getPrinter().indent();
        a.getEndAction().accept(getTraverser());
        getPrinter().unindent();
        print("}");
      }

      if (a.isPresentVariable()) {
        getPrinter().print(a.getVariable());

        if (!a.getTypeList().isEmpty()) {
          getPrinter().print("->");
          getPrinter().print(Names.getQualifiedName(a.getTypeList()));

          if (a.isPresentBlock()) {
            getPrinter().print(":");
            if (a.isPresentBlock()) {
              print(" {");
              getPrinter().println();
              getPrinter().indent();
              a.getBlock().accept(getTraverser());
              getPrinter().unindent();
              print("}");
            }
          }

        }

      }


    }


    print(";");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
    getPrinter().unindent();
    println();
  }

  @Override
  public void handle(ASTLexBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isNegate()) {
      getPrinter().print("~ ");
    }

    print("(");
    if (a.isPresentOption()) {
      print("options {");
      print(a.getOption().getID() + "=" + a.getOption().getValue() + ";");
      print("} ");
    }

    if (a.isPresentInitAction()) {
      getPrinter().print("init ");
      print(" {");
      getPrinter().println();
      getPrinter().indent();
      a.getInitAction().accept(getTraverser());
      getPrinter().unindent();
      print("}");
    }

    if (a.isPresentInitAction() || a.isPresentOption()) {
      print(": ");
    }

    printList(a.getLexAltList().iterator(), " | ");
    print(")");
    outputIteration(a.getIteration());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

    getPrinter().optionalBreak();

  }

  /**
   * Visit method for the ASTGrammar (the root object) we have to use the handle method because
   * neither the visit/endVisit nor the traverseOrder merhods allow us to visit Packagename before
   * the AstGrammar itself #complete children calls
   *
   * @param a The ASTGrammar
   */
  @Override
  public void handle(ASTMCGrammar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isPresentGrammarAnnotation()) {
      a.getGrammarAnnotation().accept(getTraverser());
    }

    if (!a.getPackageList().isEmpty()) {
      print("package ");
      print(Names.getQualifiedName(a.getPackageList()));
      println(";");
    }

    println();
    if (a.isComponent()) {
      print("component ");
    }
    print("grammar " + a.getName());

    if (!a.getSupergrammarList().isEmpty()) {
      print(" extends ");
      String comma = "";
      for (ASTGrammarReference sgrammar : a.getSupergrammarList()) {
        print(comma + Names.getQualifiedName(sgrammar.getNameList()));
        comma = ", ";
      }
    }
    println(" {");
    getPrinter().indent();
    if (a.isPresentGrammarOption()) {
      a.getGrammarOption().accept(getTraverser());
    }
    printList(a.getLexProdList().iterator(), "");
    printList(a.getClassProdList().iterator(), "");
    printList(a.getExternalProdList().iterator(), "");
    printList(a.getEnumProdList().iterator(), "");
    printList(a.getInterfaceProdList().iterator(), "");
    printList(a.getAbstractProdList().iterator(), "");
    printList(a.getASTRuleList().iterator(), "");
    printList(a.getConceptList().iterator(), "");
    if (a.isPresentStartRule()) {
      a.getStartRule().accept(getTraverser());
    }
    printList(a.getSymbolRuleList().iterator(), "");
    if (a.isPresentScopeRule()) {
      a.getScopeRule().accept(getTraverser());
    }

    getPrinter().unindent();
    print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());

    println();
  }

  // helper fuctions

  /**
   * returns the right String for the Iteration value
   *
   * @param i .getIteration() value
   */
  protected void outputIteration(int i) {
    if (i == ASTConstantsGrammar.QUESTION) {
      print("?");
    }
    else if (i == ASTConstantsGrammar.STAR) {
      print("*");
    }
    else if (i == ASTConstantsGrammar.PLUS) {
      print("+");
    }
    else {
      print("");
    }
  }

  protected void print(String o) {
    getPrinter().print(o);
  }

  protected void println(String o) {
    getPrinter().println(o);
  }

  protected void println() {
    getPrinter().println();
  }

  @Override
  public void handle(ASTLexAlt a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    String sep = "";
    for (ASTLexComponent c : a.getLexComponentList()) {
      print(sep);
      c.accept(getTraverser());
      sep = " ";
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLexChar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isNegate()) {
      getPrinter().print("~");
    }

    getPrinter().print("'" + a.getChar() + "'");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLexAnyChar a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print(".");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLexCharRange a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    if (a.isNegate()) {
      getPrinter().print("~");
    }

    getPrinter().print("'" + a.getLowerChar() + "'..");
    getPrinter().print("'" + a.getUpperChar() + "' ");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTRuleReference a) {
    if (a.isPresentSemanticpredicateOrAction()) {
      a.getSemanticpredicateOrAction().accept(getTraverser());
    }
    getPrinter().print(a.getName());
    if (a.isPresentPrio()) {
      getPrinter().print(" <" + a.getPrio() + "> ");
    }
  }

  public void handle(ASTLexString a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print("\"" + a.getString() + "\"");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void endVisit(ASTLexSimpleIteration a) {
    outputIteration(a.getIteration());
    if(a.isQuestion()) {
      getPrinter().print("?");
    }
  }

  @Override
  public void handle(ASTLexActionOrPredicate a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getExpressionPredicate().accept(getTraverser());
    getPrinter().unindent();
    print("}");

    if (a.isPredicate()) {
      print("?");
    }

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTLexNonTerminal a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print(a.getName());

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTLexOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    getPrinter().print("options ");

    getPrinter().print("{" + a.getID() + "=" + a.getValue() + ";}");

    CommentPrettyPrinter.printPostComments(a, getPrinter());

  }

  @Override
  public void handle(ASTAbstractProd a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());

    printList(a.getGrammarAnnotationList().iterator(), "");
    getPrinter().print("abstract ");
    printList(a.getSymbolDefinitionList().iterator(), " ");
    getPrinter().print(a.getName() + " ");
    if (!a.getSuperRuleList().isEmpty()) {
      getPrinter().print("extends ");
      printList(a.getSuperRuleList().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      getPrinter().print("implements ");
      printList(a.getSuperInterfaceRuleList().iterator(), ", ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperClassList().isEmpty()) {
      getPrinter().print("astextends ");
      printMCSimpleGenericList(a.getASTSuperClassList().iterator(), " ");
      getPrinter().print(" ");
    }
    if (!a.getASTSuperInterfaceList().isEmpty()) {
      getPrinter().print("astimplements ");
      printMCSimpleGenericList(a.getASTSuperInterfaceList().iterator(), ", ");
      getPrinter().print(" ");
    }

    if (!a.getAltList().isEmpty()) {
      println(" =");

      getPrinter().indent();
      printList(a.getAltList().iterator(), " | ");
    }

    getPrinter().println(";");

    CommentPrettyPrinter.printPostComments(a, getPrinter());
    getPrinter().println();
  }


  @Override
  public void handle(ASTFollowOption a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    print("follow " + a.getProdName() + " ");
    a.getAlt().accept(getTraverser());
    println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSymbolDefinition node) {
    if (node.isGenSymbol()) {
      getPrinter().print(" symbol ");
    }
    if (node.isGenScope()) {
      getPrinter().print(" scope ");
      if (node.isOrdered() || node.isShadowing() || node.isNon_exporting()) {
        getPrinter().print("(");
        if (node.isOrdered()) {
          getPrinter().print(" ordered ");
        }
        if (node.isShadowing()) {
          getPrinter().print(" shadowing ");
        }
        if (node.isNon_exporting()) {
          getPrinter().print(" non_exporting ");
        }
        getPrinter().print(")");
      }
    }
  }

  public void handle(ASTGrammarAnnotation node) {
    if (node.isOverride()) {
      getPrinter().println("@Override");
    } else if (node.isDeprecated()) {
      getPrinter().print("@Deprecated");
      if (node.isPresentMessage()) {
        getPrinter().print(("(\""));
        getPrinter().print(node.getMessage());
        getPrinter().print("\")");
      }
      getPrinter().println();
    }
  }


  @Override
  public void handle(ASTStartRule node) {
    getPrinter().println(" start " + node.getName() + ";");
  }

  public String prettyprint(ASTGrammarNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTGrammarNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = seperator;
    }
  }
  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printMCSimpleGenericList(Iterator<? extends ASTMCBasicTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = seperator;
    }
  }

  @Override
  public GrammarTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

}
