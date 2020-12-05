/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typecd2java;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.simpleName;

public class TypeCD2JavaVisitor implements Grammar_WithConceptsVisitor {

  private static final String PACKAGE_SEPARATOR = "\\.";

  protected ICD4AnalysisScope scope;

  public TypeCD2JavaVisitor(ICD4AnalysisScope scope) {
    this.scope = scope;
  }

  private TypeCD2JavaVisitor() {
  }

  @Override
  public void visit(ASTMCQualifiedType node) {
    //only take first one because at first the type has just one name which contains the complete qualified name
    //e.g. "de.monticore.Automaton.ASTAutomaton"
    Optional<CDTypeSymbol> typeSymbol = scope.resolveCDType(String.join(".", node.getNameList()));
    if (typeSymbol.isPresent()) {
      ArrayList<String> l = Lists.newArrayList();
      for (String name: node.getNameList()) {
        l.add(name.toLowerCase());
      }
      l.remove(node.getNameList().size()-1);
      l.add( ASTConstants.AST_PACKAGE);
      l.add(simpleName(node));
      node.getMCQualifiedName().setPartsList(l);
    }
    if(node.getNameList().size() <= 1){
      node.getMCQualifiedName().setPartsList(new ArrayList<>(Arrays.asList(node.getNameList().get(0).split(PACKAGE_SEPARATOR))));
    }
  }

  // TODO Lösche diese Methoden, wenn ein neues CD4A benutzt wird.
  @Override
  public void endVisit(ISymbol node) {
  }

  @Override
  public void endVisit(IScope node) {
  }

  @Override
  public void visit(ISymbol node) {
  }

  @Override
  public void visit(IScope node) {
  }

}
