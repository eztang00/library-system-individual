package cosc310_T28_librarySystem;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GoogleStaticMapAPIDisplayer extends JFrame {
    
//    public static void main(String[] args) {
//	try (Scanner scanner = new Scanner(System.in)) {
//	    new GoogleStaticMapAPIDisplayer().showMap(scanner);
//	}
//    }

    public void showMap(Scanner scanner) {
	System.out.println(GoogleTranslateAPILanguageSetter.translate("The map quality is currently not very good. For a better interactive map, use a web browser: ") + "https://goo.gl/maps/uu24BoMZqnp8u1eE7");
	GoogleTranslateAPILanguageSetter.translateAndPrint("Enter starting address for directions (or just press enter): ");
	if (!scanner.hasNextLine()) {
	    return;
	} 
	String address = scanner.nextLine();
	
	try {
	JLabel label = new JLabel(new ImageIcon(GoogleStaticMapAPILink.getMap(address)));
	add(label);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	pack();
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setLocationRelativeTo(null); // center of screen
	setVisible(true);
    }
	    
}
