/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.utils.Link;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class InheritedAttributesTranslation implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {
      //inherited
      handleInheritedRuleComponents(link);
      handleInheritedAttributeInASTs(link);
      //overwritten
      Optional<ASTProd> overwrittenProdIfNoNewRightSide = getOverwrittenProdIfNoNewRightSide(link.source());
      overwrittenProdIfNoNewRightSide.ifPresent(astProd -> handleOverwrittenRuleComponents(link, astProd));
    }
    return rootLink;
  }

  /**
   * handleInherited method for each RuleComponent type
   */
  private void handleInheritedRuleComponents(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, List<ASTRuleComponent>> entry : getInheritedRuleComponents(link.source())
            .entrySet()) {
      handleInheritedRuleComponents(link, entry.getKey(), entry.getValue());
    }
  }

  private void handleInheritedRuleComponents(Link<ASTClassProd, ASTCDClass> link, ASTProd astProd,
                                             List<? extends ASTRuleComponent> ruleComponents) {
    for (ASTRuleComponent ruleComponent : ruleComponents) {
      if ((ruleComponent instanceof ASTNonTerminal)
       ||(ruleComponent instanceof ASTConstantGroup)
       || ((ruleComponent instanceof ASTITerminal) && ((ASTITerminal) ruleComponent).isPresentUsageName())) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), astProd);
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(ruleComponent, cdAttribute, link);
      }
    }
  }

  /**
   * handleInherited method for astrules
   */
  private void handleInheritedAttributeInASTs(Link<ASTClassProd, ASTCDClass> link) {
    for (Entry<ASTProd, Collection<AdditionalAttributeSymbol>> entry : getInheritedAttributeInASTs(
            link.source()).entrySet()) {
      for (AdditionalAttributeSymbol attributeInAST : entry.getValue()) {
        ASTCDAttribute cdAttribute = createCDAttribute(link.source(), entry.getKey());
        link.target().getCDAttributeList().add(cdAttribute);
        if (attributeInAST.isPresentAstNode()) {
          new Link<>(attributeInAST.getAstNode(), cdAttribute, link);
        }
      }
    }
  }

  /**
   * Methods to get special RuleComponent Types for a Prod
   */
  private Map<ASTProd, List<ASTRuleComponent>> getInheritedRuleComponents(ASTProd sourceNode) {
    return TransformationHelper.getAllSuperProds(sourceNode).stream()
            .distinct()
            .collect(Collectors.toMap(Function.identity(),
                    astProd -> TransformationHelper.getAllComponents(astProd)));
  }


  /**
   * all attributes from a astrule for a Prod
   */
  protected Map<ASTProd, Collection<AdditionalAttributeSymbol>> getInheritedAttributeInASTs(
          ASTProd astNode) {
    return TransformationHelper.getAllSuperProds(astNode).stream()
            .distinct()
            .collect(Collectors.toMap(Function.identity(), prod -> prod.isPresentSymbol() ?
                    prod.getSymbol().getSpannedScope().getAstAttributeList() : Collections.emptyList()));
  }

  /**
   * create Attribute with a inherited flag
   */
  protected ASTCDAttribute createCDAttribute(ASTProd inheritingNode, ASTProd definingNode) {
    List<ASTInterfaceProd> interfacesWithoutImplementation = getAllInterfacesWithoutImplementation(
            inheritingNode);

    String superGrammarName = MCGrammarSymbolTableHelper.getMCGrammarSymbol(definingNode.getEnclosingScope())
            .map(MCGrammarSymbol::getFullName)
            .orElse("");

    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    if (!interfacesWithoutImplementation.contains(definingNode)) {
      TransformationHelper.addStereoType(
              cdAttribute, MC2CDStereotypes.INHERITED.toString(), superGrammarName);
    }
    return cdAttribute;
  }

  /**
   * handleOverwritten method for each RuleComponent type
   */
  private void handleOverwrittenRuleComponents(Link<ASTClassProd, ASTCDClass> link, ASTProd superProd) {
    List<ASTRuleComponent> overwrittenComponents = TransformationHelper.getAllComponents(superProd)
            .stream()
            .filter(c -> !(c instanceof ASTTerminal) || ((ASTTerminal) c).isPresentUsageName())
            .collect(Collectors.toList());
    handleInheritedRuleComponents(link, superProd, overwrittenComponents);
  }

  /**
   * checks if the Prod is overwriting a Prod of the super grammar with the same name
   * and does not change the implementation by not adding a right hand side
   * e.g. Prod Foo
   * grammar A { Foo = Name;}
   * grammar B extends A {interface Bar; Foo implements Bar;}
   */
  private Optional<ASTProd> getOverwrittenProdIfNoNewRightSide(ASTClassProd astProd) {
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper
            .resolveRuleInSupersOnly(
                    astProd,
                    astProd.getName());
    if (ruleSymbol.isPresent() && !ruleSymbol.get().isIsExternal()
            && ruleSymbol.get().isPresentAstNode()
            && TransformationHelper.getAllComponents(astProd).isEmpty()) {
      return Optional.of(ruleSymbol.get().getAstNode());
    }
    return Optional.empty();
  }

  /**
   * @return a list of interfaces that aren't already implemented by another
   * class higher up in the type hierarchy. (the list includes interfaces
   * extended transitively by other interfaces)
   */
  protected List<ASTInterfaceProd> getAllInterfacesWithoutImplementation(ASTProd astNode) {
    List<ASTInterfaceProd> directInterfaces = TransformationHelper.getDirectSuperProds(astNode).stream()
            .filter(ASTInterfaceProd.class::isInstance)
            .map(ASTInterfaceProd.class::cast)
            .collect(Collectors.toList());
    List<ASTInterfaceProd> allSuperRules = new ArrayList<>();
    for (ASTInterfaceProd superInterface : directInterfaces) {
      allSuperRules.addAll(getAllInterfacesWithoutImplementation(superInterface));
    }
    allSuperRules.addAll(directInterfaces);
    return allSuperRules;
  }
}
