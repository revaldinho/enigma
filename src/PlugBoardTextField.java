import javax.swing.*;
import javax.swing.text.*;
import java.awt.Toolkit; 

/** Extend the JTextField to restrict entry to 2 characters of only the letters a-z and automatically switch any entry
    to upper case */
public class PlugBoardTextField extends JTextField {
     public PlugBoardTextField() {
         super();
    }
 
     protected Document createDefaultModel() {
 	      return new PlugBoardDocument();
     }
 
     static class PlugBoardDocument extends PlainDocument {
         private static boolean usedChars[] = new boolean[26];
         
         public void insertString(int offs, String str, AttributeSet a) 
             throws BadLocationException {
             StringBuffer sb = new StringBuffer();            
             if (str == null) {
                 return;
             }
             if ( str.length() + getLength() > 2 ) {
                      Toolkit.getDefaultToolkit().beep();
             } else {                 
                 // Restrict length to 2 and upper case letters only, don't allow duplicated within 1 field
                 String existing = this.getText(0,this.getLength());
                 char[] upper = str.toCharArray();
                 
                 for (int i = 0; i < upper.length; i++) {
                     if ( Character.isLetter(upper[i]) ) {
                         upper[i] = Character.toUpperCase(upper[i]);
                         if ( existing.indexOf(upper[i]) <0 ) {
                             sb.append(upper[i]);
                         }
                     }
                 }
                 // System.out.println("Adding string to textField "+sb.toString());
                 // Check that the new chars are not duplicates

                 super.insertString(offs, sb.toString(), a);
                 
             }
         }
     }
}

 