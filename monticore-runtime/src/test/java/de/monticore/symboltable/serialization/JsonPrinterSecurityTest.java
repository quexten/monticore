/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * This test checks whether injection of objects into serialization and deserialization is avoided
 * correctly.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonPrinterSecurityTest {
  
  @Test
  public void test() {
    // create test subject:
    // Bar
    // |- Bar1
    // |- Bar1.1
    // |- Bar2 (with injected bar 2.1)
    Foo bar = new Foo("Bar");
    Foo bar1 = new Foo("Bar1");
    Foo bar11 = new Foo("Bar1.1");
    Foo bar2 = new Foo(
        "Bar2\",\"children\":[{\"name\":\"Bar2.1\",\"name2\":\"Bar2.1\"}],\"name2\":\"Bar2\"},{\"name\":\"Bar2");
    bar1.children.add(bar11);
    bar.children.add(bar1);
    bar.children.add(bar2);
    
    // use JSONPrinter to produce json stirng
    String s = printFoo(bar);
    
    // use JsonParser to produce JsonObject form seriaiized stnirg
    JsonObject o = JsonParser.parseJsonObject(s);
    
    assertEquals("Bar", getName(o));
    assertEquals(2, getChildren(o).size());
    
    JsonObject b1 = getChildren(o).get(0).getAsJsonObject();
    assertEquals("Bar1", getName(b1));
    assertEquals(1, getChildren(b1).size());
    
    JsonObject b11 = getChildren(b1).get(0).getAsJsonObject();
    assertEquals("Bar1.1", getName(b11));
    assertEquals(false, b11.containsKey("children"));
    
    JsonObject b2 = getChildren(o).get(1).getAsJsonObject();
    assertTrue(getName(b2).startsWith("Bar2"));
    // without escaping, Bar2 would contain the injected child Bar2.1
    assertEquals(false, b2.containsKey("children"));
  }
  
  protected JsonArray getChildren(JsonObject foo) {
    assertEquals(true, foo.containsKey("children"));
    assertEquals(true, foo.get("children").isJsonArray());
    return foo.get("children").getAsJsonArray();
  }
  
  protected String getName(JsonObject foo) {
    assertEquals(true, foo.containsKey("name"));
    assertEquals(true, foo.get("name").isJsonString());
    return foo.get("name").getAsJsonString().getValue();
  }
  
  protected String printFoo(Foo f) {
    JsonPrinter p = new JsonPrinter();
    p.beginObject();
    p.member("name", f.name);
    if (!f.children.isEmpty()) {
      p.beginArray("children");
      f.children.stream().forEach(F -> p.value(printFoo(F)));
      p.endArray();
    }
    p.member("name2", f.name);
    p.endObject();
    return p.getContent();
  }
  
  class Foo {
    String name;
    
    List<Foo> children;
    
    String name2;
    
    public Foo(String name) {
      this.name = name;
      this.children = new ArrayList<>();
      this.name2 = name;
    }
  }
  
}
