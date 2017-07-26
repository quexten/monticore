/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

/* generated from model Tagging1*/
/* generated by template ast.AstClass*/

package mc.feature.hwc.tagging1._ast;

/* generated by template ast_emf.AstEImports*/


import java.util.List;

import mc.grammar.types.ittesttypes._ast.ASTImportStatement;
import mc.grammar.types.ittesttypes._ast.ASTQualifiedName;

public  class ASTTaggingUnit extends ASTTaggingUnitTOP
{

  private String name;

  public ASTTaggingUnit() {
    super();
  }

  public ASTTaggingUnit(List<String> r__package, List<ASTImportStatement> importStatements,
      List<ASTQualifiedName> qualifiedNames, ASTTagBody tagBody) {
    super(r__package, importStatements, qualifiedNames, tagBody);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
