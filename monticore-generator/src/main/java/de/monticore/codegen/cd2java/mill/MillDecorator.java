/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.DELEGATOR_SUFFIX;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.mill.MillConstants.*;
import static de.monticore.codegen.cd2java.top.TopDecorator.TOP_SUFFIX;

/**
 * created mill class for a grammar
 */
public class MillDecorator extends AbstractCreator<List<ASTCDCompilationUnit>, ASTCDClass> {

  protected final SymbolTableService symbolTableService;
  protected final VisitorService visitorService;
  protected final ParserService parserService;

  public MillDecorator(final GlobalExtensionManagement glex,
                       final SymbolTableService symbolTableService,
                       final VisitorService visitorService,
                       final ParserService parserService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.parserService = parserService;
  }

  public ASTCDClass decorate(final List<ASTCDCompilationUnit> cdList) {
    String millClassName = symbolTableService.getMillSimpleName();
    ASTMCType millType = this.getMCTypeFacade().createQualifiedType(millClassName);

    String fullDefinitionName = symbolTableService.getCDSymbol().getFullName();

    List<CDDefinitionSymbol> superSymbolList = symbolTableService.getSuperCDsTransitive();

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(PROTECTED, millClassName);

    ASTCDAttribute millAttribute = this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX);
    // add all standard methods
    ASTCDMethod getMillMethod = addGetMillMethods(millType);
    ASTCDMethod initMethod = addInitMethod(millType, superSymbolList, fullDefinitionName);

    ASTCDClass millClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .addCDAttribute(millAttribute)
        .addCDConstructor(constructor)
        .addCDMethod(getMillMethod)
        .addCDMethod(initMethod)
        .build();

    // list of all classes needed for the reset and initMe method
    List<ASTCDClass> allClasses = new ArrayList<>();

    for (ASTCDCompilationUnit cd : cdList) {
      // filter out all classes that are abstract and only builder classes
      List<ASTCDClass> classList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> !x.getModifier().isAbstract())
          .filter(this::checkIncludeInMill)
          .collect(Collectors.toList());


      // filter out all classes that are abstract and end with the TOP suffix
      List<ASTCDClass> topClassList = cd.getCDDefinition().deepClone().getCDClassList()
          .stream()
          .filter(ASTCDClass::isPresentModifier)
          .filter(x -> x.getModifier().isAbstract())
          .filter(x -> x.getName().endsWith(TOP_SUFFIX))
          .collect(Collectors.toList());
      // remove TOP suffix
      topClassList.forEach(x -> x.setName(x.getName().substring(0, x.getName().length() - 3)));
      // check if builder classes
      topClassList = topClassList
          .stream()
          .filter(this::checkIncludeInMill)
          .collect(Collectors.toList());
      // add to classes which need a builder method
      classList.addAll(topClassList);

      // add to all class list for reset and initMe method
      allClasses.addAll(classList);

      // add mill attribute for each class
      List<ASTCDAttribute> attributeList = new ArrayList<>();
      for (String attributeName : getAttributeNameList(classList)) {
        attributeList.add(this.getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + attributeName));
      }

      //remove the methods that are generated in the code below the for-loop
      classList = classList.stream().filter(this::checkNotGeneratedSpecifically).collect(Collectors.toList());
      List<ASTCDMethod> builderMethodsList = addBuilderMethods(classList, cd);

      millClass.addAllCDAttributes(attributeList);
      millClass.addAllCDMethods(builderMethodsList);
    }
    
    // decorate for traverser
    List<ASTCDMethod> traverserMethods = getAttributeMethods(visitorService.getTraverserSimpleName(),
        visitorService.getTraverserFullName(), TRAVERSER, visitorService.getTraverserInterfaceFullName());
    millClass.addAllCDMethods(traverserMethods);

    // decorate for traverser with InheritanceHandler
    List<ASTCDMethod> traverserInheritanceMethods = getInheritanceTraverserMethods(visitorService.getInheritanceHandlerSimpleName(),
            visitorService.getTraverserFullName(), INHERITANCE_TRAVERSER, visitorService.getTraverserInterfaceFullName());
    millClass.addAllCDMethods(traverserInheritanceMethods);

    // decorate for global scope
    //globalScope
    String globalScopeAttributeName = StringTransformations.uncapitalize(symbolTableService.getGlobalScopeSimpleName());
    ASTCDAttribute globalScopeAttribute = getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getGlobalScopeInterfaceType(),globalScopeAttributeName);
    List<ASTCDMethod> globalScopeMethods = getGlobalScopeMethods(globalScopeAttribute);
    millClass.addCDAttribute(globalScopeAttribute);
    millClass.addAllCDMethods(globalScopeMethods);

    //artifactScope
    millClass.addAllCDMethods(getArtifactScopeMethods());


    if(!symbolTableService.hasComponentStereotype(symbolTableService.getCDSymbol().getAstNode())) {
      ASTCDAttribute parserAttribute = getCDAttributeFacade().createAttribute(PROTECTED_STATIC, millType, MILL_INFIX + parserService.getParserClassSimpleName());
      List<ASTCDMethod> parserMethods = getParserMethods();
      millClass.addCDAttribute(parserAttribute);
      millClass.addAllCDMethods(parserMethods);
      allClasses.add(CD4AnalysisMill.cDClassBuilder().setName(parserService.getParserClassSimpleName()).build());
    }
    //scope
    millClass.addAllCDMethods(getScopeMethods());

    //decorate for scopesgenitor
    Optional<String> startProd = symbolTableService.getStartProdASTFullName();
    if (startProd.isPresent()){
      millClass.addAllCDMethods(getScopesGenitorMethods());

      millClass.addAllCDMethods(getScopesGenitorDelegatorMethods());
    }

    // add builder methods for each class
    List<ASTCDMethod> superMethodsList = addSuperBuilderMethods(superSymbolList, allClasses);
    millClass.addAllCDMethods(superMethodsList);

    ASTCDMethod initMeMethod = addInitMeMethod(millType, allClasses);
    millClass.addCDMethod(initMeMethod);

    ASTCDMethod resetMethod = addResetMethod(allClasses, superSymbolList);
    millClass.addCDMethod(resetMethod);

    return millClass;
  }

  /**
   * checks if the class is generated specifically, i.e. there are special methods for the parser, scope, artifactScope and globalScope
   * These special methods can also be seen in this class, e.g. {@link #getScopeMethods()}
   * if a new class does not need a specific mill implementation, normal methods will be generated for it
   */
  protected boolean checkNotGeneratedSpecifically(ASTCDClass cdClass){
    String name = cdClass.getName();
    String cdName = symbolTableService.getCDName();
    return !(name.endsWith(PARSER_SUFFIX)
        || name.endsWith(cdName + SCOPE_SUFFIX)
        || name.endsWith(cdName + ARTIFACT_PREFIX + SCOPE_SUFFIX)
        || name.endsWith(cdName + GLOBAL_SUFFIX + SCOPE_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(TRAVERSER_CLASS_SUFFIX)
        || name.endsWith(INHERITANCE_SUFFIX + HANDLER_SUFFIX));
  }

  /**
   * checks if a class should be included in the mill, example: serialization classes should not be included
   * so they are not mentioned in this method
   */
  protected boolean checkIncludeInMill(ASTCDClass cdClass){
    String name = cdClass.getName();
    String cdName = symbolTableService.getCDName();
    return name.endsWith(BUILDER_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX)
        || name.endsWith(SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(PARSER_SUFFIX)
        || name.endsWith(cdName + SCOPE_SUFFIX)
        || name.endsWith(cdName + ARTIFACT_PREFIX + SCOPE_SUFFIX)
        || name.endsWith(cdName + GLOBAL_SUFFIX + SCOPE_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX)
        || name.endsWith(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX)
        || name.endsWith(TRAVERSER_CLASS_SUFFIX)
        || name.endsWith(INHERITANCE_SUFFIX + HANDLER_SUFFIX);
  }

  protected List<String> getAttributeNameList(List<ASTCDClass> astcdClasses) {
    List<String> attributeNames = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      attributeNames.add(astcdClass.getName());
    }
    return attributeNames;
  }

  protected ASTCDMethod addGetMillMethods(ASTMCType millType) {
    ASTCDMethod getMillMethod = this.getCDMethodFacade().createMethod(PROTECTED_STATIC, millType, GET_MILL);
    this.replaceTemplate(EMPTY_BODY, getMillMethod, new TemplateHookPoint("mill.GetMillMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    return getMillMethod;
  }

  protected ASTCDMethod addInitMeMethod(ASTMCType millType, List<ASTCDClass> astcdClassList) {
    ASTCDParameter astcdParameter = getCDParameterFacade().createParameter(millType, "a");
    ASTCDMethod initMeMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT_ME, astcdParameter);
    this.replaceTemplate(EMPTY_BODY, initMeMethod, new TemplateHookPoint("mill.InitMeMethod", getAttributeNameList(astcdClassList)));
    return initMeMethod;
  }

  protected ASTCDMethod addInitMethod(ASTMCType millType, List<CDDefinitionSymbol> superSymbolList, String fullDefinitionName) {
    ASTCDMethod initMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, INIT);
    this.replaceTemplate(EMPTY_BODY, initMethod, new TemplateHookPoint("mill.InitMethod", millType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()), superSymbolList, fullDefinitionName+"."+AUXILIARY_PACKAGE));
    return initMethod;
  }

  protected ASTCDMethod addResetMethod(List<ASTCDClass> astcdClassList, List<CDDefinitionSymbol> superSymbolList) {
    ASTCDMethod resetMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, RESET);
    this.replaceTemplate(EMPTY_BODY, resetMethod, new TemplateHookPoint("mill.ResetMethod", getAttributeNameList(astcdClassList), superSymbolList));
    return resetMethod;
  }

  protected List<ASTCDMethod> addBuilderMethods(List<ASTCDClass> astcdClassList, ASTCDCompilationUnit cd) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    for (ASTCDClass astcdClass : astcdClassList) {
      String astName = astcdClass.getName();
      String packageDef = String.join(".", cd.getPackageList());
      ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(packageDef + "." + astName);
      String methodName = astName.startsWith(AST_PREFIX) ?
          StringTransformations.uncapitalize(astName.replaceFirst(AST_PREFIX, ""))
          : StringTransformations.uncapitalize(astName);

      // add public static Method for Builder
      ASTModifier modifier = PUBLIC_STATIC.build();
      ASTCDMethod builderMethod = this.getCDMethodFacade().createMethod(modifier, builderType, methodName);
      builderMethodsList.add(builderMethod);
      this.replaceTemplate(EMPTY_BODY, builderMethod, new TemplateHookPoint("mill.BuilderMethod", astName, methodName));

      // add protected Method for Builder
      ASTModifier protectedModifier = PROTECTED.build();
      ASTCDMethod protectedMethod = this.getCDMethodFacade().createMethod(protectedModifier, builderType, "_" + methodName);
      builderMethodsList.add(protectedMethod);
      this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", builderType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())));
    }

    return builderMethodsList;
  }

  /**
   * adds builder methods for the delegation to builders of super grammars
   */
  protected List<ASTCDMethod> addSuperBuilderMethods(List<CDDefinitionSymbol> superSymbolList, List<ASTCDClass> classList) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // get super symbols
    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      if (superSymbol.isPresentAstNode()) {
        for (CDTypeSymbol type : superSymbol.getTypes()) {
          if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
              && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
            superMethods.addAll(getSuperSymbolMethods(superSymbol, type));
          }
          if (type.isIsClass() && !type.isIsAbstract() && type.isPresentAstNode() &&
              !symbolTableService.isClassOverwritten(type.getName() + BUILDER_SUFFIX, classList)) {
            superMethods.addAll(getSuperASTMethods(superSymbol, type, superMethods));
          }
        }
      }
    }
    return superMethods;
  }

  protected List<ASTCDMethod> getScopesGenitorDelegatorMethods(){
    String scopesGenitorSimpleName = symbolTableService.getScopesGenitorDelegatorSimpleName();
    String scopesGenitorFullName = symbolTableService.getScopesGenitorDelegatorFullName();
    return getStaticAndProtectedMethods(StringTransformations.uncapitalize(SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX), scopesGenitorSimpleName, scopesGenitorFullName);
  }

  protected List<ASTCDMethod> getScopesGenitorMethods(){
    String scopesGenitorSimpleName = symbolTableService.getScopesGenitorSimpleName();
    String scopesGenitorFullName = symbolTableService.getScopesGenitorFullName();
    return getStaticAndProtectedMethods(StringTransformations.uncapitalize(SCOPES_GENITOR_SUFFIX), scopesGenitorSimpleName, scopesGenitorFullName);
  }

  protected List<ASTCDMethod> getStaticAndProtectedMethods(String methodName, String name, String fullName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(methodName);
    String protectedMethodName = "_"+staticMethodName;
    ASTMCType scopesGenitorType = getMCTypeFacade().createQualifiedType(fullName);

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, scopesGenitorType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", name, staticMethodName));
    methods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, scopesGenitorType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", fullName));
    methods.add(protectedMethod);
    return methods;
  }

  protected List<ASTCDMethod> getParserMethods(){
    List<ASTCDMethod> parserMethods = Lists.newArrayList();

    String parserName = parserService.getParserClassSimpleName();
    String staticMethodName = "parser";
    String protectedMethodName = "_"+staticMethodName;
    ASTMCType parserType = getMCTypeFacade().createQualifiedType(parserService.getParserClassFullName());

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, parserType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", parserName, staticMethodName));
    parserMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, parserType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedParserMethod", parserService.getParserClassFullName()));
    parserMethods.add(protectedMethod);

    return parserMethods;
  }

  protected List<ASTCDMethod> getGlobalScopeMethods(ASTCDAttribute globalScopeAttribute){
    List<ASTCDMethod> globalScopeMethods = Lists.newArrayList();

    String attributeName = globalScopeAttribute.getName();
    String staticMethodName = "globalScope";
    String protectedMethodName = "_"+staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, globalScopeAttribute.getMCType(), staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), staticMethodName));
    globalScopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, globalScopeAttribute.getMCType(), protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedGlobalScopeMethod", attributeName, symbolTableService.getGlobalScopeFullName()));
    globalScopeMethods.add(protectedMethod);

    return globalScopeMethods;
  }

  protected List<ASTCDMethod> getArtifactScopeMethods(){
    String artifactScopeName = symbolTableService.getArtifactScopeSimpleName();
    ASTMCType returnType = symbolTableService.getArtifactScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getArtifactScopeType();
    return getStaticAndProtectedScopeMethods(StringTransformations.uncapitalize(ARTIFACT_PREFIX + SCOPE_SUFFIX), artifactScopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getScopeMethods(){
    String scopeName = symbolTableService.getScopeClassSimpleName();
    ASTMCType returnType = symbolTableService.getScopeInterfaceType();
    ASTMCType scopeType = symbolTableService.getScopeType();
    return getStaticAndProtectedScopeMethods(StringTransformations.uncapitalize(SCOPE_SUFFIX), scopeName, returnType, scopeType);
  }

  protected List<ASTCDMethod> getStaticAndProtectedScopeMethods(String methodName, String scopeName, ASTMCType returnType, ASTMCType scopeType) {
    List<ASTCDMethod> scopeMethods = Lists.newArrayList();
    String staticMethodName = StringTransformations.uncapitalize(methodName);
    String protectedMethodName = "_" + staticMethodName;

    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, staticMethodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", scopeName, staticMethodName));
    scopeMethods.add(staticMethod);

    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderMethod", scopeType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter())));
    scopeMethods.add(protectedMethod);

    return scopeMethods;
  }

  protected List<ASTCDMethod> getSuperSymbolMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    // for prod with symbol property create delegate builder method
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(type.getAstNode(), superSymbol);
    String millFullName = symbolTableService.getMillFullName(superSymbol);
    String symbolBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolBuilderFullName), symbolBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderMethod, new StringHookPoint("return " + millFullName + "." + symbolBuilderSimpleName + "();"));
    superMethods.add(builderMethod);

    // create corresponding builder for symbolSurrogate
    String symbolSurrogateBuilderFullName = symbolTableService.getSymbolSurrogateBuilderFullName(type.getAstNode(), superSymbol);
    String symbolSurrogateBuilderSimpleName = StringTransformations.uncapitalize(symbolTableService.getSymbolSurrogateBuilderSimpleName(type.getAstNode()));
    ASTCDMethod builderLoaderMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC,
        getMCTypeFacade().createQualifiedType(symbolSurrogateBuilderFullName), symbolSurrogateBuilderSimpleName);

    this.replaceTemplate(EMPTY_BODY, builderLoaderMethod, new StringHookPoint("return " + millFullName + "." + symbolSurrogateBuilderSimpleName + "();"));
    superMethods.add(builderLoaderMethod);
    return superMethods;
  }


  protected List<ASTCDMethod> getSuperASTMethods(CDDefinitionSymbol superSymbol, CDTypeSymbol type,
                                                 List<ASTCDMethod> alreadyDefinedMethods) {
    List<ASTCDMethod> superMethods = new ArrayList<>();
    String astPackageName = superSymbol.getFullName().toLowerCase() + "." + AST_PACKAGE + ".";
    ASTMCQualifiedType superAstType = this.getMCTypeFacade().createQualifiedType(astPackageName + type.getName() + BUILDER_SUFFIX);
    String methodName = StringTransformations.uncapitalize(type.getName().replaceFirst(AST_PREFIX, "")) + BUILDER_SUFFIX;

    // add builder method
    ASTCDMethod createDelegateMethod = this.getCDMethodFacade().createMethod(PUBLIC_STATIC, superAstType, methodName);
    if (!symbolTableService.isMethodAlreadyDefined(createDelegateMethod, alreadyDefinedMethods)) {
      String millPackageName = superSymbol.getFullName().toLowerCase() + ".";
      this.replaceTemplate(EMPTY_BODY, createDelegateMethod, new TemplateHookPoint("mill.BuilderDelegatorMethod", millPackageName + superSymbol.getName(), methodName));
      superMethods.add(createDelegateMethod);
    }
    return superMethods;
  }

  /**
   * Creates the public accessor and protected internal method for a given
   * attribute. The attribute is specified by its simple name, its qualified
   * type, and the qualified return type of the methods. The return type of the
   * method may be equal to the attribute type or a corresponding super type.
   *
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodName The name of the method
   * @param methodType The return type of the methods
   * @return The accessor and corresponding internal method for the attribute
   */
  protected List<ASTCDMethod> getInheritanceTraverserMethods(String attributeName, String attributeType, String methodName, String methodType) {
    List<ASTCDMethod> attributeMethods = Lists.newArrayList();

    // method names and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);

    // static accessor method
    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), methodName));
    attributeMethods.add(staticMethod);

    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod,
            new TemplateHookPoint("mill.InheritanceHandlerMethod", attributeType,
                    visitorService.getAllCDs(), visitorService));

    attributeMethods.add(protectedMethod);

    return attributeMethods;
  }

  /**
   * Creates the public accessor and protected internal method for a given
   * attribute. The attribute is specified by its simple name, its qualified
   * type, and the qualified return type of the methods. The return type of the
   * method may be equal to the attribute type or a corresponding super type.
   * 
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodName The name of the method
   * @param methodType The return type of the methods
   * @return The accessor and corresponding internal method for the attribute
   */
  protected List<ASTCDMethod> getAttributeMethods(String attributeName, String attributeType, String methodName, String methodType) {
    List<ASTCDMethod> attributeMethods = Lists.newArrayList();
    
    // method names and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);
    
    // static accessor method
    ASTCDMethod staticMethod = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, methodName);
    this.replaceTemplate(EMPTY_BODY, staticMethod, new TemplateHookPoint("mill.BuilderMethod", StringTransformations.capitalize(attributeName), methodName));
    attributeMethods.add(staticMethod);
    
    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("return new " + attributeType + "();"));
    attributeMethods.add(protectedMethod);
    
    return attributeMethods;
  }

}
