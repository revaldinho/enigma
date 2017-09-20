
// Import standard classes
import java.io.*;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.util.*;
import java.lang.reflect.*;
 
public class EnigmaApp extends JFrame implements ActionListener {
    // PlainText area for typing and loading saved files into
    protected JTextArea plainText = new JTextArea(0,0);
    // Common GUI Items for machine initialisation
    protected JTextField[] plugPair = new JTextField[12];
    protected JComboBox rotorType[] = new JComboBox[3];
    protected JComboBox rotorRing[] = new JComboBox[3];
    protected JComboBox rotorStart[] = new JComboBox[3];
    // Menu GUI Items
    protected JMenuBar mBar;
    protected JMenuItem saveCfgItem, openCfgItem;
    protected JMenuItem savePlainTextItem, openPlainTextItem;
    protected JMenuItem saveCipherTextItem;
    protected JMenuItem quitItem, aboutItem, prefsItem;
    protected JMenuItem cutItem, pasteItem, selectAllItem, copyItem;
    // Machine specific initialisation items
    protected JComboBox m3ReflectorType  ;
    protected JComboBox m3ReflectorSetting ;
    protected JComboBox m4ReflectorType ;
    protected JComboBox m4ReflectorSetting ;
    protected JComboBox m4ThinRotorType ;
    protected JComboBox m4ThinRotorRing ;
    protected JComboBox m4ThinRotorStart ;
    protected JTabbedPane machineOptionTabs;
    // Dialogs
    protected JDialog aboutBox;
    protected JDialog prefsBox;
    protected JRadioButton none, five, four;

    // Other components which can change during program run
    protected JTextArea cipherText; 
    protected JLabel    statusLabel = new JLabel( "AAA", SwingConstants.CENTER);
    protected JPanel    m3Panel;
    protected JPanel    m4Panel;
    protected Properties p = new Properties();
    protected enigma enigmaInst;
    protected int prefsGroupSize = 0;
    protected File lastDirectoryUsed;

    // Check that we are on Mac OS X. 
    public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    public static boolean WINDOWS = (System.getProperty("os.name").toLowerCase().startsWith("windows"));
    public static boolean useFileDialog = MAC_OS_X || WINDOWS;
    // Ask AWT which menu modifier we should be using.
    public static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    // Good practice to declare a serialVersionUID if the class is serializable
    private static final long serialVersionUID = 0;

    /** Constructs the JFrame with the split pane interface */
    public EnigmaApp() {
        super("EnigmaApp");

        this.addWindowListener(new WindowAdapter(  ) {
                public void windowClosing(WindowEvent we) { System.exit(0); }
            });
        this.setSize(800, 600);
        this.setLocation(200, 200);
        addMenus();
        createDialogs() ;

        // Create components for the Application Panel
        JScrollPane plainTextsp = new JScrollPane(plainText, 
                                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        plainText.setLineWrap(true);
        plainText.setFont( new Font("monospaced", Font.PLAIN, 14 ));
        plainTextsp.setBorder(BorderFactory.createTitledBorder("Plaintext"));
        plainTextsp.setPreferredSize(new Dimension(400,250));
        
        JPanel appButtons = new JPanel( );
        JButton encipherButton = new JButton("Encipher/Decipher") ;
        appButtons.add( encipherButton);
        appButtons.setMaximumSize( new Dimension(600,100));
        
        cipherText = new JTextArea(0,0);
        cipherText.setFont( new Font("monospaced", Font.PLAIN, 14 ));
        cipherText.setEditable(false);
        cipherText.setLineWrap(true);
        
        JScrollPane cipherTextsp = new JScrollPane(cipherText, 
                                                   JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                                   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        cipherTextsp.setBorder(BorderFactory.createTitledBorder("Ciphertext"));
        cipherTextsp.setPreferredSize(new Dimension(400,250));
        
        // Build up the Application Panel, which will be the upper left pane
        Box appPanel = new Box(BoxLayout.Y_AXIS);
        appPanel.add(plainTextsp);
        appPanel.add(appButtons);
        appPanel.add(cipherTextsp);
        appPanel.setMinimumSize(new Dimension(300,400));
        
        JPanel plugPanel = new JPanel (new GridLayout(3,4));
        plugPanel.setBorder(BorderFactory.createTitledBorder("Plugboard Settings"));

        for ( int i = 0 ; i < 12 ; i++ ) {
            plugPair[i] = new PlugBoardTextField();
            plugPanel.add(plugPair[i]);
        }        
        
        JPanel rotorPanel = new JPanel ( new GridLayout( 5,1));
        rotorPanel.setBorder(BorderFactory.createTitledBorder("Rotor Selection"));
        
        JPanel rotorLabels = new JPanel ( new GridLayout( 1, 4));
        rotorLabels.add(new JLabel(""));
        rotorLabels.add(new JLabel("Rotor 1"));
        rotorLabels.add(new JLabel("Rotor 2"));
        rotorLabels.add(new JLabel("Rotor 3"));
        rotorPanel.add(rotorLabels);
        
        String [] alphabetItems = new String[26];
        for ( char i = 'A' ; i <= 'Z' ; i++ ) {
            alphabetItems[i-'A'] = new String( Character.toString(i));
        }
        
        JPanel rotorC = new JPanel(new GridLayout( 1,3));
        String [] items = new String[] { "I", "II", "III", "IV", "V","VI", "VII" };
        rotorC.add( new JLabel("Type"));
        for ( int i = 0 ; i < 3 ; i++ ) {
            rotorType[i] = new JComboBox( items);
            rotorType[i].setBorder(BorderFactory.createEtchedBorder(  ));
            rotorC.add(rotorType[i]);
        }
        rotorPanel.add(rotorC);
        
        JPanel ringP = new JPanel(new GridLayout( 1,4));
        ringP.add(new JLabel("Ring"));
        
        for ( int i = 0 ; i < 3 ; i++ ) {
            rotorRing[i] = new JComboBox( alphabetItems);
            rotorRing[i].setBorder(BorderFactory.createEtchedBorder(  ));
            ringP.add(rotorRing[i]);
        }
        rotorPanel.add(ringP);
        
        JPanel startP = new JPanel(new GridLayout( 1,4));
        startP.add(new JLabel("Initial"));
        for ( int i = 0 ; i < 3 ; i++ ) {
            rotorStart[i] = new JComboBox( alphabetItems);
            rotorStart[i].setBorder(BorderFactory.createEtchedBorder(  ));
            startP.add(rotorStart[i]);
        }
        rotorPanel.add(startP);
        
        // create Machine specific Panels with the UKW and thin rotor options
        m3Panel = new JPanel(new GridLayout(4,3) );
        
        String [] m3ReflectorItems = new String[] {"A", "B", "C" };
        m3Panel.add(new JLabel(""));
        m3Panel.add(new JLabel("Reflector"));
        m3Panel.add(new JLabel(""));
        
        m3Panel.add(new JLabel("Type"));
        m3ReflectorType = new JComboBox( m3ReflectorItems);
        m3Panel.add(m3ReflectorType);
        // Pad out the rest of the panel
        for ( int j = 0 ; j< 7 ; j++ ) {
            m3Panel.add(new JLabel(""));
        }     
        
        m4Panel = new JPanel(new GridLayout( 4,3));
        String [] m4ReflectorItems = new String[] {"B-thin", "C-thin" };
        String [] m4ThinRotorItems = new String[] {"beta", "gamma" };
        m4Panel.add(new JLabel(""));
        m4Panel.add(new JLabel("Reflector"));
        m4Panel.add(new JLabel("Rotor 0"));
        m4Panel.add(new JLabel("Type"));
        m4ReflectorType = new JComboBox( m4ReflectorItems);
        m4Panel.add(m4ReflectorType);
        m4ThinRotorType = new JComboBox( m4ThinRotorItems);
        m4Panel.add(m4ThinRotorType);
        m4Panel.add(new JLabel("Ring"));
        m4Panel.add(new JLabel(""));
        m4ThinRotorRing = new JComboBox( alphabetItems);
        m4Panel.add(m4ThinRotorRing);
        m4Panel.add(new JLabel("Setting"));
        m4Panel.add(new JLabel(""));
        m4ThinRotorStart = new JComboBox( alphabetItems);
        m4Panel.add(m4ThinRotorStart);
        
        // Create the tabbed pane and add the machine specific TABs
        machineOptionTabs = new JTabbedPane(  );
        machineOptionTabs.setBorder(BorderFactory.createTitledBorder("Machine Specific Options"));
        machineOptionTabs.addTab("M3", m3Panel);
        machineOptionTabs.addTab("M4", m4Panel);
        
        // Create a simple machine status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createTitledBorder("Machine Status"));
        
        statusPanel.add( statusLabel);
        
        // Create the Machine initialisation Panel
        Box initPanel = new Box(BoxLayout.Y_AXIS);
        initPanel.add(machineOptionTabs);
        initPanel.add(rotorPanel);
        initPanel.add(plugPanel);
        initPanel.setMinimumSize(new Dimension(300,500));
        initPanel.setPreferredSize(new Dimension(320,400));
        
        // Split the application and initialisation panels across the screen
        JSplitPane splitty = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,appPanel,initPanel);
        splitty.setDividerLocation( splitty.getSize().width - 320 - splitty.getInsets().right - splitty.getDividerSize() );
        splitty.setResizeWeight(1.0);
         // Create a vertical split between the status panel and the paired application and initialising panels
        JSplitPane splitty1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,splitty, statusPanel);
        splitty1.setDividerLocation(splitty1.getInsets().top + splitty1.getSize().height - splitty1.getDividerSize() - 50);
        splitty1.setResizeWeight(1.0);
        

        // Set up some button actions once all objects are defined
        encipherButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    // Reset the machine to the GUI defined state
                    initialiseMachine();
                    // Now run the translation
                    cipherText.setText( enigmaInst.encipher(plainText.getText()));
                    statusLabel.setText( enigmaInst.toString());
                }
            });

        this.getContentPane(  ).add(splitty1);
               
        // setup defaults before showing the GUI
        p.setProperty("TYPE","M3");
        p.setProperty("REFLECTOR","B");
        p.setProperty("START","AAAA");
        p.setProperty("ROTORS","1234");
        p.setProperty("RINGS","AAAA");
        p.setProperty("GROUP", "4");
        p.setProperty("PLUGS","");
        initialiseGUIFromProperties();    

        // Create the actual enigma instance
        enigmaInst = new enigma(p);       
    
       
    }

    public void createDialogs() {
        aboutBox = new JDialog(this);
        aboutBox.getContentPane().setLayout( new BorderLayout());

        // Setup the AboutBox Content
        JEditorPane ta = new JEditorPane();
        ta.setContentType("text/html");
        ta.setEditable(false);
        ta.setText("<HTML><body><center><h1>EnigmaApp</h1></p><p>v0.40</p><hr><p>\251 Richard Evans (richevans@mac.com), 2007</p><p>OSXAdapter \251 Apple Computer, Inc., 2003-2006</p></center></body></HTML>");       

        aboutBox.getContentPane().add(ta, BorderLayout.CENTER);

        // Setup the preferences panel
        prefsBox = new JDialog(this, "Options");
        if ( MAC_OS_X ) {
            prefsBox.setTitle("Preferences");
        } 
        
        prefsBox.getContentPane().setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setBorder( BorderFactory.createTitledBorder("Ciphertext Letter Grouping"));
        ButtonGroup group = new ButtonGroup();
        none = new JRadioButton("None", prefsGroupSize!=4 && prefsGroupSize!=5);
        four = new JRadioButton("Four", prefsGroupSize==4);
        five = new JRadioButton("Five", prefsGroupSize==5);
        group.add(none);
        group.add(four);
        group.add(five);
        p.add(none);
        p.add(four);
        p.add(five);
        prefsBox.getContentPane().add(p, BorderLayout.CENTER);
        // Declare a local actionlistener for the preferences events
        ActionListener prefsListener = new ActionListener() { 
                public void actionPerformed( ActionEvent ae ) {
                    JRadioButton item = (JRadioButton)ae.getSource();
                    prefsGroupSize = (item == five)? 5 : ( item== four) ? 4 : 0;
                    cipherText.setText( enigma.groupChars( cipherText.getText(), prefsGroupSize));
                }
            };
        none.addActionListener(prefsListener);
        four.addActionListener(prefsListener);
        five.addActionListener(prefsListener); 
    }

    /** Set all GUI items based on values in the properties object (usually following Open Configuration...) */
    public void initialiseGUIFromProperties() {
        int offset = 0;
        if ( p.getProperty("TYPE", "M3").equalsIgnoreCase("M4") ) {
            machineOptionTabs.setSelectedComponent(m4Panel);
            m4ReflectorType.setSelectedIndex((p.getProperty("REFLECTOR", "B")).charAt(0)-'D');
            m4ThinRotorType.setSelectedIndex((p.getProperty("ROTORS","B123")).charAt(0)-'B');
            m4ThinRotorRing.setSelectedIndex((p.getProperty("RINGS","AAAA")).charAt(0)-'A');
            m4ThinRotorStart.setSelectedIndex((p.getProperty("START","AAAA")).charAt(0)-'A');
            offset = 1;
        } else {
             machineOptionTabs.setSelectedComponent(m3Panel);
            m3ReflectorType.setSelectedIndex((p.getProperty("REFLECTOR", "B")).charAt(0)-'A');     
        }

        for ( int i=0 ; i<3 ; i++ ) {
            rotorType[i].setSelectedIndex((p.getProperty("ROTORS","1123")).charAt(i+offset)-'1');
            rotorRing[i].setSelectedIndex((p.getProperty("RINGS","AAAA")).charAt(i+offset)-'A');
            rotorStart[i].setSelectedIndex((p.getProperty("START","AAAA")).charAt(i+offset)-'A');
        }

        prefsGroupSize = Integer.parseInt(p.getProperty("GROUP","0"));

        // Simple setup of plugs with basic error checking
        char [] plugs = (p.getProperty("PLUGS","")).toCharArray();
        StringBuffer sb = new StringBuffer();
        // Clear the field first
        for ( int i=0 ; i< plugPair.length ; i++ ) {
            plugPair[i].setText("");
        }
        for ( int i=0, j=0 ; i< plugs.length ; i++ ) {
            if ( Character.isLetter( plugs[i] ) ){
                sb.append( Character.toUpperCase(plugs[i]));
            }
            if ( sb.length() == 2 ) {
                plugPair[j++].setText( sb.toString());
                sb.delete(0, 2);
            }
        }
        // Don't forget the preferences buttons
        none.setSelected( prefsGroupSize!=4 && prefsGroupSize!=5);
        four.setSelected( prefsGroupSize==4);
        five.setSelected( prefsGroupSize==5);

    }

    /** Read all GUI settings into the property object to initialise the Enigma machine */
    public void setPropertiesFromGUI() {
        StringBuffer rings = new StringBuffer();
        StringBuffer start = new StringBuffer();
        StringBuffer rotors = new StringBuffer();
        
        // Reset the enigma machine to the indicated state first
        if ( machineOptionTabs.getSelectedComponent().equals(m4Panel) ) {
            p.setProperty("TYPE", "M4");
            char c = (char)(m4ReflectorType.getSelectedIndex()+'D');
            p.setProperty("REFLECTOR", Character.toString(c));
            rotors.append((char)(m4ThinRotorType.getSelectedIndex()+'B'));
            rings.append((char)(m4ThinRotorRing.getSelectedIndex()+'A'));
            start.append((char)(m4ThinRotorStart.getSelectedIndex()+'A'));
            
        } else {
            p.setProperty("TYPE", "M3");
            p.setProperty("REFLECTOR",(String)m3ReflectorType.getSelectedItem());
        }
        
        // Warn if a rotor type is selected several times
        if ( rotorType[0].getSelectedIndex() == rotorType[1].getSelectedIndex() ||
             rotorType[2].getSelectedIndex() == rotorType[1].getSelectedIndex() ||
             rotorType[0].getSelectedIndex() == rotorType[2].getSelectedIndex() ) {
            JOptionPane.showMessageDialog(this, "One type of rotor has been selected more than once.", "Replicated Rotor Setting", JOptionPane.WARNING_MESSAGE);
        }

        for ( int j=0 ; j< 3 ; j++ ) {
            rotors.append((char)(rotorType[j].getSelectedIndex()+'1'));
            rings.append((char)(rotorRing[j].getSelectedIndex()+'A'));
            start.append((char)(rotorStart[j].getSelectedIndex()+'A'));
        }
        p.setProperty("ROTORS", rotors.toString());
        p.setProperty("RINGS", rings.toString());
        p.setProperty("START", start.toString());
        p.setProperty("GROUP", Integer.toString(prefsGroupSize));
        // Get the plugboard data        
        p.setProperty("PLUGS", getPlugSettings( plugPair));
        // set the GUI again in case the plugs data was cleaned up
        initialiseGUIFromProperties();
    }
    public  void initialiseMachine() {
       setPropertiesFromGUI();
        // Clear the enciphered text field
        cipherText.setText("");
        // Initialize the machine to the starting state
        p.list(System.err);
        enigmaInst.reset(p);                   
    }

    /** Create all Menus */
    private void addMenus() {
        mBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        // Create the menus
        fileMenu.add( openPlainTextItem = new JMenuItem("Open Plaintext..."));
        fileMenu.add( savePlainTextItem = new JMenuItem("Save Plaintext As..."));
        fileMenu.add( saveCipherTextItem = new JMenuItem("Save Ciphertext As..."));
        fileMenu.addSeparator();
        fileMenu.add( openCfgItem = new JMenuItem("Open Configuration..."));
        fileMenu.add( saveCfgItem = new JMenuItem("Save Configuration As..."));
        // Quit menu item is provided on Mac OS X.. only make it on other platforms.
        if (!MAC_OS_X) {
            fileMenu.addSeparator();
            fileMenu.add( quitItem = new JMenuItem("Quit"));
            quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MENU_MASK));
        }

        // Associate the actions with the menu items
        for ( int i = 0 ;  i < fileMenu.getItemCount() ; i++ ) {
            JMenuItem item = fileMenu.getItem(i);
            if ( item != null ) {
                (item).addActionListener( this );
            }
        }
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add( cutItem = new JMenuItem() );
        editMenu.add( copyItem = new JMenuItem() );
        editMenu.add( pasteItem = new JMenuItem() );
        editMenu.add( selectAllItem = new JMenuItem("Select All") );
        if ( !MAC_OS_X ) {
            editMenu.addSeparator();
            editMenu.add ( prefsItem = new JMenuItem("Options"));
            prefsItem.addActionListener( this);
        }

        copyItem.setAction(new DefaultEditorKit.CopyAction());
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_MASK));
        copyItem.setText("Copy");
        cutItem.setAction(new DefaultEditorKit.CutAction());
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));
        cutItem.setText("Cut");
        pasteItem.setAction(new DefaultEditorKit.PasteAction());
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_MASK));
        pasteItem.setText("Paste");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MENU_MASK));
        selectAllItem.setEnabled(false);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");	
        JMenuItem onlineDocItem = new JMenuItem("Online Documentation");
        JMenuItem techSupportItem = new JMenuItem("Technical Support");
        helpMenu.add(onlineDocItem);
        onlineDocItem.setEnabled(false);
        helpMenu.addSeparator();
        helpMenu.add(techSupportItem);
        techSupportItem.setEnabled(false);

	// About menu item is provided on Mac OS X.. only make it on other platforms.
        if (!MAC_OS_X) {
            helpMenu.addSeparator();
            helpMenu.add( aboutItem = new  JMenuItem("About EnigmaApp..."));
            aboutItem.addActionListener( this );
        } else {
            // Set up our application to respond to the Mac OS X application menu
            macOSXRegistration();
        }
        // Add the menus to the frame
        mBar.add(fileMenu);
        mBar.add(editMenu);
        mBar.add(helpMenu);
        setJMenuBar (mBar);
    }
    

    // General info dialog.  The OSXAdapter calls this method when "About OSXAdapter" 
    // is selected from the application menu.
    public void about() {
        aboutBox.setSize(350, 200);
        aboutBox.setLocation((int)this.getLocation().getX() + 22, (int)this.getLocation().getY() + 22);
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);
    }
    // General preferences dialog.  The OSXAdapter calls this method when "Preferences..." 
    // is selected from the application menu.
    public void preferences() {
        prefsBox.setSize(320, 140);
        prefsBox.setLocation((int)this.getLocation().getX() + 22, (int)this.getLocation().getY() + 22);
        prefsBox.setResizable(false);
        prefsBox.setVisible(true);
    }
    // General info dialog.  The OSXAdapter calls this method when "Quit OSXAdapter" 
    // is selected from the application menu, Cmd-Q is pressed, or "Quit" is selected from the Dock.
    public void quit() {	
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    /** Combine all strings from the plug board text fields into one */
    public  String getPlugSettings( JTextField plugs[] ) {
        StringBuffer pb = new StringBuffer();
        boolean [] used = new boolean [26];
        for ( int i=0 ; i < 26 ; i++ ) {
            used [i] = false;
        }

        boolean plugsAllValid = true;
        for ( int i=0; i< plugs.length ; i++ ) {
            String s =  plugs[i].getText();
            boolean valid =true;
            if ( s.equals("") ) {
                // Ignore blank text fields
            } else if ( s.length()  != 2  ) {
                // All others should have exactly two characters
                valid = false;
            } else {
                for ( int j=0; j<2 ; j++ ) {
                    if ( used[s.charAt(j)-'A'] ) {
                        valid = false;
                    } else {
                        used[s.charAt(j)-'A'] = true;
                    }
                }
            }
            if ( valid ) {
                pb.append(s);
            } else {
                plugsAllValid = false;
            }
        }
        if ( ! plugsAllValid ) {
            JOptionPane.showMessageDialog(this, "Duplicate pairs and singleton entries in the plugboard are ignored.", "Errors in Plugboard Settings", JOptionPane.WARNING_MESSAGE);
        }
        return pb.toString();
    }



   /** Return a File with the chosen file for reading, or null if cancel was pressed. */
    public File SelectFile(JMenuItem item, boolean openNotSave) {
        File f = null;
        if ( useFileDialog) {
            FileDialog fd;
            if ( openNotSave) {
                fd = new FileDialog(this, "Open File...", FileDialog.LOAD);
            } else {
                fd = new FileDialog(this, "Save File...", FileDialog.SAVE);
            }
            if ( lastDirectoryUsed != null ) {
                fd.setDirectory( lastDirectoryUsed.getAbsolutePath());
            }
            fd.setVisible(true);
            String fileName, dir;
            fileName = fd.getFile();
            dir = fd.getDirectory();
            if ( fileName != null ) {
                f =  new File( dir, fileName) ;
            } 
        } else {
            JFileChooser chooser = new JFileChooser(lastDirectoryUsed);
            ExampleFileFilter filter = new ExampleFileFilter();
            filter.addExtension("txt");
            if ( item == openCfgItem ) {
                filter.addExtension("cfg");
            }
            filter.setDescription("Text Files");
            chooser.setFileFilter(filter);
            int returnVal;
            if ( openNotSave ) {
                returnVal = chooser.showOpenDialog(this);
            } else {
                returnVal = chooser.showSaveDialog(this);
            }
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                f = chooser.getSelectedFile();
            } 
        }
        return f;
    }

    /** Menu actions are dealt with by this method (from the ActionListener interface)*/
    public void actionPerformed(ActionEvent e ) {
        JMenuItem item = (JMenuItem)e.getSource();
        if ( item == openPlainTextItem || item == openCfgItem || item == savePlainTextItem || item == saveCfgItem || item == saveCipherTextItem) {
            boolean openNotSave = ( item == openPlainTextItem || item == openCfgItem);
            File f = SelectFile(item, openNotSave);
            if ( f != null ) {
                lastDirectoryUsed = f.getParentFile();
                try {                         
                    if ( item == openPlainTextItem ) {
                        plainText.setText( readFileToString( f  ));
                    } else if ( item == openCfgItem) {
                        p.load(new FileInputStream( f ));
                        initialiseGUIFromProperties();                             
                    } else if ( item == savePlainTextItem) {
                        FileWriter out = new FileWriter(f);
                        out.write(plainText.getText());
                        out.close();
                    } else if ( item == saveCipherTextItem)  {
                        FileWriter out = new FileWriter(f);
                        out.write(cipherText.getText());
                        out.close();
                    } else {
                        setPropertiesFromGUI();
                        p.store( new FileOutputStream(f) , "Properties file from EnigmaApp" );
                    }
                } catch ( IOException ioe) {
                    JOptionPane.showMessageDialog(this, "Could not" + ( (openNotSave)?" read ":" write ")+"file "+f.getName(), "File Error", JOptionPane.ERROR_MESSAGE);     
                }
            }
        } else if ( item == aboutItem ) {
            about();
        } else if ( item == prefsItem ) {
            preferences();
        } else if ( item == quitItem ) {
            quit();
        }
    }

    // Generic registration with the Mac OS X application menu.  Checks the platform, then attempts
    // to register with the Apple EAWT.
    // This method calls OSXAdapter.registerMacOSXApplication() and OSXAdapter.enablePrefs().
    // See OSXAdapter.java for the signatures of these methods.
    public void macOSXRegistration() {
            if (MAC_OS_X) {
                    try {
                            Class osxAdapter = ClassLoader.getSystemClassLoader().loadClass("OSXAdapter");

                            Class[] defArgs = {EnigmaApp.class};
                            Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);
                            if (registerMethod != null) {
                                    Object[] args = { this };
                                    registerMethod.invoke(osxAdapter, args);
                            }
                            // This is slightly gross.  to reflectively access methods with boolean args, 
                            // use "boolean.class", then pass a Boolean object in as the arg, which apparently
                            // gets converted for you by the reflection system.
                            defArgs[0] = boolean.class;
                            Method prefsEnableMethod =  osxAdapter.getDeclaredMethod("enablePrefs", defArgs);
                            if (prefsEnableMethod != null) {
                                    Object args[] = {Boolean.TRUE};
                                    prefsEnableMethod.invoke(osxAdapter, args);
                            }
                    } catch (NoClassDefFoundError e) {
                            // This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
                            // because OSXAdapter extends ApplicationAdapter in its def
                            System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
                    } catch (ClassNotFoundException e) {
                            // This shouldn't be reached; if there's a problem with the OSXAdapter we should get the 
                            // above NoClassDefFoundError first.
                            System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
                    } catch (Exception e) {
                            System.err.println("Exception while loading the OSXAdapter:");
                            e.printStackTrace();
                    }
            }
    }
 
    /** Read an entire file into a String */
    public static String readFileToString( File f ) throws IOException {
        StringBuffer sb = new StringBuffer();
        char[] b = new char[4096];
        int n;
        Reader r = new FileReader( f  ) ;
        while ((n = r.read(b) ) > 0 ) {
            sb.append(b, 0, n);
        }
        return sb.toString();
    }
 

    /** main class can take 1 argument -javaLNF to force the Java look'n'feel */
    public static void main(String[] args) {
        for (int s = 0 ; s < args.length ; s++ ) {
            if ( args[s].equalsIgnoreCase("-javaLnF") ) {
                try {
                    UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName());
                    MENU_MASK = Event.CTRL_MASK;
                    MAC_OS_X = false;                    
                } catch (Exception ee) {
                }
            } else if  ( args[s].equalsIgnoreCase("-useLnF") && s < (args.length-1) ) {
                String lnfClass = args[++s];
                try {
                    UIManager.setLookAndFeel(lnfClass);
                    if ( lnfClass.indexOf("Aqua") < 0 ) {
                        MENU_MASK = Event.CTRL_MASK;
                        MAC_OS_X = false;
                    }
                } catch (Exception ee) {
                }
            } else if ( args[s].equalsIgnoreCase("-showAvailableLNF")){
                UIManager.LookAndFeelInfo inf []  = UIManager.getInstalledLookAndFeels();
                System.err.println("Available GUI Look & Feels are:");
                for ( int i = 0 ; i < inf.length ; i++ ) {            
                    System.err.println("  "+inf[i].getClassName());
                }
            } else if ( args[s].equalsIgnoreCase("-useFileDialog")){
                useFileDialog = true;
            } else if ( args[s].equalsIgnoreCase("-useJFileChooser")){
                useFileDialog = false;
            }
        }

        if ( MAC_OS_X) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "EnigmaApp");
        }
        EnigmaApp ea = new EnigmaApp();
        ea.setVisible(true);
    }

}
