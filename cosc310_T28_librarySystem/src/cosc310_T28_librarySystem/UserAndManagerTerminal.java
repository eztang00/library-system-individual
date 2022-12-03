package cosc310_T28_librarySystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserAndManagerTerminal extends Thread {
    LocalLibraryData localLibraryData;
    Console console;
    Scanner scanner;
    boolean restart = false;

    public UserAndManagerTerminal() {
	super();
    }

    public void start() {
	GoogleTranslateAPILanguageSetter.addRestartListener(e -> restart()); //needed to restart after changing langage
	super.start();
    }

    /**
     * This thread will will keep asking the user for input and replying to all the
     * input.
     * 
     * This function is automatically run by the thread after start() is called.
     */
    public void run() {
	do {
	    restart = false;
	    runOnce();
	    console.dispose();
	} while (restart); // loop for restarting if change language
    }
    private void runOnce() {
	/*
	 * NOTE: when testing make all passwords "testpassword". First username should be "test"
	 */
		boolean finishedWithoutInterruption = false; // this boolean tells the try-finally clause whether
						     // scanner.hasNextLine()
						     // failed

	console = new Console();
	console.create();
	console.addLanguageButtons(GoogleTranslateAPILanguageSetter.languageChangeListener);
    
	try (Scanner scanner = new Scanner(System.in)) {

        this.scanner = scanner;
        
        localLibraryData = loadSession("main.ser");
        if (localLibraryData == null) {
                localLibraryData = new LocalLibraryData();
        }

        Account currentAccount=null;
        GoogleTranslateAPILanguageSetter.translateAndPrintln("Welcoming to Team 28's Library System (version 0.1).");
        

        GoogleTranslateAPILanguageSetter.translateAndPrintln("Library User please enter 1, Manager (Librarian) please enter 2.");
        boolean isManager=true;

        String AccountTypeSelection = scanner.nextLine(); //Judge user type
        while(!AccountTypeSelection.equals("1")&&!AccountTypeSelection.equals("2")){
            GoogleTranslateAPILanguageSetter.translateAndPrintln("Please only enter 1 or 2.");
            AccountTypeSelection = scanner.nextLine();
        }
        switch (AccountTypeSelection) {
            case "1":
            isManager=false;
                break;
            case "2":
            isManager=true;
                break;
        }
        GoogleTranslateAPILanguageSetter.translateAndPrintln("Log in to an account, please enter 1.\nCreate a new account, please enter 2.");
        String LogInTypeSelection = scanner.nextLine(); //Select to log in or create an account
        while(!LogInTypeSelection.equals("1")&&!LogInTypeSelection.equals("2")){
            GoogleTranslateAPILanguageSetter.translateAndPrintln("Please only enter 1 or 2.");
            LogInTypeSelection = scanner.nextLine();
        }
        
        switch (LogInTypeSelection) {   
            case "1":
            if(isManager==true)
            currentAccount = tryLoggingInManager(scanner, localLibraryData);
            if(isManager==false)
            currentAccount = tryLoggingInUser(scanner, localLibraryData);
            if (currentAccount == null) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Login failed. Exiting.");
                        finishedWithoutInterruption = true;
                        return;
            }
                break;
            case "2":
            currentAccount = tryCreatingAccount(scanner, localLibraryData, isManager);
            if (currentAccount == null) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Account creation failed. Exiting.");
                        finishedWithoutInterruption = true;
                        return;
            }
            if(isManager)
            localLibraryData.managerAccounts.put(currentAccount.getUsername(), (Manager) currentAccount);
            else
            localLibraryData.userAccounts.put(currentAccount.getUsername(), (User)currentAccount);
            
                break;
            }
            
       
	    
	    //this while loop is the main loop for the user or manager to do any command
            UserOrManagerCommandResult userOrManagerCommandResult;
	    do {
		userOrManagerCommandResult = askAndDoNextUserOrManagerCommand(scanner, localLibraryData, currentAccount);	
	    } while (userOrManagerCommandResult != UserOrManagerCommandResult.EXIT);

	    finishedWithoutInterruption = true;
	} catch (NoSuchElementException e) {
	} finally {
	    if (!finishedWithoutInterruption) {
		GoogleTranslateAPILanguageSetter.translateAndPrintln("Program interrupted. Exiting.");
	    }else{GoogleTranslateAPILanguageSetter.translateAndPrintln("Shut down the system");}
	}
    }
    // Create a new account 
    private Account tryCreatingAccount(Scanner scanner, LocalLibraryData localLibraryData, boolean isManager) {
        String username = null;
        while (username == null) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Username: ");
            if (!scanner.hasNextLine()) {
                return null;
            } 
            String usernameEntered = scanner.nextLine();
            if (!usernameEntered.matches("^[a-zA-Z0-9 _-]*$")) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("The username must contain only letters, numbers, _- symbols, or spaces.");
            } else if (!(1 <= usernameEntered.length() && usernameEntered.length() <= 99)) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("The username must be 1 to 99 characters long.");
            } else if (localLibraryData.managerAccounts.containsKey(usernameEntered) && localLibraryData.userAccounts.containsKey(usernameEntered)) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("The username already exists.");
            } else {
        	username = usernameEntered;
            }
        }
        String password = null;
        while (password == null) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Password: ");
            console.setPasswordMode(true);
            if (!scanner.hasNextLine()) {
                return null;
            }
            String passwordEntered = scanner.nextLine();
            console.setPasswordMode(false);
            if (!passwordEntered.matches("^[a-zA-Z0-9 _-`~!@#$%^&*()=+\\[{\\]}\\\\|;:'\",<.>/?]*$")) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("The password must contain only letters, numbers, _-`~!@#$%^&*()=+[{]}\\|;:'\",<.>/? symbols, or spaces.");
            } else if (!(1 <= passwordEntered.length() && passwordEntered.length() <= 99)) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("The password must be 1 to 99 characters long.");
            } else {
        	password = passwordEntered;
            }
        }
        Account newAccount;
        if (isManager) {
            newAccount = new Manager(username, password, 2); //name manager type to '2'
            GoogleTranslateAPILanguageSetter.translateAndPrintln("The manager account " + username + " was created successfully.");
        } else {
            newAccount = new User(username, password, 1);    //name user type to '1'
            GoogleTranslateAPILanguageSetter.translateAndPrintln("The user account " + username + " was created successfully.");
        }
        return newAccount;
    }
    private Account tryLoggingInUser(Scanner scanner, LocalLibraryData localLibraryData) {
        GoogleTranslateAPILanguageSetter.translateAndPrintln("Please log in to an account.");
        Account accountToLogIn = null;
        while (accountToLogIn == null) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Username: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String usernameEntered = scanner.nextLine();
            if (localLibraryData.userAccounts.containsKey(usernameEntered)) {
        	accountToLogIn = localLibraryData.userAccounts.get(usernameEntered);
            } else {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Account not found.");
            return null;
            }
        }
        while (true) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Password: ");
            console.setPasswordMode(true);
            if (!scanner.hasNextLine()) {
                return null;
            }
            String passwordEntered = scanner.nextLine();
            console.setPasswordMode(false);
            if (!accountToLogIn.passwordEquals(passwordEntered)) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Password incorrect.");
            } else {
        	return accountToLogIn;
            }
        }
    }
    private Account tryLoggingInManager(Scanner scanner, LocalLibraryData localLibraryData) {
        GoogleTranslateAPILanguageSetter.translateAndPrintln("Please log in to an account.");
        Account accountToLogIn = null;
        while (accountToLogIn == null) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Username: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String usernameEntered = scanner.nextLine();
            if (localLibraryData.managerAccounts.containsKey(usernameEntered)) {
        	accountToLogIn = localLibraryData.managerAccounts.get(usernameEntered);
            } else {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Account not found.");
            return null;
            }
        }
        while (true) {
            GoogleTranslateAPILanguageSetter.translateAndPrint("Password: ");
            console.setPasswordMode(true);
            if (!scanner.hasNextLine()) {
                return null;
            }
            String passwordEntered = scanner.nextLine();
            console.setPasswordMode(false);
            if (!accountToLogIn.passwordEquals(passwordEntered)) {
        	GoogleTranslateAPILanguageSetter.translateAndPrintln("Password incorrect.");
            } else {
        	return accountToLogIn;
            }
        }
    }

    //Input the corresponding number realization function
    private UserOrManagerCommandResult askAndDoNextUserOrManagerCommand(Scanner scanner, LocalLibraryData localLibraryData, Account currentAccount) {
        GoogleTranslateAPILanguageSetter.translateAndPrintln("Welcome " + currentAccount.getUsername() + ". What would you like to do? Enter a number to make a selection.");
        if (currentAccount instanceof Manager) {
            GoogleTranslateAPILanguageSetter.translateAndPrintln("1: search for a book");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("2: checkout book on hold to lend");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("3: save session");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("4: add a new book from library");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("5: delete a book");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("6: return a lended book");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("7: Google Map directions to library");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("8: quit and save");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("9: quit without save");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String selection = scanner.nextLine();
            switch (selection) {
                case "1":
                    currentAccount.searchForABook(scanner, localLibraryData, false);
                    break;
                case "2":
                    ((Manager) currentAccount).checkoutBook(scanner, localLibraryData);
                    break;
                case "3":
                    saveSession(scanner, localLibraryData, "main.ser");
                    break;
                case "4":
                    ((Manager) currentAccount).addBook(scanner, localLibraryData);
                    break;
                case "5":
                    ((Manager)currentAccount).delBook(scanner, localLibraryData);;
                    break;
                case "6":
                    ((Manager) currentAccount).returnBook(scanner, localLibraryData);
                    break;
                case "7":
                    new GoogleStaticMapAPIDisplayer().showMap(scanner);
                    break;
                case "8":
                    saveSession(scanner, localLibraryData, "main.ser");
                    return UserOrManagerCommandResult.EXIT;
                case "9":
                    return UserOrManagerCommandResult.EXIT; 
        	default:
                    GoogleTranslateAPILanguageSetter.translateAndPrintln("Selection unavailable");
                    break;
            }
        } else if (currentAccount instanceof User) {
            
            GoogleTranslateAPILanguageSetter.translateAndPrintln("1: search for a book");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("2: apply to lend a book");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("3: Google Map directions to library");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("4: save session");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("5: quit and save");
            GoogleTranslateAPILanguageSetter.translateAndPrintln("6: quit without save");

            if (!scanner.hasNextLine()) {
                return null;
            }
            String selection = scanner.nextLine();
            switch (selection) {
                case "1":
                    currentAccount.searchForABook(scanner, localLibraryData, false);
                    break;
                case "2":
                    ((User) currentAccount).borrowBook(scanner, localLibraryData);
                    break;
                case "3":
                    new GoogleStaticMapAPIDisplayer().showMap(scanner);
                    break;
                case "4":
                    saveSession(scanner, localLibraryData, "main.ser");
                    break;
                case "5":
                    saveSession(scanner, localLibraryData, "main.ser");
                    return UserOrManagerCommandResult.EXIT;
                case "6":
                    return UserOrManagerCommandResult.EXIT;   
                    
        	default:
                    GoogleTranslateAPILanguageSetter.translateAndPrintln("Selection unavailable");
                    break;
            }
        } else {
            throw new IllegalStateException();
        }
        return UserOrManagerCommandResult.NEXT_COMMAND;
    }
    static enum UserOrManagerCommandResult {
	EXIT,
	NEXT_COMMAND,
	LOG_OUT;
    }

    void restart() {
	restart = true;
	interrupt();
    }

    /**
     * save data function
     * NOTE: this doesn't need to be encrypted since everyone can see the books, just not the passwords
     * @param name
     * @return
     */
    void saveSession(Scanner scanner, LocalLibraryData localLibraryData, String name) {
	try {
	    if (!Files.exists(Paths.get("cosc310_T28_library_system_saved_files/"))) {
		Files.createDirectories(Paths.get("cosc310_T28_library_system_saved_files/")); // does not overwrite anyways
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	try (
		FileOutputStream fileOut = new FileOutputStream("cosc310_T28_library_system_saved_files/" + name);
//		GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
		ObjectOutputStream out = new ObjectOutputStream(fileOut)
		) {
	    out.writeObject(localLibraryData);
	} catch (IOException i) {
	    i.printStackTrace();
	}
    }

    /**
     * load data function
     * NOTE: this doesn't need to be encrypted since everyone can see the books, just not the passwords
     * @param name
     * @return
     */
    LocalLibraryData loadSession(String name) {
	if (Files.exists(Paths.get("cosc310_T28_library_system_saved_files/" + name))) {
	    try (
		    FileInputStream fileIn = new FileInputStream("cosc310_T28_library_system_saved_files/" + name);
//		    GZIPInputStream zipIn = new GZIPInputStream(fileIn);
		    ObjectInputStream in = new ObjectInputStream(fileIn)
		    ) {
		LocalLibraryData localLibraryData = (LocalLibraryData) in.readObject();
		return localLibraryData;
	    } catch (IOException | ClassNotFoundException c) {
		c.printStackTrace();
		return null;
	    }	
	}
	return null;
    } 

}

       