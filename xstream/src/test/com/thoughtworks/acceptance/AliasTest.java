package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.alias.CannotResolveClassException;

/**
 * @author Paul Hammant
 */
public class AliasTest extends AbstractAcceptanceTest {

   public void testBarfsIfAliasDoesNotExist() {

       String xml = "" +
               "<X-array>\n" +
               "  <X>\n" +
               "    <anInt>0</anInt>\n" +
               "  </X>\n" +
               "</X-array>";

       // now change the alias
       xstream.alias("Xxxxxxxx", X.class);
       try {
           xstream.fromXML(xml);
           fail("Should have barfed");
       } catch (CannotResolveClassException expectedException) {
           // expected
       }
   }

}
