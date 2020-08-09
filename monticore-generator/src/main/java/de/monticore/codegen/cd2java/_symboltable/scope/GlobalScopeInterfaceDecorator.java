/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

<<<<<<< HEAD
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
=======
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import net.sourceforge.plantuml.Log;
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a globalScope interface from a grammar
 */
public class GlobalScopeInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;
=======
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a globalScope class from a grammar
 */
public class GlobalScopeInterfaceDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.iglobalscope.";

>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
  /**
   * flag added to define if the GlobalScope interface was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different getRealThis method implementations
   */
<<<<<<< HEAD
  protected boolean isGlobalScopeTop = false;

  protected static final String LOAD_MODELS_FOR = "loadModelsFor%s";

  protected static final String TEMPLATE_PATH = "_symboltable.iglobalscope.";

  public GlobalScopeInterfaceDecorator(final GlobalExtensionManagement glex,
                                       final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
=======
  protected boolean isGlobalScopeInterfaceTop = false;

  public GlobalScopeInterfaceDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService,
      final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceSimpleName();
<<<<<<< HEAD
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    String definitionName = input.getCDDefinition().getName();

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
=======

    List<ASTCDType> symbolClasses = symbolTableService
        .getSymbolDefiningProds(input.getCDDefinition());
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(globalScopeInterfaceName)
        .setModifier(PUBLIC.build())
<<<<<<< HEAD
        .addInterface(scopeInterfaceType)
        .addInterface(getMCTypeFacade().createQualifiedType(I_GLOBAL_SCOPE_TYPE))
        .addCDMethod(createGetLanguageMethod(definitionName))
        .addCDMethod(createCacheMethod())
        .addCDMethod(creatCheckIfContinueAsSubScopeMethod())
        .addCDMethod(createContinueWithModelLoaderMethod())
        .addCDMethod(createGetRealThisMethod(globalScopeInterfaceName))
        .addAllCDMethods(createResolveMethods(symbolClasses, definitionName))
        .addAllCDMethods(createSuperProdResolveMethods(definitionName))
        .build();
  }

  protected ASTCDMethod createCacheMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "cache", parameter);
  }

  protected ASTCDMethod createGetLanguageMethod(String definitionName) {
    ASTMCType languageType = getMCTypeFacade().createQualifiedType(definitionName + LANGUAGE_SUFFIX);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, languageType, "get" + definitionName + LANGUAGE_SUFFIX);
  }

  protected ASTCDMethod createContinueWithModelLoaderMethod() {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();
    ASTMCQualifiedType modelLoaderType = getMCTypeFacade().createQualifiedType(modelLoaderClassName);
    ASTCDParameter modelLoaderParameter = getCDParameterFacade().createParameter(modelLoaderType, MODEL_LOADER_VAR);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, getMCTypeFacade().createBooleanType(), "continueWithModelLoader", modelNameParameter, modelLoaderParameter);
  }

  protected ASTCDMethod creatCheckIfContinueAsSubScopeMethod() {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "symbolName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "checkIfContinueAsSubScope", modelNameParameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return false;"));
    return method;
  }

  protected ASTCDMethod createGetRealThisMethod(String globalScopeName) {
    ASTMCType globalScopeInterfaceType = getMCTypeFacade().createQualifiedType(globalScopeName);
    ASTCDMethod getRealThis = getCDMethodFacade().createMethod(PUBLIC, globalScopeInterfaceType, "getRealThis");
    if (isGlobalScopeTop()) {
      getRealThis.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, getRealThis, new StringHookPoint("return this;"));
    }
    return getRealThis;
  }

  /**
   * creates all resolve methods
   * reuses the often used parameters, so that they only need to be created once
   */
  protected List<ASTCDMethod> createResolveMethods(List<? extends ASTCDType> symbolProds, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      resolveMethods.addAll(createResolveMethods(symbolProd, nameParameter, foundSymbolsParameter, accessModifierParameter,
          symbolTableService.getCDSymbol(), definitionName));
    }

    return resolveMethods;
  }

  protected List<ASTCDMethod> createResolveMethods(ASTCDType symbolProd, ASTCDParameter nameParameter, ASTCDParameter foundSymbolsParameter,
                                                   ASTCDParameter accessModifierParameter, CDDefinitionSymbol cdDefinitionSymbol, String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    String className = symbolTableService.removeASTPrefix(symbolProd);
    String symbolFullTypeName = symbolTableService.getSymbolFullName(symbolProd, cdDefinitionSymbol);
    ASTMCType listSymbol = getMCTypeFacade().createListTypeOf(symbolFullTypeName);

    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, symbolFullTypeName), PREDICATE_VAR);

    resolveMethods.add(createResolveManyMethod(className, symbolFullTypeName, listSymbol, foundSymbolsParameter,
        nameParameter, accessModifierParameter, predicateParameter));

    resolveMethods.add(createResolveAdaptedMethod(className, listSymbol, foundSymbolsParameter,
        nameParameter, accessModifierParameter, predicateParameter));

    resolveMethods.add(createLoadModelsForMethod(className, definitionName, nameParameter));
    return resolveMethods;
  }

  protected List<ASTCDMethod> createSuperProdResolveMethods(String definitionName) {
    List<ASTCDMethod> resolveMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifierOpt().get())) {
          resolveMethods.addAll(createResolveMethods(type.getAstNode(), nameParameter, foundSymbolsParameter,
              accessModifierParameter, cdDefinitionSymbol, definitionName));
        }
      }
    }
    return resolveMethods;
  }

  protected ASTCDMethod createResolveManyMethod(String className, String fullSymbolName, ASTMCType returnType,
                                                ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    String methodName = String.format(RESOLVE_MANY, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveMany", className, fullSymbolName));
    return method;
  }

  protected ASTCDMethod createResolveAdaptedMethod(String className, ASTMCType returnType,
                                                   ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                   ASTCDParameter accessModifierParameter, ASTCDParameter predicateParameter) {
    String methodName = String.format(RESOLVE_ADAPTED, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return com.google.common.collect.Lists.newArrayList();"));
    return method;
  }

  protected ASTCDMethod createLoadModelsForMethod(String className, String definitionName,
                                                  ASTCDParameter nameParameter) {
    String methodName = String.format(LOAD_MODELS_FOR, className);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, nameParameter);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "LoadModelsFor", className, definitionName));
    return method;
  }

  public boolean isGlobalScopeTop() {
    return isGlobalScopeTop;
  }

  public void setGlobalScopeTop(boolean globalScopeTop) {
    isGlobalScopeTop = globalScopeTop;
  }
}


=======
        .addAllInterfaces(getSuperGlobalScopeInterfaces())
        .addInterface(symbolTableService.getScopeInterfaceType())
        .addAllCDMethods(createCalculateModelNameMethods(symbolClasses))
        .addCDMethod(createCacheMethod())
        .build();
  }

  private List<ASTMCQualifiedType> getSuperGlobalScopeInterfaces() {
    List<ASTMCQualifiedType> result = new ArrayList<>();
    for (CDDefinitionSymbol superGrammar : symbolTableService.getSuperCDsDirect()) {
      if(!superGrammar.isPresentAstNode()){
        Log.error("0xA4323 Unable to load AST of '" +superGrammar.getFullName()
            + "' that is supergrammar of '" + symbolTableService.getCDName() + "'.");
        continue;
      }
      if (symbolTableService.hasStartProd(superGrammar.getAstNode())
          ||!symbolTableService.getSymbolDefiningSuperProds().isEmpty() ) {
        result.add(symbolTableService.getGlobalScopeInterfaceType(superGrammar));
      }
    }
    if (result.isEmpty()) {
      result.add(getMCTypeFacade().createQualifiedType(I_GLOBAL_SCOPE_TYPE));
    }
    return result;
  }

  /**
   * This creates only an abstract method, because the implementation of the cache method requires
   * private attributes of the global scope class, such as e.g., the modelName2ModelLoaderCache
   * @return
   */
  protected ASTCDMethod createCacheMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    ASTCDMethod cacheMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "cache", parameter);
    return cacheMethod;
  }

  protected List<ASTCDMethod> createCalculateModelNameMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      String simpleName = symbolTableService.removeASTPrefix(symbolProd);
      ASTMCSetType setTypeOfString = getMCTypeFacade().createSetTypeOf(String.class);
      ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, NAME_VAR);
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, setTypeOfString,
          String.format("calculateModelNamesFor%s", simpleName), nameParam);
      this.replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint(TEMPLATE_PATH + "CalculateModelNamesFor"));
      methodList.add(method);
    }
    return methodList;
  }

  public boolean isGlobalScopeInterfaceTop() {
    return isGlobalScopeInterfaceTop;
  }

  public void setGlobalScopeInterfaceTop(boolean globalScopeInterfaceTop) {
    isGlobalScopeInterfaceTop = globalScopeInterfaceTop;
  }

}
>>>>>>> 4a140e4c5da5ecbc2be7c40ebe93937d04f19b8e
