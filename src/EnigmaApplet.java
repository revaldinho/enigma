import java.awt.* ;
import java.applet.* ;

public class EnigmaApplet extends javax.swing.JApplet {
    public EnigmaApplet() {
        getContentPane().add( new EnigmaApp().getContentPane());
    }
}