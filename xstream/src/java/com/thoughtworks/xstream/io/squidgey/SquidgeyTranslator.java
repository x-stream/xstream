package com.thoughtworks.xstream.io.squidgey;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.squidgey.analysis.DepthFirstAdapter;
import com.thoughtworks.xstream.io.squidgey.lexer.Lexer;
import com.thoughtworks.xstream.io.squidgey.lexer.LexerException;
import com.thoughtworks.xstream.io.squidgey.node.AListAttributes;
import com.thoughtworks.xstream.io.squidgey.node.ANode;
import com.thoughtworks.xstream.io.squidgey.node.AText;
import com.thoughtworks.xstream.io.squidgey.node.Start;
import com.thoughtworks.xstream.io.squidgey.parser.Parser;
import com.thoughtworks.xstream.io.squidgey.parser.ParserException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class SquidgeyTranslator {

    public void transform(Reader squidgeyInput, HierarchicalStreamWriter output) {
        Start ast = parseAst(squidgeyInput);
        ast.apply(new TreeWalker(output));
    }

    private Start parseAst(Reader reader) {
        try {
            PushbackReader pushbackReader = new PushbackReader(reader);
            Lexer lexer = new Lexer(pushbackReader);
            Parser parser = new Parser(lexer);
            return parser.parse();
        } catch (ParserException e) {
            throw new StreamException(e);
        } catch (LexerException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    private static class TreeWalker extends DepthFirstAdapter {

        private HierarchicalStreamWriter output;
        private boolean inAttributes = false;
        private String currentAttributeName;

        public TreeWalker(HierarchicalStreamWriter output) {
            this.output = output;
        }

        public void caseANode(ANode node) {
            if (inAttributes) {
                currentAttributeName = node.getWord().getText();
                super.caseANode(node);
                currentAttributeName = null;
            } else {
                output.startNode(node.getWord().getText());
                super.caseANode(node);
                output.endNode();
            }
        }

        public void caseAListAttributes(AListAttributes node) {
            inAttributes = true;
            super.caseAListAttributes(node);
            inAttributes = false;
        }

        public void caseAText(AText node) {
            if (inAttributes) {
                output.addAttribute(currentAttributeName, node.getCdata().getText());
            } else {
                output.setValue(node.getCdata().getText());
            }
        }
    }
}
