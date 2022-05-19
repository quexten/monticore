/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the JsonPrinter
 */
public class JsonPrinterTest {

  @Before
  public void disableIndentation() {
    JsonPrinter.disableIndentation();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testOmitEmptyArray() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("name", "anArtifactScopeName");
    printer.beginArray("kindHierarchy");
    printer.endArray();
    printer.beginArray("symbols");
    printer.value("foo");
    printer.endArray();
    printer.endObject();

    String serialized = printer.getContent();
    assertTrue(null != JsonParser.parseJsonObject(serialized));
    assertTrue(!serialized.contains("kindHierarchy"));
  }

  @Test
  public void testEscapeSequences() {
    JsonPrinter printer = new JsonPrinter();
    printer.value("\"\t\\\n\'");
    assertEquals("\"\\\"\\t\\\\\\n\\'\"", printer.toString());
  }

  @Test
  public void testEmptyObject() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    printer.endObject();
    assertEquals("{}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.endObject();
    assertEquals("", printer.toString());
  }

  @Test
  public void testDefaultString() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("s", "");
    printer.endObject();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("s", "");
    printer.endObject();
    assertEquals("{\"s\":\"\"}", printer.toString());
  }

  @Test
  public void testDefaultInt() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("i", 0);
    printer.endObject();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("i", 0);
    printer.endObject();
    assertEquals("{\"i\":0}", printer.toString());
  }

  @Test
  public void testDefaultBoolean() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("b", false);
    printer.endObject();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("b", false);
    printer.endObject();
    assertEquals("{\"b\":false}", printer.toString());
  }

  @Test
  public void testEmptyList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginArray();
    printer.endArray();
    assertEquals("[]", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.beginArray("emptyList");
    printer.endArray();
    printer.endObject();
    assertEquals("{\"emptyList\":[]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginArray();
    printer.endArray();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.beginArray("emptyList");
    printer.endArray();
    printer.endObject();
    assertEquals("", printer.toString());
  }

  @Test
  public void testBasicTypeAttributes() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("booleanAttribute", true);
    printer.endObject();
    assertEquals("{\"booleanAttribute\":true}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("intAttribute", -1);
    printer.endObject();
    assertEquals("{\"intAttribute\":-1}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("floatAttribute", 47.11f);
    printer.endObject();
    assertEquals("{\"floatAttribute\":47.11}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("doubleAttribute", 47.11);
    printer.endObject();
    assertEquals("{\"doubleAttribute\":47.11}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("longAttribute", 123456789L);
    printer.endObject();
    assertEquals("{\"longAttribute\":123456789}", printer.toString());
  }

  @Test
  public void testOptionalAndList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    printer.endObject();
    assertEquals("{\"optionalAttribute\":\"presentOptional\"}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    printer.endObject();
    assertEquals("{\"optionalAttribute\":\"presentOptional\"}", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.empty());
    printer.endObject();
    assertEquals("{\"optionalAttribute\":null}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.empty());
    printer.endObject();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("listAttribute", new ArrayList<>());
    printer.endObject();
    assertEquals("{\"listAttribute\":[]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("listAttribute", new ArrayList<>());
    printer.endObject();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    printer.endObject();
    assertEquals("{\"listAttribute\":[\"first\",\"second\"]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    printer.endObject();
    assertEquals("{\"listAttribute\":[\"first\",\"second\"]}", printer.toString());
  }

  @Test
  public void testInvalidNestings() {

    Log.clearFindings();
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.beginObject();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginObject();
    printer.endObject();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginArray();
    printer.beginArray();
    printer.endArray();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginObject();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.endObject();
    printer.getContent();
    assertEquals(3, Log.getFindings().size());
  }

}
