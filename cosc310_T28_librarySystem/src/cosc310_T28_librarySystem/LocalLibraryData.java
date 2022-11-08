package cosc310_T28_librarySystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Team 28
 * 
 *         The LocalLibraryData stores the entire collection of these objects in
 *         ArrayList and HashMap collections, and currently has no methods.
 */
public class LocalLibraryData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Store the user and manager data 
    public HashMap<String, User> userAccounts;
    public HashMap<String, Manager> managerAccounts;
    // All book in the library
    public ArrayList<Book> bookList;
    // Only book can lend 
    public ArrayList<Book> freeToLend;
    // Books applied for borrowing
    public ArrayList<Book> readyToLend;
    // Books not in library, Lending
    public ArrayList<Book> lended;

    public LocalLibraryData() {
	
	userAccounts = new HashMap<>();
	managerAccounts = new HashMap<>();
	bookList = new ArrayList<>();
	freeToLend = new ArrayList<>();
	readyToLend = new ArrayList<>();
	lended = new ArrayList<>();
    }

}
