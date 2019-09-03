package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class StartProdTranslation  implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    MCGrammarSymbol grammarSymbol = rootLink.source().getMCGrammarSymbol();
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      if (link.source().getSymbol().isStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class)) {
      if (link.source().getSymbol().isStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class, ASTCDInterface.class)) {
      if (link.source().getSymbol().isStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }
    return rootLink;
  }
}
