/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Checks if the source rules were extending other rules and sets the super
 * classes / extended interfaces of the target nodes accordingly.
 */
public class DeprecatedTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(
            ASTAbstractProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTExternalProd, ASTCDClass> link : rootLink.getLinks(
            ASTExternalProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(
            ASTInterfaceProd.class, ASTCDInterface.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTEnumProd, ASTCDEnum> link : rootLink.getLinks(
            ASTEnumProd.class, ASTCDEnum.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  protected void translateProd(ASTProd prod, ASTCDType cdType,
                               ASTMCGrammar astGrammar) {
    List<ASTGrammarAnnotation> annos = prod.getGrammarAnnotationList().stream().filter(a -> a.isDeprecated()).collect(Collectors.toList());
    String message;
    if (annos.isEmpty()) {
      if (!astGrammar.isPresentGrammarAnnotation() || !astGrammar.getGrammarAnnotation().isDeprecated()) {
        return;
      }
      message = astGrammar.getGrammarAnnotation().isPresentMessage()?astGrammar.getGrammarAnnotation().getMessage():"";
    } else {
      message = annos.get(0).isPresentMessage()?astGrammar.getGrammarAnnotation().getMessage():"";
    }
    if (!message.isEmpty()) {
      TransformationHelper.addStereoType(cdType, MC2CDStereotypes.DEPRECATED.toString(),
              message);
    } else {
      TransformationHelper.addStereoType(cdType, MC2CDStereotypes.DEPRECATED.toString());
    }
  }

}
