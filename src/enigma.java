import java.io.*;
import java.util.*;

/**  Simulation of a 3 or 4 rotor Enigma machine.
*/

public class enigma {


    // Rotor positions are numbered r0 (nearest the reflector) to r3 (nearest the plugboard). r3 rotates every cycle 
    public rotor [] r = new rotor[4];
    public rotor reflector;
    public boolean m4NotM3 = false;
    public int numRotors = 3;
    public char  [] plugBoard = new char[26];
    public static int groupSize = 0;
    
    /** Construct a new machine from the given properties */
    public enigma(Properties p) {
        reset(p);
    }

    /** Reset an existing machine to a new setting */
    public void reset (Properties p) {
        m4NotM3 = (p.getProperty("TYPE", "M3")).equalsIgnoreCase("M4");
        numRotors = ( m4NotM3) ? 4 : 3 ; 
        String ringSetting = p.getProperty("RINGS", "AAAA");
        String startSetting = p.getProperty("START", "AAAA");
        String rotorSetting = p.getProperty("ROTORS", "1234");
        String plugBoardSetting = validatedString(p.getProperty("PLUGS", ""));
        String reflectorSetting = p.getProperty("REFLECTOR","B");
        String groupSizeSetting = p.getProperty("GROUP", "4");
        // Conventionally M3 decodings group in 4's, M4 in 5's
        groupSize = Integer.parseInt(groupSizeSetting);

        // Select the rotors, 0-8 for the normal set, B&C for the thin rotors
        for ( int k= 0 ; k < numRotors ; k++ ) {
            if (rotorSetting.charAt(k)  > '0' && rotorSetting.charAt(k) < '9' ) {
                r[k] = new rotor (startSetting.charAt(k), ringSetting.charAt(k), rotorSetting.charAt(k) -'1');
            } else if ( Character.toUpperCase(rotorSetting.charAt(k)) == 'B' ) {
                r[k] = new rotor (startSetting.charAt(k), ringSetting.charAt(k), rotor.ROTOR_BETA ) ;
            } else if (  Character.toUpperCase(rotorSetting.charAt(k)) == 'C' ) {
                r[k] = new rotor (startSetting.charAt(k), ringSetting.charAt(k), rotor.ROTOR_GAMMA ) ;
            } else {
                System.err.println( "Error: character "+rotorSetting.charAt(k)+" is not a valid rotor selector in  ROTORS="+rotorSetting);
                System.exit(1);
            }
        }
        // Always ignore the ring setting for the reflector - the ring is fixed on this part.
        reflector = new rotor('A', 'A' ,reflectorSetting.charAt(0) -'A' + rotor.REFLECTOR_A );
        // Set up the plugboard swapping only the pairs in the input string
        for ( int i = 0 ; i < 26 ; i++ ) {
            plugBoard[i] = (char)(i+'A');
        }
        for ( int i = 0 ; i < plugBoardSetting.length()-1 ; i+= 2 ) {
            plugBoard[ plugBoardSetting.charAt(i) -'A' ] = plugBoardSetting.charAt(i+1);
            plugBoard[ plugBoardSetting.charAt(i+1) -'A' ] = plugBoardSetting.charAt(i);
        }               
    }

   /** Encipher a string of upper case characters. Ignore anything else */
    public String encipher(String mesg) {
        Stopwatch st = new Stopwatch();
        st.start();
        StringBuffer cipherText = new StringBuffer();
        char c;
 
        for ( int i=0 ; i< mesg.length() ; i++ ) {
            c = mesg.charAt(i);
            if ( Character.isLetter(c)) {           
                advance();
                c =  encipherChar(Character.toUpperCase(c)) ;
                cipherText.append( c ) ;
            }          
        }
        st.stop();

        System.err.println("Enciphered "+ cipherText.length() +" characters in "+st.toString()+" Final positions: "+this.toString());
        return groupChars( cipherText.toString(), groupSize);  
    }
    
    /** Encipher a single upper case character.*/
    private char encipherChar( char c ) {
        char enc ;
        int val = (int) plugBoard[ c - 'A' ] - 'A'; 
        for (int idx = numRotors-1 ; idx>=0 ; idx--) {
            val = r[idx].passForward(val);
        }
        val = reflector.passBackward(val);
        for (int idx = 0 ; idx < numRotors ; idx++ ) {
            val = r[idx].passBackward(val);
        }
        enc = plugBoard[ val ];
        if ( enc == c ) {
            System.out.println("ERROR encoded char as itself!");
        }
        return enc;
    }

    /** Move the machine on ready for the next character. 

        Note that rotor 2 moves either when rotor 3's notch is engaged or when rotor 1 moves, so that this
        rotor will move on successive steps when it engages with rotor 1. */
    public void advance() {
        boolean rotate2, rotate1;
        // On an M3 machine the moving rotors are (slowest) 0..2 (fastest)
        // On an M4 machine the moving rotors are (slowest) 1..3 (fastest) because rotor0 is static
        int leftmost = (m4NotM3)? 1: 0;
        // Check if notches are engaged first
        rotate1 = r[leftmost+1].notchEngaged;
        rotate2 = r[leftmost+2].notchEngaged | rotate1 ; 
        // Always rotote rightmost rotor
        r[leftmost+2].advance(1);
        // If notch was engaged to adjacent rotor then advance that one too
        if ( rotate2 ) { 
            r[leftmost+1].advance(1);
        }
        // Only advance leftmost rotor when its own notch is engaged
        if ( rotate1 ) {
            r[leftmost].advance(1);
        }
    }


    /** Report a simple status line */
    public String toString() {
        StringBuffer s = new StringBuffer();
        for ( int i=0 ; i < numRotors ; i++ ) {
            s.append(r[i].indicatedPosition());
        }
        return ( s.toString());
    }

    /** Enigma can be run as a standalone command line program using the main method.

    <p>Usage: java enigma -settings <filename.cfg> [-text "string" ]|[-file "filename"]

    <p>The encrypted text is sent to stdout.
   
    */
    public static void main(String[] args) {
        enigma e;
        String plainText = new String("");    
       // Setup some defaults before parsing the arguments
        Properties prop = new Properties();
        prop.setProperty("RINGS", "AAAA");
        prop.setProperty("TYPE", "M3");
        prop.setProperty("START", "AAAA");
        prop.setProperty("ROTORS", "1234");
        prop.setProperty("REFLECTOR", "B");
        prop.setProperty("GROUP","4");
        prop.setProperty("PLUGS", "");
        // Parse Arguments
        for (int s = 0 ; s < args.length ; s++ ) {
            if ( args[s].equalsIgnoreCase("-text") && s< (args.length-1)) {
                plainText = new String( args[++s]);
            } else if ( args[s].equalsIgnoreCase("-file") && s < (args.length-1)) {
                plainText = new String( args[++s]);
                StringBuffer plainTextBuffer = new StringBuffer();
                try{
                    String line;
                    BufferedReader f = new BufferedReader( new FileReader( plainText )) ;
                    while ( ( line = f.readLine())!= null ) {
                        plainTextBuffer.append(validatedString(line));
                    }
                } catch ( IOException ioe) {
                    System.err.println("Could not open file "+plainText+" for reading");
                    System.exit( 1);
                }
                
                plainText = plainTextBuffer.toString();
            } else if ( args[s].equalsIgnoreCase("-settings") && s < (args.length-1)) {
                // Read a machine initialisation file
                String settingsFile = new String( args[++s]);
                try {
                    prop.load( new FileInputStream(settingsFile )) ;
                } catch ( IOException ioe ) {
                    System.err.println("Can't read file "+settingsFile);
                }
                //p.list( System.err ) ;
            }            
        }    
        e = new enigma(prop);

        StringBuffer ct = new StringBuffer();
        ct.append(new String(e.encipher(plainText.toUpperCase())));  
        System.out.println(ct.toString());
     }
        
    /** Group output characters separated by spaces */
    public static String groupChars( String s, int groupSize ) {
        StringBuffer sb = new StringBuffer();

        if ( groupSize <= 0  ) {
            for ( int i=0 ; i < s.length() ; i++ ) {
                if ( Character.isLetter(s.charAt(i))) { 
                    sb.append(s.charAt(i));
                }
            }
        } else {
            for (int i=0, j=0 ; i< s.length(); i++ ) {
                if ( Character.isLetter(s.charAt(i) )){
                    if ( j>0 && ((j%groupSize)==0) ) {
                        sb.append(' ');
                    }
                    sb.append(s.charAt(i));
                    j++;
                }
            }
        }
        return sb.toString();

    }

    /** Strip a string of all but alphabetic characters */
    public static String validatedString( String s ) {
        StringBuffer sb = new StringBuffer();
        for ( int i=0; i< s.length() ; i++ ) {
            char c = Character.toUpperCase(s.charAt(i));
            if ( c >= 'A' && c<= 'Z' ) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
