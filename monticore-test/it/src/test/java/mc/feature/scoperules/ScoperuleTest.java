/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import mc.feature.scoperules.scoperuletest.ScoperuleTestMill;
import mc.feature.scoperules.scoperuletest._parser.ScoperuleTestParser;
import mc.feature.scoperules.scoperuletest._symboltable.*;
import mc.feature.scoperules.scoperuletest._ast.ASTFoo;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScoperuleTest {

  @BeforeClass
  public static void setup(){
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testModel() throws IOException {
    ScoperuleTestParser parser = ScoperuleTestMill.parser();
    Optional<ASTFoo> optModel = parser.parse("src/test/resources/mc/feature/symbolrules/SymbolruleTest.rule");
    assertTrue(optModel.isPresent());

    ScoperuleTestScopesGenitorDelegator scopesGenitorDelegator = ScoperuleTestMill.scopesGenitorDelegator();
    IScoperuleTestArtifactScope scope = scopesGenitorDelegator.createFromAST(optModel.get());
    scope.setName("SymbolruleTest");
    scope.setBar(true);
    scope.setNumber(17);
    scope.setModifiedNameList(Lists.newArrayList("foo", "bar", "test"));
    scope.setSymType(SymTypeExpressionFactory.createTypeConstant("int"));
    ScoperuleTestSymbols2Json symbols2Json = new ScoperuleTestSymbols2Json();
    scope.accept(symbols2Json.getTraverser());
    String serialized = symbols2Json.getSerializedString();
    ScoperuleTestDeSer deSer = new ScoperuleTestDeSer();
    IScoperuleTestScope as = deSer.deserialize(serialized);
    assertTrue(as.isBar());
    assertEquals(17, as.getNumber());
    assertEquals(3, as.getModifiedNameList().size());
    assertEquals("foo", as.getModifiedName(0));
    assertEquals("bar", as.getModifiedName(1));
    assertEquals("test", as.getModifiedName(2));
    assertTrue(SymTypeExpressionFactory.createTypeConstant("int").deepEquals(as.getSymType()));
  }

  @Test
  public void testExtendsAndImplements(){
    IScoperuleTestScope scope = ScoperuleTestMill.scope();

    assertTrue(scope instanceof ScoperuleTestScope);
    Dummy dummy = (ScoperuleTestScope) scope;

    assertTrue(scope instanceof IDummy);
    IDummy dummyI = (IDummy) scope;
  }

}
