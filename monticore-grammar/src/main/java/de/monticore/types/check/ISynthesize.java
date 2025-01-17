/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.Optional;

/**
 * A common interface that can be used to synthesize SymTypeExpressions from MCTypes
 */
public interface ISynthesize {

  TypeCheckResult synthesizeType(ASTMCType type);

  TypeCheckResult synthesizeType(ASTMCReturnType type);

  TypeCheckResult synthesizeType(ASTMCQualifiedName qName);

}
