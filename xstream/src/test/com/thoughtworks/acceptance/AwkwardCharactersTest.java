package com.thoughtworks.acceptance;

public class AwkwardCharactersTest extends AbstractAcceptanceTest {

    public static class WithDollarCharField extends StandardObject {
        String $field;
        String field$;
        String fi$eld;
        String fi$$eld;
    }

    public void testSupportsFieldsWithDollarChar() {
        xstream.alias("dollar", WithDollarCharField.class);

        WithDollarCharField in = new WithDollarCharField();
        in.$field = "a";
        in.field$ = "b";
        in.fi$eld = "c";
        in.fi$$eld = "d";

        String expected = "" +
                "<dollar>\n" +
                "  <_DOLLAR_field>a</_DOLLAR_field>\n" +
                "  <field_DOLLAR_>b</field_DOLLAR_>\n" +
                "  <fi_DOLLAR_eld>c</fi_DOLLAR_eld>\n" +
                "  <fi_DOLLAR__DOLLAR_eld>d</fi_DOLLAR__DOLLAR_eld>\n" +
                "</dollar>";
        assertBothWays(in, expected);
    }

    public static class WithUnderscoreCharField extends StandardObject {
        String _field;
        String field_;
        String fi_eld;
        String fi__eld;
    }

    public void testSupportsFieldsWithUnderscoreChar() {
        xstream.alias("underscore", WithUnderscoreCharField.class);

        WithUnderscoreCharField in = new WithUnderscoreCharField();
        in._field = "a";
        in.field_ = "b";
        in.fi_eld = "c";
        in.fi__eld = "d";

        String expected = "" +
                "<underscore>\n" +
                "  <__field>a</__field>\n" +
                "  <field__>b</field__>\n" +
                "  <fi__eld>c</fi__eld>\n" +
                "  <fi____eld>d</fi____eld>\n" +
                "</underscore>";
        assertBothWays(in, expected);
    }

    public static class A_B extends StandardObject {
        private int x;

        public A_B(int x) {
            this.x = x;
        }

    }

    public void testSupportsUnderscoreInShortClassName() {
        assertBothWays(new A_B(3), ""
                + "<com.thoughtworks.acceptance.AwkwardCharactersTest-A_B>\n"
                + "  <x>3</x>\n"
                + "</com.thoughtworks.acceptance.AwkwardCharactersTest-A_B>");
    }
}
