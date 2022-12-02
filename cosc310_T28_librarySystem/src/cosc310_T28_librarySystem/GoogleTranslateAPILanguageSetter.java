package cosc310_T28_librarySystem;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * 
 * @author Ethan
 *
 */
public class GoogleTranslateAPILanguageSetter {
    /*
     * needs to be static because System.out.println is static.
     */
    static String currentLanguage = "en";
    static ArrayList<ActionListener> onRestart = new ArrayList<ActionListener>();
    static LoadingFrame loadingFrame = new LoadingFrame();
    public static ActionListener languageChangeListener = e -> {
	
	String oldLanguage = currentLanguage;
	currentLanguage = e.getActionCommand();
	if (currentLanguage != oldLanguage) {
//	    JOptionPane.setDefaultLocale(new Locale(currentLanguage));
	    UIManager.put("OptionPane.cancelButtonText", translate("Cancel"));
	    UIManager.put("OptionPane.noButtonText", translate("No"));
	    UIManager.put("OptionPane.okButtonText", translate("Okay"));
	    UIManager.put("OptionPane.yesButtonText", translate("Yes"));
	    int option = JOptionPane.showConfirmDialog(null, translate("The language was changed. Do you want to restart the session, and discard all the changes you made in the old language? Choosing \"no\" will change the language without restarting."), translate("Restart sesson?"), JOptionPane.YES_NO_CANCEL_OPTION);
	    switch (option) {
	    case JOptionPane.YES_OPTION:
		onRestart.forEach(l -> l.actionPerformed(e));
		break;
	    case JOptionPane.NO_OPTION:
		break;
            default: //cancel or close
		currentLanguage = oldLanguage;
//        	JOptionPane.setDefaultLocale(new Locale(oldLanguage));
        	break;
	    }
	}
    };
    public static String translate(String input) {
	if (currentLanguage.equals("en")) {
	    return input;
	} else {
	    try {
		loadingFrame.setVisible(true);
		((JComponent) loadingFrame.getContentPane()).paintImmediately(((JComponent) loadingFrame.getContentPane()).getBounds());
		String s = GoogleTranslateAPILink.translate("en", currentLanguage, input);
		return s.replaceAll("&#39;", "'").replaceAll("&quot;", "\"");
	    } catch (IOException e) {
		return "translation error, no internet?";
	    } finally {
		loadingFrame.setVisible(false);
	    }
	}
    }
    public static void translateAndPrint(String input) {
	System.out.print(translate(input));
    }
    public static void translateAndPrintln(String input) {
	System.out.println(translate(input));
    }
    static class LoadingFrame extends JFrame {
	{
	    add(new JLabel("Loading Google Translate API..."), BorderLayout.CENTER);
	    pack();
	    setLocationRelativeTo(null); // make it centered
	    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
    }
    public static void addRestartListener(ActionListener listener) {
	onRestart.add(listener);
    }
}
