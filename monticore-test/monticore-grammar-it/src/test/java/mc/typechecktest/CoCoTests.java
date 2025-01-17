/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typechecktest._ast.ASTTCCompilationUnit;
import mc.typechecktest._cocos.TypeCheckTestCoCoChecker;
import mc.typechecktest._cocos.VariableAssignmentCorrectType;
import mc.typechecktest._cocos.VariableDeclarationIsCorrect;
import mc.typechecktest._parser.TypeCheckTestParser;
import mc.typechecktest._symboltable.TypeCheckTestPhasedSTC;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoTests {

  private ASTTCCompilationUnit check;
  private ASTTCCompilationUnit bar;
  private ASTTCCompilationUnit inheritanceBar;
  private ASTTCCompilationUnit staticAbstractOOMethods;
  private ASTTCCompilationUnit staticAbstractOOFields;
  private ASTTCCompilationUnit wrongAssignment;
  private ASTTCCompilationUnit complicatedWrongAssignment;
  private ASTTCCompilationUnit complicatedCorrectAssignment;
  private ASTTCCompilationUnit inheritedCannotUseStaticFromSuper;

  @BeforeClass
  public static void init(){
    LogStub.init();
    LogStub.enableFailQuick(false);
    TypeCheckTestMill.init();
  }

  @Before
  public void setup() throws IOException {
    TypeCheckTestMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    TypeSymbol string = TypeCheckTestMill
        .typeSymbolBuilder()
        .setName("String")
        .setEnclosingScope(TypeCheckTestMill.globalScope())
        .build();
    string.setSpannedScope(TypeCheckTestMill.scope());
    TypeCheckTestMill.globalScope().add(string);
    TypeCheckTestMill.globalScope().setSymbolPath(new MCPath(Paths.get("src/test/resources")));

    TypeCheckTestParser parser = TypeCheckTestMill.parser();
    Optional<ASTTCCompilationUnit> bar = parser
        .parse("src/test/resources/mc/typescalculator/valid/Bar.tc");
    assertTrue(bar.isPresent());
    TypeCheckTestPhasedSTC stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(bar.get());
    this.bar = bar.get();

    Optional<ASTTCCompilationUnit> inheritanceBar = parser
        .parse("src/test/resources/mc/typescalculator/valid/InheritanceBar.tc");
    assertTrue(inheritanceBar.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(inheritanceBar.get());
    this.inheritanceBar = inheritanceBar.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOMethods = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/StaticAbstractOOMethods.tc");
    assertTrue(staticAbstractOOMethods.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOMethods.get());
    this.staticAbstractOOMethods = staticAbstractOOMethods.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOFields = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/StaticAbstractOOFields.tc");
    assertTrue(staticAbstractOOFields.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOFields.get());
    this.staticAbstractOOFields = staticAbstractOOFields.get();


    Optional<ASTTCCompilationUnit> check = parser
        .parse("src/test/resources/mc/typescalculator/valid/Check.tc");
    assertTrue(check.isPresent());
    this.check = check.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(check.get());

    Optional<ASTTCCompilationUnit> wrongAssignment = parser
        .parse("src/test/resources/mc/typescalculator/invalid/WrongAssignment.tc");
    assertTrue(wrongAssignment.isPresent());
    this.wrongAssignment = wrongAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(wrongAssignment.get());

    Optional<ASTTCCompilationUnit> complicatedWrongAssignment = parser
        .parse("src/test/resources/mc/typescalculator/invalid/ComplicatedWrongAssignment.tc");
    assertTrue(complicatedWrongAssignment.isPresent());
    this.complicatedWrongAssignment = complicatedWrongAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(complicatedWrongAssignment.get());

    Optional<ASTTCCompilationUnit> complicatedCorrectAssignment = parser
        .parse("src/test/resources/mc/typescalculator/valid/ComplicatedCorrectAssignment.tc");
    assertTrue(complicatedCorrectAssignment.isPresent());
    this.complicatedCorrectAssignment = complicatedCorrectAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(complicatedCorrectAssignment.get());

    Optional<ASTTCCompilationUnit> inheritedCannotUseStaticFromSuper = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/InheritedCannotUseStaticFromSuper.tc");
    assertTrue(inheritedCannotUseStaticFromSuper.isPresent());
    this.inheritedCannotUseStaticFromSuper = inheritedCannotUseStaticFromSuper.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(inheritedCannotUseStaticFromSuper.get());
  }

  @Test
  public void testValidCheckOOAndAbstract(){
    testValidOO(check);
    testValidAbstract(check);
  }

  @Test
  public void testValidBarOOAndAbstract(){
    testValidOO(bar);
    testValidAbstract(bar);
  }

  @Test
  public void testValidInheritanceBarOOAndAbstract(){
    testValidOO(inheritanceBar);
    testValidAbstract(inheritanceBar);
  }

  @Test
  public void testStaticAbstractOOMethods(){
    testValidAbstract(staticAbstractOOMethods);
    testInvalidOO("0xA2239", staticAbstractOOMethods);
  }

  @Test
  public void testStaticAbstractOOFields(){
    testValidAbstract(staticAbstractOOFields);
    testInvalidOO("0xA1236", staticAbstractOOFields);
  }

  @Test
  public void testInheritedCannotUseStaticFromSuper(){
    testValidAbstract(inheritedCannotUseStaticFromSuper);
    testInvalidOO("0xA2239",inheritedCannotUseStaticFromSuper);
  }

  @Test
  public void testComplicatedCorrectAssignment(){
    testValidAbstract(complicatedCorrectAssignment);
    testValidOO(complicatedCorrectAssignment);
  }

  @Test
  public void testComplicatedWrongAssignment(){
    testInvalidAbstract("0xA0211",complicatedWrongAssignment);
    testInvalidOO("0xA0211", complicatedWrongAssignment);
  }

  @Test
  public void testWrongAssignment(){
    testInvalidAbstract("0xA0457", wrongAssignment);
    testInvalidOO("0xA0457", wrongAssignment);
  }

  protected void testInvalidAbstract(String errorCode, ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getAbstractChecker();
    try{
      checker.checkAll(comp);
    }catch(Exception e){
      //do nothing here, just catch the exception for further testing
    }
    assertTrue(Log.getFindingsCount()>=1);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(errorCode));
  }

  protected void testInvalidOO(String errorCode, ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getOOChecker();
    try{
      checker.checkAll(comp);
    }catch(Exception e){
      //do nothing here, just catch the exception for further testing
    }
    assertTrue(Log.getFindingsCount()>=1);
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(errorCode));
  }

  protected void testValidAbstract(ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getAbstractChecker();
    checker.checkAll(comp);
    assertEquals(0, Log.getFindingsCount());
  }

  protected void testValidOO(ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getOOChecker();
    checker.checkAll(comp);
    assertEquals(0, Log.getFindingsCount());
  }

  protected TypeCheckTestCoCoChecker getOOChecker(){
    ISynthesize synthesize = new SynthesizeSymTypeFromTypeCheckTest();
    IDerive typesCalculator = new DeriveSymTypeFromTypeCheckTest();
    TypeCalculator tc = new TypeCalculator(synthesize, typesCalculator);
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));
    return checker;
  }

  protected TypeCheckTestCoCoChecker getAbstractChecker(){
    ISynthesize synthesize = new SynthesizeSymTypeFromTypeCheckTest();
    IDerive typesCalculator = new DeriveSymTypeFromTypeCheckTestAbstract();
    TypeCalculator tc = new TypeCalculator(synthesize, typesCalculator);
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));
    return checker;
  }

}
