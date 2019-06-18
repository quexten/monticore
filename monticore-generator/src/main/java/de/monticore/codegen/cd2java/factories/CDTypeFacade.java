package de.monticore.codegen.cd2java.factories;

import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;

public class CDTypeFacade {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private static CDTypeFacade cdTypeFacade;

  private final CD4AnalysisParser parser;

  private CDTypeFacade() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDTypeFacade getInstance() {
    if (cdTypeFacade == null) {
      cdTypeFacade = new CDTypeFacade();
    }
    return cdTypeFacade;
  }

  public ASTMCType createTypeByDefinition(final String typeSignature) {
    Optional<ASTMCType> type;
    try {
      type = parser.parseMCType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createReferenceTypeByDefinition(final String typeSignature) {
    Optional<ASTMCQualifiedType> type;
    try {
      type = parser.parseMCQualifiedType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createQualifiedType(final Class<?> clazz) {
    return createQualifiedType(clazz.getSimpleName());
  }

  public ASTMCQualifiedType createQualifiedType(final String name) {
    ASTMCQualifiedName qualName = MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(name.split(PACKAGE_SEPARATOR))).build();
    return MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualName).build();
  }

  public ASTMCObjectType createOptionalTypeOf(final Class<?> clazz) {
    return createOptionalTypeOf(clazz.getSimpleName());
  }

  public ASTMCOptionalType createOptionalTypeOf(final String name) {
    ASTMCTypeArgument arg = CD4AnalysisMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(name)).build();
    return CD4AnalysisMill.mCOptionalTypeBuilder().setMCTypeArgument(arg).build();
  }

  public ASTMCOptionalType createOptionalTypeOf(final ASTMCType type) {
    return createOptionalTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCListType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTMCListType createListTypeOf(final String name) {
    ASTMCTypeArgument arg = CD4AnalysisMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(name)).build();
    return CD4AnalysisMill.mCListTypeBuilder().setMCTypeArgument(arg).build();
  }

  public ASTMCListType createListTypeOf(final ASTMCType type) {
    return createListTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCSetType createCollectionTypeOf(final Class<?> clazz) {
    return createCollectionTypeOf(clazz.getSimpleName());
  }

  public ASTMCSetType createCollectionTypeOf(final String name) {
    ASTMCTypeArgument arg = CD4AnalysisMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(name)).build();
    return CD4AnalysisMill.mCSetTypeBuilder().setMCTypeArgument(arg).build();
  }

  public ASTMCSetType createCollectionTypeOf(final ASTMCType type) {
    return createCollectionTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCMapType createMapTypeOf(final String firstType, final String secondType) {
    ASTMCTypeArgument first = CD4AnalysisMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(firstType)).build();
    ASTMCTypeArgument second = CD4AnalysisMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(secondType)).build();
    return CD4AnalysisMill.mCMapTypeBuilder().setKey(first).setValue(second).build();
  }


  public ASTMCVoidType createVoidType() {
    return MCBasicTypesMill.mCVoidTypeBuilder()
        .build();
  }

  public ASTMCType createBooleanType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
  }

  public boolean isBooleanType(ASTMCType type) {
    return type.deepEquals(createBooleanType());
  }

  public ASTMCType createIntType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.INT);
  }

  private ASTMCType createPrimitiveType(int constantsType) {
    return MCBasicTypesMill.mCPrimitiveTypeBuilder()
        .setPrimitive(constantsType)
        .build();
  }
}
