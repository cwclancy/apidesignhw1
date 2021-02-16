import java.io.StreamTokenizer;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;

import static java.io.StreamTokenizer.*;

import java.io.BufferedReader;

class Tokenize {
    public static void main(String [] args) throws Exception {
        String text = "a 'Hello'. 'T'his is text * * + + \n . \r that will 12-34 be split into tokens abc123 123abc. 1.3453 + 1 = 2 a//gg";

        FileOutputStream out = new FileOutputStream("test.txt");
        ObjectOutputStream oout = new ObjectOutputStream(out);

        oout.writeUTF(text);
        oout.flush();
        oout.close();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("test.txt"));

        Reader r = new BufferedReader(new InputStreamReader(ois));

        StreamTokenizer st = new StreamTokenizer(r);
        st.eolIsSignificant(false);
        st.slashStarComments(false);
        st.slashSlashComments(false);
        st.lowerCaseMode(true);
        st.parseNumbers();

        if (st.ttype == TT_WORD) {
            System.out.println(st.sval);
        }
        do {
            st.nextToken();
            System.out.printf("%d %d: %s%n", st.ttype, st.lineno(), lastTokenToString(st));
        } while (st.ttype != TT_EOF);
    }

    private static String lastTokenToString(StreamTokenizer st) {
        int tt = st.ttype;
        return tt == TT_WORD           ? "Word: "   + st.sval  :
               tt == TT_NUMBER         ? "Number: " + st.nval  :
               tt == TT_EOL            ? "End of line"         :
               tt == TT_EOF            ? "End of file"         :
               tt == '"' || tt == '\'' ? String.format("Quoted string: %s%s%s", (char) tt, st.sval, (char) tt) :
               String.format("Ordinary: " + (char) tt);
    }
}