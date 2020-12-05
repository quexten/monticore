package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.*;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_VAR;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

public class ScopesGenitorDecorator extends AbstractCreator<ASTCDCompilationUnit, Optional<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.scopesgenitor.";

  public ScopesGenitorDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final VisitorService visitorService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    Optional<String> startProd = symbolTableService.getStartProdASTFullName(input.getCDDefinition());
    if (startProd.isPresent()) {
      String astFullName = startProd.get();
      String scopesGenitorName = symbolTableService.getScopesGenitorSimpleName();
      String visitorName = visitorService.getVisitor2FullName();
      String handlerName = visitorService.getHandlerFullName();
      String traverserName = visitorService.getTraverserInterfaceFullName();
      String scopeInterface = symbolTableService.getScopeInterfaceFullName();
      String symTabMillFullName = symbolTableService.getMillFullName();
      ASTMCBasicGenericType dequeType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, scopeInterface);
      ASTMCWildcardTypeArgument wildCardTypeArgument = getMCTypeFacade().createWildCardWithUpperBoundType(scopeInterface);
      ASTMCBasicGenericType dequeWildcardType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, wildCardTypeArgument);

      String simpleName = symbolTableService.removeASTPrefix(Names.getSimpleName(startProd.get()));
      List<ASTCDType> symbolDefiningClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition().getCDClassList());
      Map<ASTCDClass, String> inheritedSymbolPropertyClasses = symbolTableService.getInheritedSymbolPropertyClasses(input.getCDDefinition().getCDClassList());
      List<ASTCDType> noSymbolDefiningClasses = symbolTableService.getNoSymbolAndScopeDefiningClasses(input.getCDDefinition().getCDClassList());
      List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
      List<ASTCDType> onlyScopeProds = symbolTableService.getOnlyScopeClasses(input.getCDDefinition());

      ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserName);
      List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

      ASTCDAttribute firstCreatedScopeAttribute = createFirstCreatedScopeAttribute(scopeInterface);
      List<ASTCDMethod> firstCreatedScopeMethod = methodDecorator.getAccessorDecorator().decorate(firstCreatedScopeAttribute);

      ASTCDClass scopesGenitor = CD4CodeMill.cDClassBuilder()
          .setName(scopesGenitorName)
          .setModifier(PUBLIC.build())
          .addInterface(getMCTypeFacade().createQualifiedType(visitorName))
          .addInterface(getMCTypeFacade().createQualifiedType(handlerName))
          .addCDConstructor(createSimpleConstructor(scopesGenitorName, scopeInterface))
          .addCDConstructor(createDequeConstructor(scopesGenitorName, dequeWildcardType, dequeType))
          .addCDConstructor(createZeroArgsConstructor(scopesGenitorName))
          .addCDAttribute(createScopeStackAttribute(dequeType))
          .addCDAttribute(traverserAttribute)
          .addAllCDMethods(traverserMethods)
          .addCDAttribute(firstCreatedScopeAttribute)
          .addAllCDMethods(firstCreatedScopeMethod)
          .addCDMethod(createCreateFromASTMethod(astFullName, scopesGenitorName, symTabMillFullName))
          .addCDMethod(createPutOnStackMethod(scopeInterface))
          .addAllCDMethods(createCurrentScopeMethods(scopeInterface))
          .addCDMethod(createSetScopeStackMethod(dequeType, simpleName))
          .addCDMethod(createCreateScopeMethod(scopeInterface))
          .addAllCDMethods(createSymbolClassMethods(symbolDefiningClasses, scopeInterface))
          .addAllCDMethods(createSymbolClassMethods(inheritedSymbolPropertyClasses, scopeInterface))
          .addAllCDMethods(createVisitForNoSymbolMethods(noSymbolDefiningClasses))
          .addAllCDMethods(createAddToScopeMethods(symbolDefiningProds))
          .addAllCDMethods(createAddToScopeMethodsForSuperSymbols())
          .addAllCDMethods(createScopeClassMethods(onlyScopeProds, scopeInterface))
          .addCDMethod(createAddToScopeStackMethod())
          .build();
      return Optional.ofNullable(scopesGenitor);
    }
    return Optional.empty();
  }

  protected ASTCDConstructor createSimpleConstructor(String symTabCreator, String scopeInterface) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("putOnStack(Log.errorIfNull(" + ENCLOSING_SCOPE_VAR + "));"));
    return constructor;
  }

  protected ASTCDConstructor createDequeConstructor(String symTabCreator, ASTMCType dequeWildcardType, ASTMCType dequeType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(dequeWildcardType, SCOPE_STACK_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new
        StringHookPoint("this." + SCOPE_STACK_VAR + " = Log.errorIfNull(("
        + dequeType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) + ")" + SCOPE_STACK_VAR + ");"));
    return constructor;
  }

  protected ASTCDConstructor createZeroArgsConstructor(String symTabCreator){
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symTabCreator);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(new ArrayDeque<>());"));
    return constructor;
  }

  protected ASTCDAttribute createScopeStackAttribute(ASTMCType dequeType) {
    ASTCDAttribute scopeStack = getCDAttributeFacade().createAttribute(PROTECTED, dequeType, SCOPE_STACK_VAR);
    this.replaceTemplate(VALUE, scopeStack, new StringHookPoint("= new java.util.ArrayDeque<>()"));
    return scopeStack;
  }

  protected ASTCDAttribute createFirstCreatedScopeAttribute(String scopeInterface) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, "firstCreatedScope");
  }

  protected ASTCDAttribute createTraverserAttribute(String visitor) {
    return getCDAttributeFacade().createAttribute(PROTECTED, visitor, TRAVERSER);
  }

  protected ASTCDMethod createCreateFromASTMethod(String astStartProd, String scopesGenitor, String symTabMillFullName) {
    String artifactScope = symbolTableService.getArtifactScopeSimpleName();
    String artifactScopeFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    ASTCDParameter rootNodeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astStartProd), "rootNode");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC,
        getMCTypeFacade().createQualifiedType(artifactScopeFullName), "createFromAST", rootNodeParam);
    String generatedErrorCode = symbolTableService.getGeneratedErrorCode(astStartProd + scopesGenitor + createFromAST.getName());
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateFromAST", symTabMillFullName, artifactScope, scopesGenitor, generatedErrorCode));
    return createFromAST;
  }

  protected ASTCDMethod createPutOnStackMethod(String scopeInterface) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, "putOnStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "PutOnStack"));
    return createFromAST;
  }

  protected List<ASTCDMethod> createCurrentScopeMethods(String scopeInterface) {
    ASTCDMethod getCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getMCTypeFacade().createOptionalTypeOf(scopeInterface), "getCurrentScope");
    this.replaceTemplate(EMPTY_BODY, getCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".peekLast());"));

    ASTCDMethod removeCurrentScope = getCDMethodFacade().createMethod(PUBLIC_FINAL,
        getMCTypeFacade().createOptionalTypeOf(scopeInterface), "removeCurrentScope");
    this.replaceTemplate(EMPTY_BODY, removeCurrentScope, new StringHookPoint(
        "return Optional.ofNullable(" + SCOPE_STACK_VAR + ".pollLast());"));
    return new ArrayList<>(Arrays.asList(getCurrentScope, removeCurrentScope));
  }

  protected ASTCDMethod createSetScopeStackMethod(ASTMCType dequeType, String simpleName) {
    ASTCDParameter dequeParam = getCDParameterFacade().createParameter(dequeType, SCOPE_STACK_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC,
        "set" + StringTransformations.capitalize(simpleName) + "ScopeStack", dequeParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new StringHookPoint(
        "this." + SCOPE_STACK_VAR + " = " + SCOPE_STACK_VAR + ";"));
    return createFromAST;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName) {
    String symTabMill = symbolTableService.getMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        TEMPLATE_PATH + "CreateScope", scopeInterfaceName, symTabMill));
    return createFromAST;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(List<ASTCDType> symbolClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClasses) {
      methodList.addAll(createSymbolClassMethods(symbolClass, symbolTableService.getSymbolFullName(symbolClass), scopeInterface));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(Map<ASTCDClass, String> symbolClassMap, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolClass : symbolClassMap.keySet()) {
      if (!symbolTableService.hasSymbolStereotype(symbolClass.getModifier())) {
        methodList.addAll(createSymbolClassMethods(symbolClass, symbolClassMap.get(symbolClass), scopeInterface));
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createSymbolClassMethods(ASTCDType symbolClass, String symbolFullName, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    String astFullName = symbolTableService.getASTPackage() + "." + symbolClass.getName();
    String simpleName = symbolTableService.removeASTPrefix(symbolClass);
    // visit method
    methodList.add(createSymbolVisitMethod(astFullName, symbolFullName, simpleName));

    // endVisit method
    methodList.add(createSymbolEndVisitMethod(astFullName, symbolClass, simpleName));

    ASTCDParameter astParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astFullName), "ast");
    // create_$ symbol method
    methodList.add(createSymbolCreate_Method(symbolFullName, simpleName, astParam));

    ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    if (symbolClass.isPresentModifier()) {
      boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(symbolClass.getModifier()) ||
          symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier());

      // addToScopeAndLinkWithNode method
      methodList.add(createSymbolAddToScopeAndLinkWithNodeMethod(scopeInterface, astParam, symbolParam, isScopeSpanningSymbol, symbolClass.getModifier()));

      // setLinkBetweenSymbolAndNode method
      methodList.add(createSymbolSetLinkBetweenSymbolAndNodeMethod(astParam, symbolParam, isScopeSpanningSymbol));
      if (isScopeSpanningSymbol) {
        // setLinkBetweenSpannedScopeAndNode method
        methodList.add(createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(scopeInterface, astParam));
      }
    }
    return methodList;
  }


  protected ASTCDMethod createSymbolVisitMethod(String astFullName, String symbolFullName, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "Visit4SSC", symbolFullName, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createSymbolEndVisitMethod(String astFullName, ASTCDType symbolClass, String simpleName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    boolean removeScope = (symbolClass.isPresentModifier() && (symbolTableService.hasScopeStereotype(symbolClass.getModifier())
        || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier())));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(TEMPLATE_PATH + "EndVisitSymbol",  simpleName, removeScope));
    return endVisitMethod;
  }

  protected ASTCDMethod createSymbolCreate_Method(String symbolFullName, String simpleName, ASTCDParameter astParam) {
    String symTabMillFullName = symbolTableService.getMillFullName();
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(symbolFullName + BUILDER_SUFFIX),
        "create_" + simpleName, astParam);
    String symbolSimpleName = Names.getSimpleName(symbolFullName);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new StringHookPoint("return " + symTabMillFullName +
        "." + StringTransformations.uncapitalize(symbolSimpleName) + BUILDER_SUFFIX + "().setName(ast.getName());"));
    return createSymbolMethod;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSpannedScopeAndNodeMethod(String scopeInterface,
                                                                            ASTCDParameter astParam) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    // setLinkBetweenSpannedScopeAndNode method
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC, "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNode"));
    return setLinkBetweenSpannedScopeAndNode;
  }

  protected ASTCDMethod createSymbolAddToScopeAndLinkWithNodeMethod(String scopeInterface, ASTCDParameter astParam,
                                                                    ASTCDParameter symbolParam, boolean isScopeSpanningSymbol,
                                                                    ASTModifier symbolClassModifier) {
    boolean isShadowing = symbolTableService.hasShadowingStereotype(symbolClassModifier);
    boolean isNonExporting = symbolTableService.hasNonExportingStereotype(symbolClassModifier);
    boolean isOrdered = symbolTableService.hasOrderedStereotype(symbolClassModifier);
    ASTCDMethod addToScopeAnLinkWithNode = getCDMethodFacade().createMethod(PUBLIC, "addToScopeAndLinkWithNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, addToScopeAnLinkWithNode, new TemplateHookPoint(
        TEMPLATE_PATH + "AddToScopeAndLinkWithNode", scopeInterface, isScopeSpanningSymbol, isShadowing,
        isNonExporting, isOrdered));
    return addToScopeAnLinkWithNode;
  }

  protected ASTCDMethod createSymbolSetLinkBetweenSymbolAndNodeMethod(ASTCDParameter astParam, ASTCDParameter symbolParam,
                                                                      boolean isScopeSpanningSymbol) {
    String scopeInterface = symbolTableService.getScopeInterfaceSimpleName();
    ASTCDMethod setLinkBetweenSymbolAndNode = getCDMethodFacade().createMethod(PUBLIC, "setLinkBetweenSymbolAndNode", symbolParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSymbolAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSymbolAndNode", isScopeSpanningSymbol, scopeInterface));
    return setLinkBetweenSymbolAndNode;
  }

  protected List<ASTCDMethod> createScopeClassMethods(List<ASTCDType> scopeClasses, String scopeInterface) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType scopeClass : scopeClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + scopeClass.getName();
      String simpleName = symbolTableService.removeASTPrefix(scopeClass);
      // visit method
      methodList.add(createScopeVisitMethod(astFullName, scopeInterface, simpleName));

      // endVisit method
      methodList.add(createScopeEndVisitMethod(astFullName, simpleName));
      ASTCDParameter astParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(astFullName), "ast");

      // create_$ method
      methodList.add(createScopeCreate_Method(scopeInterface, simpleName, astParam, scopeClass.getModifier()));

      ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);

      // setLinkBetweenSpannedScopeAndNode method
      methodList.add(createScopeSetLinkBetweenSpannedScopeAndNodeMethod(astParam, scopeParam));
    }
    return methodList;
  }

  protected ASTCDMethod createScopeVisitMethod(String astFullName, String scopeInterface, String simpleName) {
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "VisitScope4SSC", scopeInterface, simpleName));
    return visitMethod;
  }

  protected ASTCDMethod createScopeEndVisitMethod(String astFullName, String simpleName) {
    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(astFullName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "EndVisitScope4SSC", simpleName));
    return endVisitMethod;
  }

  protected ASTCDMethod createScopeCreate_Method(String scopeInterface, String simpleName, ASTCDParameter astParam,
                                                 ASTModifier scopeModifier) {
    boolean isShadowing = symbolTableService.hasShadowingStereotype(scopeModifier);
    boolean isNonExporting = symbolTableService.hasNonExportingStereotype(scopeModifier);
    boolean isOrdered = symbolTableService.hasOrderedStereotype(scopeModifier);
    ASTCDMethod createSymbolMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeInterface),
        "create_" + simpleName, astParam);
    this.replaceTemplate(EMPTY_BODY, createSymbolMethod, new TemplateHookPoint(TEMPLATE_PATH + "CreateSpecialScope",
        scopeInterface, isShadowing, isNonExporting, isOrdered));
    return createSymbolMethod;
  }

  protected ASTCDMethod createScopeSetLinkBetweenSpannedScopeAndNodeMethod(ASTCDParameter astParam,
                                                                           ASTCDParameter scopeParam) {
    ASTCDMethod setLinkBetweenSpannedScopeAndNode = getCDMethodFacade().createMethod(PUBLIC,
        "setLinkBetweenSpannedScopeAndNode", scopeParam, astParam);
    this.replaceTemplate(EMPTY_BODY, setLinkBetweenSpannedScopeAndNode, new TemplateHookPoint(
        TEMPLATE_PATH + "SetLinkBetweenSpannedScopeAndNodeScope"));
    return setLinkBetweenSpannedScopeAndNode;
  }

  protected List<ASTCDMethod> createVisitForNoSymbolMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String astFullName = symbolTableService.getASTPackage() + "." + astcdClass.getName();
      ASTCDMethod visitorMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(astFullName));
      this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(TEMPLATE_PATH + "VisitNoSymbol"));
      methodList.add(visitorMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createAddToScopeMethods(List<ASTCDType> astcdClasses) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType astcdClass : astcdClasses) {
      String symbolFullName = symbolTableService.getSymbolFullName(astcdClass);
      ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
      ASTCDMethod addToScopeMethod = getCDMethodFacade().createMethod(PUBLIC, "addToScope", symbolParam);
      Optional<String> superType = Optional.empty();
      if (symbolTableService.hasInheritedSymbolStereotype(astcdClass.getModifier())) {
        superType = Optional.of(symbolTableService.getInheritedSymbol(astcdClass));
      }
      this.replaceTemplate(EMPTY_BODY, addToScopeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddToScope",
          superType));
      methodList.add(addToScopeMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createAddToScopeMethodsForSuperSymbols() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String symbolFullName = symbolTableService.getSymbolFullName(type.getAstNode(), cdDefinitionSymbol);
          ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
          ASTCDMethod addToScopeMethod = getCDMethodFacade().createMethod(PUBLIC, "addToScope", symbolParam);
          Optional<String> superType = Optional.empty();
          if (symbolTableService.hasInheritedSymbolStereotype(type.getAstNode().getModifier())) {
            superType = Optional.of(symbolTableService.getInheritedSymbol(type.getAstNode()));
          }

          this.replaceTemplate(EMPTY_BODY, addToScopeMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "AddToScope", superType));
          methodList.add(addToScopeMethod);
        }
      }
    }
    return methodList;
  }

  protected ASTCDMethod createAddToScopeStackMethod(){
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), "scope");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "addToScopeStack", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("scopeStack.addLast(scope);"));
    return method;
  }
}
