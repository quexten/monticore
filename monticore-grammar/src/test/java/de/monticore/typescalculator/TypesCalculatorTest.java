package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.typescalculator.TypesCalculator.*;
import static org.junit.Assert.*;

public class TypesCalculatorTest {

  ExpressionsBasisScope scope;

  @Before
  public void setup(){
    LogStub.init();
    scope = new ExpressionsBasisScope();

    EVariableSymbol sym = new EVariableSymbol("varInt");
    MCTypeSymbol typeSymbol = new MCTypeSymbol("java.lang.Integer");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Integer");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());

    EVariableSymbol sym2 = new EVariableSymbol("varDouble");
    MCTypeSymbol typeSymbol2 = new MCTypeSymbol("java.lang.Double");
    name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Double");
    typeSymbol2.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    List<MCTypeSymbol> subTypes = new ArrayList<>();
    subTypes.add(typeSymbol);
    List<MCTypeSymbol> superTypes = new ArrayList<>();
    superTypes.add(typeSymbol2);
    typeSymbol.setSupertypes(superTypes);
    typeSymbol2.setSubtypes(subTypes);
    sym2.setMCTypeSymbol(typeSymbol2);
    sym.setMCTypeSymbol(typeSymbol);
    scope.add(sym);
    scope.add(sym2);
    //Sub und Supertyp setzen und hinzufuegen

    sym = new EVariableSymbol("varTest");
    name=new ArrayList<>();
    name.add("Test");
    typeSymbol= new MCTypeSymbol("Test");
    ASTMCType type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(sym);
    sym.setMCTypeSymbol(typeSymbol);

    sym2=new EVariableSymbol("varSuperTest");
    name =new ArrayList<>();
    name.add("SuperTest");
    typeSymbol2 = new MCTypeSymbol("SuperTest");
    type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol2.setASTMCType(type);
    typeSymbol2.setEVariableSymbol(sym2);
    subTypes = new ArrayList<>();
    subTypes.add(typeSymbol);
    typeSymbol2.setSubtypes(subTypes);

    superTypes = new ArrayList<>();
    superTypes.add(typeSymbol2);
    typeSymbol.setSupertypes(superTypes);
    sym2.setMCTypeSymbol(typeSymbol2);

    scope.add(sym);
    scope.add(sym2);
    TypesCalculator.setExpressionAndLiteralsTypeCalculator(new CombineExpressionsWithLiteralsTypesCalculator(scope));

  }

  @Test
  public void testIsBoolean() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7<=4&&9>3");
    Optional<ASTExpression> b = p.parse_StringExpression("7+3-(23*9)");

    assertTrue(a.isPresent());
    assertTrue(isBoolean(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isBoolean(b.get()));
  }

  @Test
  public void testIsInt() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isInt(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isInt(b.get()));
  }

  @Test
  public void testIsDouble() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7.3+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isDouble(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isDouble(b.get()));
  }

  @Test
  public void testIsFloat() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7.3f+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isFloat(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isFloat(b.get()));
  }

  @Test
  public void testIsLong() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("7L+3-(23*9)");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isLong(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isLong(b.get()));
  }

  @Test
  public void testIsChar() throws IOException {
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("\'3\'");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");

    assertTrue(a.isPresent());
    assertTrue(isChar(a.get()));

    assertTrue(b.isPresent());
    assertFalse(isChar(b.get()));
  }

  @Test
  public void testIsPrimitive() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> a = p.parse_StringExpression("\'3\'");
    Optional<ASTExpression> b = p.parse_StringExpression("7<=4&&9>3");
    Optional<ASTExpression> c = p.parse_StringExpression("\"Hello World\"");

    assertTrue(a.isPresent());
    assertTrue(isPrimitive(a.get()));

    assertTrue(b.isPresent());
    assertTrue(isPrimitive(b.get()));

    assertTrue(c.isPresent());
    assertFalse(isPrimitive(c.get()));
  }

  @Test
  public void testIsAssignable() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(scope);
    Optional<ASTExpression> a = p.parse_StringExpression("varDouble");
    Optional<ASTExpression> b = p.parse_StringExpression("varInt");
    Optional<ASTExpression> c = p.parse_StringExpression("varSuperTest");
    Optional<ASTExpression> d = p.parse_StringExpression("varTest");
    Optional<ASTExpression> e = p.parse_StringExpression("5");

    assertTrue(a.isPresent());
    assertTrue(isAssignableFrom(a.get(),b.get()));

    assertTrue(b.isPresent());
    assertFalse(isAssignableFrom(b.get(),a.get()));

    assertTrue(c.isPresent());
    assertTrue(isAssignableFrom(c.get(),d.get()));

    assertTrue(d.isPresent());
    assertFalse(isAssignableFrom(d.get(),c.get()));

    assertTrue(e.isPresent());
    assertTrue(isAssignableFrom(a.get(),e.get()));
  }

  @Test
  public void testIsSubtype() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(scope);
    Optional<ASTExpression> a = p.parse_StringExpression("varDouble");
    Optional<ASTExpression> b = p.parse_StringExpression("varInt");
    Optional<ASTExpression> c = p.parse_StringExpression("varSuperTest");
    Optional<ASTExpression> d = p.parse_StringExpression("varTest");

    assertTrue(a.isPresent());
    assertTrue(b.isPresent());

    assertTrue(isSubtypeOf(b.get(),a.get()));

    assertFalse(isSubtypeOf(a.get(),b.get()));

    assertTrue(c.isPresent());
    assertTrue(d.isPresent());

    assertTrue(isSubtypeOf(d.get(),c.get()));

    assertFalse(isSubtypeOf(c.get(),d.get()));
  }

  @Test
  public void testGetType() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(scope);
    List<String> name = new ArrayList<>();
    name.add("Test");
    Optional<ASTExpression> a = p.parse_StringExpression("13+12.5-9");
    Optional<ASTExpression> b = p.parse_StringExpression("varTest");

    assertTrue(a.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(getType(a.get())));

    assertTrue(b.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(getType(b.get())));
  }

  @Test
  public void testGetTypeString() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    TypesCalculator.setScope(scope);
    Optional<ASTExpression> a = p.parse_StringExpression("13+12.5-9");
    Optional<ASTExpression> b = p.parse_StringExpression("varTest");
    Optional<ASTExpression> c = p.parse_StringExpression("varInt");

    assertTrue(a.isPresent());
    assertEquals("double", getTypeString(a.get()));

    assertTrue(b.isPresent());
    assertEquals("Test", getTypeString(b.get()));

    assertTrue(c.isPresent());
    assertEquals("java.lang.Integer",getTypeString(c.get()));
  }





}
