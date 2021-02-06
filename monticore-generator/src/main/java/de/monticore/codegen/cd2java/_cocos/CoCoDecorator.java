/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

/**
 * combines the CoCo checker class and all CoCo interfaces to create the _coco package
 */
public class CoCoDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final CoCoCheckerDecorator cocoCheckerDecorator;

  protected final CoCoInterfaceDecorator cocoInterfaceDecorator;

  protected final IterablePath handCodedPath;

  public CoCoDecorator(final GlobalExtensionManagement glex,
                       final IterablePath handCodedPath,
                       final CoCoCheckerDecorator cocoCheckerDecorator,
                       final CoCoInterfaceDecorator cocoInterfaceDecorator) {
    super(glex);
    this.cocoCheckerDecorator = cocoCheckerDecorator;
    this.cocoInterfaceDecorator = cocoInterfaceDecorator;
    this.handCodedPath = handCodedPath;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> cocoPackage = Lists.newArrayList();
    input.getPackageList().forEach(p -> cocoPackage.add(p.toLowerCase()));
    cocoPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), CoCoConstants.COCO_PACKAGE));

    ASTCDDefinition cocoCD = CD4AnalysisMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDClass(cocoCheckerDecorator.decorate(input))
        .addAllCDInterfaces(cocoInterfaceDecorator.decorate(input.getCDDefinition()))
        .build();

    // change the package to _coco
    for (ASTCDClass ast : cocoCD.getCDClassList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
      if (ast.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, ast, createAnnotationsHookPoint(ast.getModifier()));
      }
    }

    for (ASTCDInterface ast : cocoCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, ast, createPackageHookPoint(cocoPackage));
      if (ast.isPresentModifier()) {
        this.replaceTemplate(ANNOTATIONS, ast, createAnnotationsHookPoint(ast.getModifier()));
      }
    }

    return CD4AnalysisMill.cDCompilationUnitBuilder()
        .setPackageList(cocoPackage)
        .setCDDefinition(cocoCD)
        .build();
  }
}
