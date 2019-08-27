/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

public class SymbolTableConstants {

  public static final String SYMBOL_TABLE_PACKAGE = "_symboltable";

  public static final String SYMBOL_SUFFIX = "Symbol";

  public static final String SCOPE_SUFFIX = "Scope";

  public static final String SPANNED_SCOPE = "spanned%sScope";

  public static final String SYMBOL_FULL_NAME = "de.monticore.symboltable.ISymbol";

  public static final String SCOPE_FULL_NAME = "de.monticore.symboltable.IScope";

  public static final String ENCLOSING_SCOPE = "enclosingScope";

  public static final String INTERFACE_PREFIX = "I";

  public static final String ARTIFACT_PREFIX = "Artifact";

  public static final String GLOBAL_PREFIX = "Global";

  public static final String COMMON_PREFIX = "Common";

  public static final String SCOPE_INTERFACE_FULL_NAME = "de.monticore.symboltable.IScope";

  public static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  public static final String SYMBOL_BUILD_TEMPLATE = "_symboltable.symbol.builder.Build";

  public static final String SCOPE_BUILD_TEMPLATE = "_symboltable.scope.builder.Build";

  public static final String DETERMINE_PACKAGE_NAME_METHOD = "determinePackageName";

  public static final String DETERMINE_FULL_NAME_METHOD = "determineFullName";

  public static final String PACKAGE_NAME = "packageName";

  public static final String FULL_NAME = "fullName";

  public static final String AST_NODE_VARIABLE = "astNode";

  public static final String ALREADY_RESOLVED = "AlreadyResolved";

  public static final String SHADOWING = "shadowingScope";

  public static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  public static final String RESOLVE = "resolve%s";

  public static final String RESOLVE_DOWN = "resolve%sDown";

  public static final String RESOLVE_DOWN_MANY = "resolve%sDownMany";

  public static final String RESOLVE_MANY = "resolve%sMany";

  public static final String RESOLVE_LOCALLY = "resolve%sLocally";

  public static final String RESOLVE_LOCALLY_MANY = "resolve%sLocallyMany";

  public static final String RESOLVE_ADAPTED_LOCALLY_MANY = "resolveAdapted%sLocallyMany";

  public static final String RESOLVE_ADAPTED = "resolveAdapted%s";

  public static final String RESOLVE_IMPORTED = "resolve%sImported";

  public static final String FILTER = "filter%s";

  public static final String CONTINUE_WITH_ENCLOSING_SCOPE = "continue%sWithEnclosingScope";

  public static final String CONTINUE_AS_SUB_SCOPE = "continueAs%sSubScope";

  public static final String LOAD_MODELS_FOR = "loadModelsFor%s";

  public static final String PREDICATE = "java.util.function.Predicate";

  public static final String SYMBOLS_MULTI_MAP = "com.google.common.collect.LinkedListMultimap<String, %s>";

  public static final String LANGUAGE_SUFFIX = "Language";

  public static final String MODEL_LOADER_SUFFIX = "ModelLoader";

  public static final String MODEL_PATH = "de.monticore.io.paths.ModelPath";

}
