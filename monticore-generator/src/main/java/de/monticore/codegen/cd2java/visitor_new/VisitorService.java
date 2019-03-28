package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.List;
import java.util.stream.Collectors;

public class VisitorService extends AbstractService {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }

  public String getVisitorSimpleTypeName() {
    return getCDName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getVisitorFullTypeName() {
    return getPackage() + "." + getVisitorSimpleTypeName();
  }

  public ASTType getVisitorType() {
    return getCDTypeFactory().createSimpleReferenceType(getVisitorFullTypeName());
  }

  public ASTType getVisitorType(CDSymbol cd) {
    return getCDTypeFactory().createSimpleReferenceType(String.join(".", getPackage(cd), cd.getName() + VisitorConstants.VISITOR_SUFFIX));
  }

  public List<ASTType> getAllVisitorTypesInHierarchy() {
    return SuperSymbolHelper.getSuperCDs(getCD()).stream()
        .map(this::getVisitorType)
        .collect(Collectors.toList());
  }
}
