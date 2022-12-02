package cosc310_T28_librarySystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * 
 * @author lodeous and Team 28
 * copied from lodeous's Github at https://gist.github.com/lodeous/70e36c2ebed76f3efe257204d9cdc375
 * Edited to add different colours and output stream for System.setOut(), also added password capability
 *
 */
public class Console {

    private JFrame consoleFrame;
    private JPanel consolePane;
    private JTextPane outputPane;
    private JTextField inputField;
    private JPasswordField passwordInputField;
    PipedOutputStream outputFromField;
    PipedInputStream inputFromField;
//    private Scanner fieldInput;
    private PrintStream fieldOutput;
    private GridBagConstraints inputFieldConstraints; // used to swap inputField and passwordInputField
    private boolean passwordMode = false;
    ActionListener enterListener;
    

    public void create() {
        //create components
        consoleFrame = new JFrame("Library System");
        outputPane = new JTextPane();
        inputField = new JTextField();
        passwordInputField = new JPasswordField();
        
        

        //Make outputArea read-only
        outputPane.setEditable(false);

        //Set component backgrounds to BLACK and text color to WHITE to make it look more like a console
//        outputPane.setBackground(Color.BLACK);
//        inputField.setBackground(Color.BLACK);
//        outputPane.setForeground(Color.WHITE);
//        inputField.setForeground(Color.WHITE);

        //Setup Piped IO
        outputFromField = new PipedOutputStream();
        inputFromField = new PipedInputStream();
        try {
            outputFromField.connect(inputFromField);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
//        fieldInput = new Scanner(inputFromField);
        fieldOutput = new PrintStream(outputFromField);

        //Setup listeners

        //This listener listens for ENTER key on the inputField.
        enterListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
        	String text = inputField.getText();
        	if (!text.equals("")) {
        	    jTextPaneOutputStream.writeString(text + "\n", Color.GREEN);
        	} else {
        	    char[] password = passwordInputField.getPassword();
        	    text = String.valueOf(password);
        	    Arrays.fill(password, '*');
        	    jTextPaneOutputStream.writeString(String.valueOf(password) + "\n", Color.GREEN);
        	}
        	fieldOutput.println(text);
        	inputField.setText("");
        	passwordInputField.setText("");
        	//Wake up the other thread for an immediate response.
        	synchronized (inputFromField) {
        	    inputFromField.notify();
        	}
            }
        };
        inputField.addActionListener(enterListener);
        passwordInputField.addActionListener(enterListener);

        //Setup Frame for display
        //Add components

        consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        consolePane = new JPanel() {
	    @Override
	    public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = 1; // force it to be small and expand, causes line wrap
		return size;
	    }
        };
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        consolePane.setLayout(new GridBagLayout());
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.weightx = 1;
        layoutConstraints.weighty = 1;
        JPanel whiteSpacePanel = new JPanel();
        whiteSpacePanel.setBackground(Color.WHITE);
        consolePane.add(whiteSpacePanel, layoutConstraints);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.weightx = 1;
        layoutConstraints.weighty = 0;
        consolePane.add(outputPane, layoutConstraints);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 2;
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.weightx = 1;
        layoutConstraints.weighty = 0;
        consolePane.add(inputField, layoutConstraints);
        inputFieldConstraints = layoutConstraints;

	JScrollPane scroll = new JScrollPane(consolePane) {
	    @Override
	    public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.max(size.width, 500);
		size.height = Math.max(size.height, 500);
		return size;
	    }
	};
	consolePane.addComponentListener(new ComponentListener() {
	    @Override
	    public void componentShown(ComponentEvent e) {
	    }

	    @Override
	    public void componentResized(ComponentEvent e) {
		// credit to
		// https://stackoverflow.com/questions/2483572/making-a-jscrollpane-automatically-scroll-all-the-way-down
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
		scroll.getHorizontalScrollBar().setValue(0);
	    }

	    @Override
	    public void componentMoved(ComponentEvent e) {
	    }

	    @Override
	    public void componentHidden(ComponentEvent e) {
	    }
	});
	consoleFrame.setLayout(new BorderLayout());
	consoleFrame.add(scroll, BorderLayout.CENTER);
	consoleFrame.pack();

	// make inputField focused by default
	// credit:
	// http://www.java2s.com/Code/Java/Event/SettingtheInitialFocusedComponentinaWindow.htm
	inputField.addHierarchyListener(new HierarchyListener() {
	    @Override
	    public void hierarchyChanged(HierarchyEvent e) {
		inputField.requestFocus();
	    }
	});


        consoleFrame.setLocationRelativeTo(null); // center of computer screen
        consoleFrame.setVisible(true);
        
        jTextPaneOutputStream = new JTextPaneOutputStream(outputPane);

        //set system in
        setSystemOutAndSystemIn();

    }
    public void dispose() {
	consoleFrame.dispose();
    }

    JTextPaneOutputStream jTextPaneOutputStream;
    public void setSystemOutAndSystemIn() {
      System.setOut(new PrintStream(jTextPaneOutputStream));
      System.setIn(inputFromField);
    }
    
    public void setPasswordMode(boolean passwordMode) {
	if (passwordMode && !this.passwordMode) {
	    consolePane.remove(inputField);
	    consolePane.add(passwordInputField, inputFieldConstraints);
	    passwordInputField.requestFocus();
	} else if (!passwordMode && this.passwordMode) {
	    consolePane.remove(passwordInputField);
	    consolePane.add(inputField, inputFieldConstraints);
	    inputField.requestFocus();
	}
	this.passwordMode = passwordMode;
    }
    
    public void addLanguageButtons(ActionListener listener) {
	JMenu languageMenu = new JMenu();
	ButtonGroup radioButtonSelectionShare = new ButtonGroup();
	for (String s : new String[] {"enEnglish", "frFrancais", "esEspanol", "zhChinese", "deDeutsch", "itItaliano", "nlNederlands", "daDansk", "noNorsk", "svSvenska", "ptPortugues", "ruRussian", "fiSuomi", "plPolski", "bgBulgarian", "csCesky"}) {
	    JRadioButtonMenuItem languageMenuItem = new JRadioButtonMenuItem(new AbstractAction(s.substring(2)) {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            languageMenu.setText(s.substring(2));
	            listener.actionPerformed(e);
	        }
	    });
	    languageMenuItem.setActionCommand(s.substring(0, 2));
	    if (s.startsWith(GoogleTranslateAPILanguageSetter.currentLanguage)) {
		languageMenuItem.setSelected(true);
                languageMenu.setText(s.substring(2));
	    }
	    languageMenu.add(languageMenuItem);
	    radioButtonSelectionShare.add(languageMenuItem);
	}
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(languageMenu);
	
	consoleFrame.add(menuBar, BorderLayout.SOUTH);
	consoleFrame.pack();
    }


//    public static void main(String[] args) {
//        try {
//            //Run GUI Creation code on the AWT Event dispatching thread.
//            //Needs to use invoke and wait otherwise scanner created on System.in might fail
//	    SwingUtilities.invokeAndWait(new Runnable() {
//	        @Override
//	        public void run() {
//	            Console console = new Console();
//	            console.create();
//	        }
//	    });
//	} catch (InvocationTargetException | InterruptedException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
//        new Thread() {
//            public void run() {
//        	
//        	try (Scanner s = new Scanner(System.in)) {
//        	    while (s.hasNextLine()) {
//        		String line = s.nextLine();
//        		System.out.println("Program recieved input: "+line);
//        	    }
//        	}
//            }
//        }.start();
//    }
}