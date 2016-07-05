package org.getalp.dbnary.wiki;

import org.sweble.wikitext.parser.WikitextEncodingValidator;
import scala.collection.immutable.Stream;

import java.util.*;

/**
 * Created by serasset on 24/01/16.
 */
public class WikiText {
    private String content;
    private int startOffset;
    private int endOffset;
    private WikiContent root;

    /**
     * A segment of text identifies a substring whose first character is at
     * position start and last character is at position end-1;
     */
    public class Segment {
        int start, end;

        public Segment(int start) {
            this.start = start;
        }

        public Segment(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

    public abstract class Token {
        Segment offset;

        public void addToken(Token t) {
        }

        public void setEndOffset(int endOffset) {
            this.offset.setEnd(endOffset);
        }

        public String toString() {
            return (null == offset) ? super.toString() : WikiText.this.content.substring(offset.start, offset.end);
        }

        public void addFlattenedTokens(Token t) {
            if (t instanceof WikiContent) {
                WikiContent wc = (WikiContent) t;
                for (Token token : wc.tokens) {
                    this.addToken(token);
                }
            } else if (t instanceof Template) {
                Template tmpl = (Template) t;
                this.addFlattenedTokens(tmpl.name);
                if (null != tmpl.args)
                    for (WikiContent arg : tmpl.args) {
                        this.addFlattenedTokens(arg);
                    }
            } else if (t instanceof InternalLink) {
                InternalLink l = (InternalLink) t;
                this.addFlattenedTokens(l.target);
                this.addFlattenedTokens(l.text);
            }
        }
    }

    /**
     * Upper element containing text/links/templates and comments interleaved
     */
    public class WikiContent extends Token {
        ArrayList<Token> tokens = new ArrayList<>();

        public WikiContent(int startOffset) {
            this.offset = new Segment(startOffset);
        }

        @Override
        public void addToken(Token t) {
            tokens.add(t);
        }
    }

    public class HTMLComment extends Token {

        public HTMLComment(int startOffset) {
            this.offset = new Segment(startOffset);
        }

    }

    public class Template extends Token {
        protected WikiContent name;
        protected ArrayList<WikiContent> args;

        public Template(int startOffset) {
            this.offset = new Segment(startOffset);
            name = new WikiContent(startOffset+2);
        }

        /**
         * sets the end offset to the given position (should point to the first char of the closing "}}")
         * @param position the position of the first character of the enclosing "}}"
         */
        @Override
        public void setEndOffset(int position) {
            if (null == args) {
                this.name.setEndOffset(position);
            } else {
                args.get(args.size()-1).setEndOffset(position);
            }
            super.setEndOffset(position+2);
        }

        public void gotAPipe(int position) {
            if (null == args) {
                this.name.setEndOffset(position);
                args = new ArrayList<>();
                args.add(new WikiContent(position+1));
            } else {
                // got a new parameter separator...
                if (! args.isEmpty()) args.get(args.size()-1).setEndOffset(position);
                args.add(new WikiContent(position+1));
            }
        }

        @Override
        public void addToken(Token t) {
            if (null == args) {
                this.name.addToken(t);
            } else {
                // got a new parameter separator...
                args.get(args.size()-1).addToken(t);
            }
        }

        public String getName() {
            return name.toString();
        }

        public Map<String, String> parseArgs() {
            HashMap<String,String> res = new HashMap<String,String>();
            if (null == args) return res;
            int n = 1; // number for positional args.
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i).toString();
                if (null == arg || arg.length() == 0) continue;
                int eq = arg.indexOf('=');
                if (eq == -1) {
                    // There is no argument name.
                    res.put(""+n, arg);
                    n++;
                } else {
                    res.put(arg.substring(0,eq), arg.substring(eq+1));
                }
            }
            return res;
        }

    }
    public abstract class Link extends Token {
        WikiContent target;
        WikiContent text;

        @Override
        public void addToken(WikiText.Token t) {
            if (null == this.text) {
                this.target.addToken(t);
            } else {
                this.text.addToken(t);
            }
        }
    }

    public class InternalLink extends Link {

        public InternalLink(int startOffset) {
            this.offset = new Segment(startOffset);
            this.target = new WikiContent(startOffset+2);
        }

        /**
         * sets the end offset to the given position (should point to the first char of the closing "]]")
         * @param position the position of the first character of the enclosing "]]"
         */
        @Override
        public void setEndOffset(int position) {
            super.setEndOffset(position+2);
            if (null == this.text) {
                this.target.setEndOffset(position);
            } else {
                this.text.setEndOffset(position);
            }
        }

        public void gotAPipe(int position) {
            if (null == this.text) {
                this.target.setEndOffset(position);
                this.text = new WikiContent(position + 1);
            }
        }

    }

    public class ExternalLink extends Link {

        public ExternalLink(int startOffset) {
            this.offset = new Segment(startOffset);
            this.target = new WikiContent(startOffset+1);
        }

        /**
         * sets the end offset to the given position (should point to the first char of the closing "]")
         * @param position the position of the first character of the enclosing "]"
         */
        @Override
        public void setEndOffset(int position) {
            super.setEndOffset(position+1);
            if (null == this.text) {
                this.target.setEndOffset(position);
            } else {
                this.text.setEndOffset(position);
            }
        }

        public void gotASpace(int position) {
            if (null == this.text) {
                this.target.setEndOffset(position);
                this.text = new WikiContent(position + 1);
            }
        }

    }

    public WikiText(String content) {
        this(content, 0, content.length());
    }

    public WikiText(String content, int startOffset, int endOffset) {
        if ((startOffset < 0) || (startOffset > content.length()))
            throw new IndexOutOfBoundsException("startOffset");
        if ((endOffset < 0) || (endOffset > content.length()))
            throw new IndexOutOfBoundsException("endOffset");
        if (startOffset > endOffset)
            throw new IndexOutOfBoundsException("start > end");
        this.content = content;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    private WikiContent parse() {
        int pos = this.startOffset;
        int end = this.endOffset;
        Stack<Token> stack = new Stack<>();
        stack.push(new WikiContent(0));

        while (pos < end) {
            if (lookup(pos, "{{")) {
                // Template Start
                stack.push(new Template(pos));
                pos++;
            } else if (lookup(pos, "[[")) {
                // InternalLink start
                stack.push(new InternalLink(pos));
                pos++;
            } else if (lookup(pos, "[")) {
                // External Link
                stack.push(new ExternalLink(pos));
            } if (lookup(pos, "}}")) {
                // Template End
                if (stack.peek() instanceof Template) {
                    Template t = (Template) stack.pop();
                    t.setEndOffset(pos);
                    stack.peek().addToken(t);
                } else  {
                    // consider the closing element as a simple text
                }
                pos++;
            } else if (lookup(pos, "]]")) {
                // InternalLink end
                if (stack.peek() instanceof InternalLink) {
                    InternalLink t = (InternalLink) stack.pop();
                    t.setEndOffset(pos);
                    stack.peek().addToken(t);
                } else {
                    // consider the closing element as a simple text
                }
            } else if (lookup(pos, "]")) {
                // External Link End
                if (stack.peek() instanceof ExternalLink) {
                    ExternalLink t = (ExternalLink) stack.pop();
                    t.setEndOffset(pos);
                    stack.peek().addToken(t);
                } else {
                    // consider the closing element as a simple text
                }
            } else if (lookup(pos, "<!--")) {
                // HTML comment start, just pass through text ignoring everything
                HTMLComment t = new HTMLComment(pos);
                pos = pos + 4;
                while (pos != end && ! lookup(pos, "-->")) pos++;
                if (pos != end) pos = pos + 2;
                t.setEndOffset(pos+1);
                stack.peek().addToken(t);
            } else if (lookup(pos, "|")) {
                // if in Template, it's a special char
                Token t = stack.peek();
                if (t instanceof Template) {
                    Template template = (Template) t;
                    template.gotAPipe(pos);
                } else if (t instanceof InternalLink) {
                    InternalLink template = (InternalLink) t;
                    template.gotAPipe(pos);
                }
            } else if (lookup(pos, " ")) {
                // if in ExternalLink, it's a special char
                Token t = stack.peek();
                if (t instanceof ExternalLink) {
                    ExternalLink template = (ExternalLink) t;
                    template.gotASpace(pos);
                }
                //TODO Handle nowiki tags
            } else {
                // Normal characters, just advance...
            }
            pos++;
        }

        while (stack.size() > 1) {
            // error: end of wiki text while elements are being parsed
            // In this case, we assume that unclosed elements are simple textual contents.
            Token t = stack.pop();
            stack.peek().addFlattenedTokens(t);
        }

        Token root = stack.pop();

        ((WikiContent) root).setEndOffset(end);
        return (WikiContent) root;

    }

    public boolean lookup(int pos, String s) {
        int i = 0; int pi;
        int slength = s.length();
        int wtlength = content.length();
        while (i != slength && (pi = pos + i) != wtlength && s.charAt(i) == content.charAt(pi)) i++;
        return i == slength;
    }

    public ArrayList<Token> tokens() {
        if (null == root) root = parse();
        return root.tokens;
    }

    public WikiEventsSequence filteredTokens(WikiEventFilter filter) {
        if (null == root) root = parse();
        return new WikiEventsSequence(this, filter);
    }

    public WikiEventsSequence links() {
        ClassBasedFilter filter = new ClassBasedFilter();
        filter.allowLink();
        return filteredTokens(filter);
    }

    public WikiEventsSequence templates() {
        ClassBasedFilter filter = new ClassBasedFilter();
        filter.allowTemplates();
        return filteredTokens(filter);
    }
}
