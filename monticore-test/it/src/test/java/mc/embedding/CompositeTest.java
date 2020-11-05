/* (c) https://github.com/MontiCore/monticore */

package mc.embedding;

import mc.GeneratorIntegrationsTest;
import mc.embedding.composite.CompositeMill;
import mc.embedding.composite._symboltable.ICompositeGlobalScope;
import mc.embedding.composite._symboltable.ICompositeScope;
import mc.embedding.composite._symboltable.Text2ContentAdapter;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;
import mc.embedding.host._symboltable.HostSymbol;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CompositeTest extends GeneratorIntegrationsTest {

  @Test
  public void test() {
    final ICompositeGlobalScope scope = CompositeMill.compositeGlobalScope();
    scope.setModelFileExtension("host");
    scope.getModelPath().addEntry(Paths.get("src/test/resources/mc/embedding"));

  // Symbol of the host language
    final HostSymbol hostSymbol = scope.resolveHost("ZComposite").orElse(null);
    assertNotNull(hostSymbol);
    assertEquals("ZComposite", hostSymbol.getName());

    // Symbol of the embedded language
    assertTrue(hostSymbol.getSpannedScope() instanceof ICompositeScope);
        final TextSymbol textSymbol = ((ICompositeScope)hostSymbol.getSpannedScope()).resolveText("Hello").orElse(null);
    assertNotNull(textSymbol);

    // Adapted text symbol -> content symbol
    final ContentSymbol text2ContentSymbol = hostSymbol.getSpannedScope().resolveContent("Hello").orElse(null);
    assertNotNull(text2ContentSymbol);
    assertTrue(text2ContentSymbol instanceof Text2ContentAdapter);


  }

}