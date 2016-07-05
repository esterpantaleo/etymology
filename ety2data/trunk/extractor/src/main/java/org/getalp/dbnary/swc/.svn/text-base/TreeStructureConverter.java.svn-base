package org.getalp.dbnary.swc;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ast.AstNodePropertyIterator;
import de.fau.cs.osr.utils.StringUtils;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngPage;
import org.sweble.wikitext.parser.nodes.*;
import org.sweble.wikitext.parser.parser.LinkTargetException;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * A visitor to convert an article AST into a pure text representation. To
 * better understand the visitor pattern as implemented by the Visitor class,
 * please take a look at the following resources:
 * <ul>
 * <li><a
 * href="http://en.wikipedia.org/wiki/Visitor_pattern">http://en.wikipedia
 * .org/wiki/Visitor_pattern</a> (classic pattern)</li>
 * <li><a
 * href="http://www.javaworld.com/javaworld/javatips/jw-javatip98.html">http
 * ://www.javaworld.com/javaworld/javatips/jw-javatip98.html</a> (the version we
 * use here)</li>
 * </ul>
 *
 * The methods needed to descend into an AST and visit the children of a given
 * node <code>n</code> are
 * <ul>
 * <li><code>dispatch(n)</code> - visit node <code>n</code>,</li>
 * <li><code>iterate(n)</code> - visit the <b>children</b> of node
 * <code>n</code>,</li>
 * <li><code>map(n)</code> - visit the <b>children</b> of node <code>n</code>
 * and gather the return values of the <code>visit()</code> calls in a list,</li>
 * <li><code>mapInPlace(n)</code> - visit the <b>children</b> of node
 * <code>n</code> and replace each child node <code>c</code> with the return
 * value of the call to <code>visit(c)</code>.</li>
 * </ul>
 */
public class TreeStructureConverter
        extends
        AstVisitor<WtNode>
{
    private static final Pattern ws = Pattern.compile("\\s+");

    private final WikiConfig config;

    private final int wrapCol;

    private StringBuilder sb;

    private StringBuilder line;

    private int extLinkNum;

    /**
     * Becomes true if we are no long at the Beginning Of the whole Document.
     */
    private boolean pastBod;

    private int needNewlines;

    private boolean needSpace;

    private boolean noWrap;

    private LinkedList<Integer> sections;

    private int indent = 0;

    // =========================================================================

    public TreeStructureConverter(WikiConfig config, int wrapCol)
    {
        this.config = config;
        this.wrapCol = wrapCol;
    }

    @Override
    protected WtNode before(WtNode node)
    {
        // This method is called by go() before visitation starts
        sb = new StringBuilder();
        line = new StringBuilder();
        extLinkNum = 1;
        pastBod = false;
        needNewlines = 0;
        needSpace = false;
        noWrap = false;
        sections = new LinkedList<Integer>();
        return super.before(node);
    }

    @Override
    protected Object after(WtNode node, Object result)
    {
        finishLine();

        // This method is called by go() after visitation has finished
        // The return value will be passed to go() which passes it to the caller
        return sb.toString();
    }

    // =========================================================================

    public void visit(WtNode n)
    {
        // Fallback for all nodes that are not explicitly handled below
        println(" + unhandled node: " + n.getNodeName() + "\n");
    }

    public void visit(WtNodeList n)
    {
        println(" + Node List : \n");
        indent++;
        iterate(n);
        indent--;
        println(" - Node List : \n");
    }

    public void visit(WtUnorderedList e)
    {
        println(" + Unordered List : \n");
        indent++;
        iterate(e);
        indent--;
        println(" - Unordered List : \n");
    }

    public void visit(WtOrderedList e)
    {
        println(" + Ordered List : \n");
        indent++;
        iterate(e);
        indent--;
        println(" - Ordered List : \n");
    }

    public void visit(WtListItem item)
    {
        println(" + item : \n");
        indent++;
        iterate(item);
        indent--;
        println(" - item : \n");
    }

    public void visit(EngPage p)
    {
        println(" + Page : \n");
        indent++;
        iterate(p);
        indent--;
        println(" - Page : \n");
    }

    public void visit(WtText text)
    {
        println("-> Text: " + text.getContent());
    }

    public void visit(WtWhitespace w)
    {
        write("_");
    }

    public void visit(WtBold b)
    {
        write("**");
        iterate(b);
        write("**");
    }


    public void visit(WtItalics i)
    {
        write("//");
        iterate(i);
        write("//");
    }

    public void visit(WtXmlCharRef cr)
    {
        write(Character.toChars(cr.getCodePoint()));
    }

    public void visit(WtXmlEntityRef er)
    {
        String ch = er.getResolved();
        if (ch == null)
        {
            write('&');
            write(er.getName());
            write(';');
        }
        else
        {
            write(ch);
        }
    }

    public void visit(WtUrl wtUrl)
    {
        if (!wtUrl.getProtocol().isEmpty())
        {
            write(wtUrl.getProtocol());
            write(':');
        }
        write(wtUrl.getPath());
    }

    public void visit(WtExternalLink link)
    {
        write('[');
        write(extLinkNum++);
        write(']');
    }

    public void visit(WtInternalLink link)
    {
        try
        {
            if (link.getTarget().isResolved())
            {
                PageTitle page = PageTitle.make(config, link.getTarget().getAsString());
                if (page.getNamespace().equals(config.getNamespace("Category")))
                    return;
            }
        }
        catch (LinkTargetException e)
        {
        }

        write(link.getPrefix());
        if (!link.hasTitle())
        {
            iterate(link.getTarget());
        }
        else
        {
            iterate(link.getTitle());
        }
        write(link.getPostfix());
    }

    public void visit(WtSection s)
    {
        println(" + Section (level=" + s.getLevel()  + "):");
        println(" +         (title) :");
        indent++;
        iterate(s.getHeading());
        indent--;
        println(" +         (Body): ");
        indent++;
        iterate(s.getBody());
        indent--;
        println(" - Section");
    }

    public void visit(WtParagraph p)
    {
        iterate(p);
    }

    public void visit(WtHorizontalRule hr)
    {
    }

    public void visit(WtXmlElement e)
    {
        if (e.getName().equalsIgnoreCase("br"))
        {
            newline(1);
        }
        else
        {
            iterate(e.getBody());
        }
    }

    // =========================================================================
    // Stuff we want to hide

    public void visit(WtImageLink n)
    {
    }

    public void visit(WtIllegalCodePoint n)
    {
    }

    public void visit(WtXmlComment n)
    {
    }

    public void visit(WtTemplate n)
    {
        println(" + Template:" + n.getName());
        // println(n.getNodeName());
        println("    - " + String.valueOf(n.getArgs()));
        AstNodePropertyIterator it = n.propertyIterator();
        while(it.next()) {
            println("    - " + it.getName() + " : " + it.getValue());
        }

    }

    public void visit(WtTemplateArgument n)
    {
    }

    public void visit(WtTemplateParameter n)
    {
    }

    public void visit(WtTagExtension n)
    {
    }

    public void visit(WtPageSwitch n)
    {
    }

    // =========================================================================

    private void newline(int num)
    {
        if (pastBod)
        {
            if (num > needNewlines)
                needNewlines = num;
        }
    }

    private void wantSpace()
    {
        if (pastBod)
            needSpace = true;
    }

    private void finishLine()
    {
        sb.append(line.toString());
        line.setLength(0);
    }

    private void writeNewlines(int num)
    {
        finishLine();
        sb.append(StringUtils.strrep('\n', num));
        needNewlines = 0;
        needSpace = false;
    }

    private void writeWord(String s)
    {
        int length = s.length();
        if (length == 0)
            return;

        if (!noWrap && needNewlines <= 0)
        {
            if (needSpace)
                length += 1;

            if (line.length() + length >= wrapCol && line.length() > 0)
                writeNewlines(1);
        }

        if (needSpace && needNewlines <= 0)
            line.append(' ');

        if (needNewlines > 0)
            writeNewlines(needNewlines);

        needSpace = false;
        pastBod = true;
        line.append(s);
    }

    private void write(String s)
    {
        if (s.isEmpty())
            return;

        if (Character.isSpaceChar(s.charAt(0)))
            wantSpace();

        String[] words = ws.split(s);
        for (int i = 0; i < words.length;)
        {
            writeWord(words[i]);
            if (++i < words.length)
                wantSpace();
        }

        if (Character.isSpaceChar(s.charAt(s.length() - 1)))
            wantSpace();
    }

    private void write(char[] cs)
    {
        write(String.valueOf(cs));
    }

    private void write(char ch)
    {
        writeWord(String.valueOf(ch));
    }

    private void write(int num)
    {
        writeWord(String.valueOf(num));
    }

    private void println(String txt) {
        for (int i = 0; i < indent; i++)
            sb.append("  ");
        sb.append(txt).append("\n");
    }

}
