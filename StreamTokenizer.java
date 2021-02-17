/*
 * Copyright (c) 1995, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.base;

import java.io.*;
import java.util.Arrays;

/**
 * The {@code StreamTokenizer} class takes an input stream and
 * parses it into "tokens", allowing the tokens to be
 * read one at a time. The parsing process is controlled by a syntax tree
 * and a number of flags that can be set to various states. The
 * stream tokenizer can recognize identifiers, numbers, quoted
 * strings, and various comment styles. 
 * <p>
 * A <b>typical usecase</b> is to
 * first create an instance of this class and repeatedly loop calling the
 * {@code nextToken} method in each iteration of the loop until
 * it returns the value {@code TT_EOF}.
 * <p>
 * The syntax tree defines how the parser reacts to each character.
 * Each ordinary character is seen as its own token.
 * Consecutive word characters are grouped together into a single token.
 * Numeric characters are grouped together into a single token. The values of 
 * these tokens are stored in the fields {@code sval} and {@code nval} respectively.
 * Whitespace characters seperate tokens such as word and number tokens.
 * Comment characters sepcify the start of a single-line comment, which
 * are ignored until the end of the line.
 * Quote characters wrap string literals, which are seen as a single token and whose value is stored in {@code sval}.
 * <p>
 * The <b>default syntax tree</b> recognized by the {@code StreamTokenizer} is:
 * <ul>
 *  <li> Word Characters as 'a-z' and 'A-Z' (combinations of these characters get parsed together as words) </li>
 *  <li> Numeric Characters as 0-9, "-", and "." (combinations of these characters get parsed together as numbers) </li>
 *  <li> Comment char as / (this has the parser ignore everything until the next newline) </li>
 *  <li> Single Quote chars as \' and "
 * </ul>
 * Whitespace Characters as ASCII values from 0 to 32 (these serve to separate tokens).
 * </p>
 * <p>
 * <b>To get started</b>, follow this example to get an understanding of the key ideas of Stream Tokenizer
 * <blockquote><pre>
      // create a new tokenizer
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.txt"));
      Reader r = new BufferedReader(new InputStreamReader(ois));
      StreamTokenizer st = new StreamTokenizer(r);
         
      // in here we will repeatedly call st.nextToken() until we hit the end of the file
      do {
         // call st.nextToken to parse the next token, 
         st.nextToken();
         // we  check the type of the token parsed with st.ttype (word, number, character, EOL, etc.)
         switch(st.ttype) {
            case StreamTokenizer.TT_EOL:
               System.out.println("End of Line encountered.");
               break;
               
            // if it is a word then st.ttype will be TT_WORD and st.sval will have the value of the word as a string
            case StreamTokenizer.TT_WORD:
               System.out.println("Word: " + st.sval);
               break;
               
            // if it is a number then st.ttype will be TT_NUMBER as st.nval will have the value of the number as a double
            case StreamTokenizer.TT_NUMBER:
               System.out.println("Number: " + st.nval);
               break;
               
            default:
            // if an ordinary character is encountered, its value will be stored in the st.ttype field
               System.out.println((char) st.ttype + " encountered.");
         }
         }
      } while (st.ttype != TT_EOF);
    }
}</pre></blockquote>
 * This is where we would put a state diagram, however we couldn't
 * figure out how so it's in the other submission file.
 * 
 * This class is <b>not thread safe</b>.
 */
public class StreamTokenizer {
    /**
     * This field contains the type of the token most recently read by 
     * a call to {@code nextToken}. The value of this field will be
     * <ul>
     *   <li>{@code TT_WORD} if the last token was a word. </li>
     *   <li>{@code TT_NUMBER} if the last token was a number. </li>
     *   <li>{@code TT_EOL} if an end of line has been read (note this this field
     *        will only have this value if {@code eolIsSignficicant} has been called with
     *        {@code true} as the argument, otherwise end of lines will be ignored).</li>
     *   <li>{@code TT_EOF} if the end of the input stream has been reached.</li>
     *   <li><i>ord(single character)</i> - if a single character has been read. For example 
     *   if {@code '+'} is read the value of {@code ttype} will be {@code 43}. Note in the default configuration
     *   the standard 'abc...zAB...Z' will regarded as words, not characters. So if {@code 'a'} is read the value
     *   of {@code ttype} will be {@code TT_WORD}.</li>
     *   <li><i>ord(quoteCharacter)</i> - if a quoted string is encountered. For example, in the default configuration,
     *   if {@code 'T'} or {@code 'ABCD'} are read, then value of {@code ttype} will be {@code 39}.
     * <ul>
     */
    public int ttype = TT_NOTHING;

    /**
     * A constant indicating the end of the stream has been read.
     */
    public static final int TT_EOF = -1;

    /**
     * A constant indicating the end of a line has been read.
     */
    public static final int TT_EOL = '\n';

   /**
     * A constant indicating a number token has been read.
     */
    public static final int TT_NUMBER = -2;

    /**
     * A constant indicating a word token has been read.
     */
    public static final int TT_WORD = -3;

    /**
     * If the last read token is a word, this field contains a string
     * giving the characters of that word token. If the last read token is a 
     * quoted string token. the field contains the body of the string. If the method
     * {@code lowerCaseMode} has been called with the argument {@code true}, then the string
     * in this field will be lowercased. If the last token read is not a word token, the value of
     * this field will be unchanged. The initial value of this field is {@code null}.
     * <p>
     * An example use of this field
     * {@code
     *   if (st.ttype == TT_WORD) {
            System.out.println(st.sval);
        }
     * }
     */

    public String sval;

    /**
     * If the last read token is a number, this fields contains the value 
     * of that number. By default the standard 0, 1, ..., 9, ., -, are parsed
     * as numeric characters. The last read token is a number when the value
     * of {@code ttype} is {@code TT_NUMBER}. The initial value of this field is {@code 0.0}.
     * <p>
     * An example use of this field
     * {@code
         if (st.ttype == TT_NUMBER) {
            System.out.println(st.nval + 10)
         }
     * }
     */

    public double nval;

    /**
     * Creates a stream tokenizer that parses the given input stream.
     * @deprecated Prefer creating {@code StreamTokenizer} with the {@code Reader}
     * <p> 
     * An example instantiation if you have an input stream:
     * <blockquote><pre>
     * Reader r = new BufferedReader(new InputStreamReader(is));
   StreamTokenizer st = new StreamTokenizer(r);</pre></blockquote>
     * @param is an input stream
     * @see java.io.StreamTokenizer#StreamTokenizer(Reader)
     */

    public StreamTokenizer(InputStream is) {}

    /**
     * Creates a stream tokenizer to parse the given character stream. A typical 
     * instantiation of this class
     * <blockquote><pre>
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("inputFile.txt"));
        Reader r = new BufferedReader(new InputStreamReader(ois));
        StreamTokenizer st = new StreamTokenizer(r);</pre></blockquote>
     *
     * <p>
     * The default synxtax is achieved by
     * @param r is a Reader object providing the input stream for stream tokenizer to parse
     */

   public StreamTokenizer(Reader r) {}

    /**
     * Sets this tokenizer's syntax table so that all characters are
     * "ordinary." Note this does not set the syntax table to the default
     * outlined in the {@code StreamTokenizer} summary, as the default
     * recognizes some characters as whitespace, comments, and numbers.
     * See the {@code ordinaryChar} method
     * for more information on a character being ordinary.
     *
     * @see     java.io.StreamTokenizer#ordinaryChar(int)
     */
    public void resetSyntax() {}

    /**
     * Specifies that all characters <i>c</i> in the range
     * {@code low <= c <= high}
     * are word constituents. Consecutive word consitutents will be
     * recognized as a single token. 
     *
     * <p>Any other attribute settings for the characters in the specified
     * range are cleared.
     * 
     * <p>
     * For example, in the default configuration {@code "a", "b", "c", ..., "z", "A", ... "Z"} are 
     * all word consitutents.
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     */
    public void wordChars(int low, int hi) {}

    /**
     * Specifies that all characters <i>c</i> in the range
     * {@code low <= c <= high}
     * are white space characters. White space characters serve only to
     * separate tokens in the input stream.
     *
     * <p>Any other attribute settings for the characters in the specified
     * range are cleared.
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     */
    public void whitespaceChars(int low, int hi) {}

    /**
     * Specifies that all characters <i>c</i> in the range
     * {@code low <= c <= high}
     * are "ordinary" in this tokenizer. See the
     * {@code ordinaryChar} method for more information on a
     * character being ordinary.
     *
     * <p>Any other attribute settings for the characters in the specified
     * range are cleared.
     *
     * @param   low   the low end of the range.
     * @param   hi    the high end of the range.
     * @see     java.io.StreamTokenizer#ordinaryChar(int)
     */
    public void ordinaryChars(int low, int hi) {}

    /**
     * Specifies that the character argument is "ordinary"
     * in this tokenizer. It removes any special significance the
     * character has as a comment character, word component, string
     * delimiter, white space, or number character. When such a character
     * is encountered by the parser, the parser treats it as a
     * single-character token and sets {@code ttype} field to the
     * character value.
     *
     * <p>Making a line terminator character "ordinary" may interfere
     * with the ability of a {@code StreamTokenizer} to count
     * lines. The {@code lineno} method may no longer reflect
     * the presence of such terminator characters in its line count.
     *
     * <p>Any other attribute settings for the specified character are cleared.
     *
     * @param   ch   the character.
     * @see     java.io.StreamTokenizer#ttype
     */
    public void ordinaryChar(int ch) {}

    /**
     * Specifies that the character argument starts a single-line
     * comment. All characters from the comment character to the end of
     * the line are ignored by this stream tokenizer.
     *
     * <p>Any other attribute settings for the specified character are cleared.
     *
     * @param   ch   the character.
     */
    public void commentChar(int ch) {}

    /**
     * Specifies that matching pairs of this character delimit string
     * constants in this tokenizer.
     * <p>
     * When the {@code nextToken} method encounters a string
     * constant, the {@code ttype} field is set to the string
     * delimiter and the {@code sval} field is set to the body of
     * the string.
     * <p>
     * If a string quote character is encountered, then a string is
     * recognized, consisting of all characters after (but not including)
     * the string quote character, up to (but not including) the next
     * occurrence of that same string quote character, or a line
     * terminator, or end of file. The usual escape sequences such as
     * {@code "\u005Cn"} and {@code "\u005Ct"} are recognized and
     * converted to single characters as the string is parsed.
     *
     * <p>Any other attribute settings for the specified character are cleared.
     *
     * @param   ch   the character.
     * @see     java.io.StreamTokenizer#nextToken()
     * @see     java.io.StreamTokenizer#sval
     * @see     java.io.StreamTokenizer#ttype
     */
    public void quoteChar(int ch) {}

    /**
     * Specifies that numbers should be parsed by this tokenizer. The
     * syntax table of this tokenizer is modified so that each of the twelve
     * characters:
     * <blockquote><pre>
     *      0 1 2 3 4 5 6 7 8 9 . -
     * </pre></blockquote>
     * <p>
     * has the "numeric" attribute. <b>Note:</b> These are parsed as numbers by default.
     * <p>
     * When the parser encounters a word token that has the format of a
     * double precision floating-point number, it treats the token as a
     * number rather than a word, by setting the {@code ttype}
     * field to the value {@code TT_NUMBER} and putting the numeric
     * value of the token into the {@code nval} field.
     *
     * @see     java.io.StreamTokenizer#nval
     * @see     java.io.StreamTokenizer#TT_NUMBER
     * @see     java.io.StreamTokenizer#ttype
     */
    public void parseNumbers() {}

    /**
     * Determines if ends of line should be treated as tokens. By default ends of lines
     * are not treated as tokens. If {@code eolIsSignificant} is called with the argument
     * {@code true}, end of lines ({@code '\u005Cr'}, {@code '\u005Cn'}, {@code '\u005Cr\u005Cn'}) get treated
     * as tokens and {@code nextToken} returns {@code TT_EOL} and {@code ttype} is set to {@code TT_EOL} when
     * these tokens are read.
     * <p>
     * If {@code eolIsSignificant} is called with the argument {@code false}, ends of line are treated
     * only as whitespace to separate tokens. 
     * </p>
     * 
     * @param flag {@code true} indicates the end of line characters are their own separate token; {@code false} indicates
     * end of line characters should just be treated as whitespace.
     */

    public void eolIsSignificant(boolean flag) {}

    /**
     * Determines if the tokenizer recognizes C-style. By default the stream tokenizer does not
     * recognize C-style comments. If {@code slashStarComments} is called with the argument {@code true}, then
     * all text bettween {@code /*} and <code>*&#47;</code> is ignored.
     * <p>
     * For example if C-style comments are regonized on the string {@code "a /* bcdef} <code>*&#47</code> {@code g"} by
     * a {@code StreamTokenizer st}, the first call to {@code st.nextToken()} will result in {@code st.sval == "a"} 
     * and the second call will result in {@code st.sval == "g"}. 
     * </p>
     * <p>
     * If {@code slashStartComments} is called with the argument {@code false}, then C-style slash star comments
     * are not treated specially.
     * </p>
     * <p>
     * Note: with the default configuration, '/' is a comment char so '/*' will ignore to end of line anyway, but 
     * <code>*&#47;</code> will not stop the comment. Only a new line will.
     * </p>
     * 
     * @param flag {@code true} indicates to recognize C-style comments. {@code false} indicates to not
     * recognize C-style comments.
     */

    public void slashStarComments(boolean flag) {}

    /**
     * Determines if the tokenizer recognizes C++-style comments. By default the stream tokenizer does
     * not recognize C++-style comments. If {@code slashSlashComments} is called with the argument {@code true}, then
     * any two occurrences of the slash character ({@code '//''}) is treated as the beginning of a comment, and the stream
     * is ignored until the next new line character. 
     * <p>
     * If {@code slashSlashComments} is called with the argument {@code false}, then C++-style comments are
     * not treated specially.
     * </p>
     * <p>
     * Note: in the default configuration, '/' is a comment char so '//' will be ignored anyway. Also calling
     * {@code slashSlashComments(false)} will not change that '/' is a comment character. To do this you must call
     * {@code resetSyntax} and choose a new comment character. However doing this will make all characters ordinary
     * so then you must set your desired syntax.
     * 
     * @param flag {@code true} indicates to recognize C++-style comments. {@code false} indicates to not
     * recognize C++-style comments.
     * </p>
     */

    public void slashSlashComments(boolean flag) {}

    /**
     * Determines if word tokens should be automatically lowercased in the {@code sval} field. 
     * If {@code lowerCaseMode} is called with the argument {@code true}, then any subsequent
     * calls to {@code nextToken} that parse a word token, will have the parsed result lowercased
     * in {@code sval}.
     * <p>
     * If {@code lowerCaseMode} is called with the argument {@code false}, then after a word token is parsed, its
     * value in {@code sval} is exactly the parsed value.
     * </p>
     * 
     * @param flag {@code true} indicates that all word tokens should be lowercased. {@code false} indicates that 
     * word tokens should remain as the parsed.
     */

    public void lowerCaseMode(boolean fl) {}
   /**
   * Parses the next token from the input stream, returning the type of the token in the {@code ttype} field.
   * More information about the return value of this function can be found in the {@code ttype} filed documentation.
   * 
   * <p>
   * A typical use of this of this function:
   * {@code ObjectInputStream ois = new ObjectInputStream(new FileInputStream("inputFile.txt"));
        Reader r = new BufferedReader(new InputStreamReader(ois));
        StreamTokenizer st = new StreamTokenizer(r);}
        do {
           st.nextToken();
           // do something here with the st.ttype, st.nval, and st.sval fields
           if (st.ttype == TT_WORD) {
              System.out.println(st.sval) // prints the token if it was a word token
           }
        } while (st.ttype != TT_EOF)
      }
   * 
   * @return the value of the {@code ttype field}
   * @exception IOException if an I/O error occurs
   * @see java.io.StreamTokenizer#ttype
   */
    public int nextToken() throws IOException {}

    /**
     * Causes the following call of the nextToken method of this tokenizer not increment to the next token and
     * return ttype of the current token - all fields will remain the same.
     */
    public void pushBack() {}

    /**
     * Returns current line number as long as the new line characters have not been modified by {@code ordinaryChar}. 
     * This method may not return the correct line number in the case {@code ordinaryChar} has made a line termination character "ordinary".
     * 
     * @return the current line number of the string tokenizer
     */
    public int lineno() {}

    /**
     * Returns a string representation of the current stream token and the line number it occurs on.
     * There are a few possible formats for the return shape of the string based on {@code ttype} and the current line number. 
     * Some typical examples include:
     * <ul>
     *  <li>EOF - {@code Token[EOF], line 3} </li>
     *  <li>EOL - {@code Token[EOL], line 3}</li>
     *  <li>Ordinay Char - {@code Token['+'], line 9}</li>
     *  <li>Word - {@code Token[abc], line 5}</li>
     *  <li>Number - {@code Token[n=123.456], line 2}</li>
     * </ul>
     */
    public String toString() {}

}