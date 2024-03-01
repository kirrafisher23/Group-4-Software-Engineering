/*this was as loosely based of a GUI project we did 
for databases hence why the windows maybe too small these can be readjusted easily.
the running two stored procedures handle registering and logging in
my test login is username: ya and passward: mamma (very original)*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class RecipeMangerGUI extends JFrame {

	GridBagConstraints gbc;
	CustomActionListener cal;
	String connection = "jdbc:mysql://localhost:3306/recipemanager?";
	String user = "guest";
	String pass = "guest";
	JLabel welcomeMessage, registerMessage, selectFormLabel, loginLabel;
	JTextField usernameField, passwordField;
	JTextArea errorMessageArea;
	JButton registerButton, loginButton, submitButton, logoutButton, viewRecipesButton, addRecipeButton;

	public RecipeMangerGUI() {
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		cal = new CustomActionListener();
		DisplaySplashScreen();
		setTitle("Recipe Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private void ClearScreen() {
		getContentPane().removeAll();
		getContentPane().repaint();
		getContentPane().revalidate();
	}

	private void DisplaySplashScreen() {
		ClearScreen();
		welcomeMessage = new JLabel("Welcome to the Recipe Manager!");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(welcomeMessage, gbc);

		registerButton = new JButton("Register");
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		registerButton.addActionListener(cal);
		registerButton.setActionCommand("register");
		add(registerButton, gbc);

		loginButton = new JButton("Login with Existing Credentials");
		gbc.gridx = 1;
		loginButton.addActionListener(cal);
		loginButton.setActionCommand("login");
		add(loginButton, gbc);

		pack();
		repaint();
	}

	private void DisplayRegisterScreen() {
		ClearScreen();
		registerMessage = new JLabel("Please enter a new username and password below.");
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(registerMessage, gbc);

		JLabel usernameLabel = new JLabel("Username:");
		gbc.gridy++;
		add(usernameLabel, gbc);

		usernameField = new JTextField(20);
		gbc.gridy++;
		add(usernameField, gbc);

		JLabel passwordLabel = new JLabel("Password:");
		gbc.gridy++;
		add(passwordLabel, gbc);

		passwordField = new JTextField(20);
		gbc.gridy++;
		add(passwordField, gbc);

		submitButton = new JButton("Submit");
		submitButton.setActionCommand("newUserRegister");
		submitButton.addActionListener(cal);
		gbc.gridy++;
		add(submitButton, gbc);

		errorMessageArea = new JTextArea(10, 50);
		gbc.gridy++;
		add(errorMessageArea, gbc);

		pack();
		repaint();
	}

	// displays login form
	private void DisplayLoginForm() {
		ClearScreen();
		loginLabel = new JLabel("Please enter your credentials below.");
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(loginLabel, gbc);

		JLabel usernameLabel = new JLabel("Username:");
		gbc.gridy++;
		add(usernameLabel, gbc);

		usernameField = new JTextField(20);
		gbc.gridy++;
		add(usernameField, gbc);

		JLabel passwordLabel = new JLabel("Password:");
		gbc.gridy++;
		add(passwordLabel, gbc);

		passwordField = new JTextField(20);
		gbc.gridy++;
		add(passwordField, gbc);

		submitButton = new JButton("Login");
		submitButton.setActionCommand("loginWithCreds");
		submitButton.addActionListener(cal);
		gbc.gridy++;
		add(submitButton, gbc);

		errorMessageArea = new JTextArea(10, 50);
		gbc.gridy++;
		add(errorMessageArea, gbc);

		pack();
		repaint();
	}

	// once logged in this is the screen that will display
	private void DisplayFormSelectScreen() {
		ClearScreen();
		selectFormLabel = new JLabel("Welcome, select an option:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(selectFormLabel, gbc);

		viewRecipesButton = new JButton("View Recipes");
		viewRecipesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Recipes - Coming Soon!"));
		gbc.gridy++;
		add(viewRecipesButton, gbc);

		addRecipeButton = new JButton("Add Recipe");
		addRecipeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Recipe - Coming Soon!"));
		gbc.gridy++;
		add(addRecipeButton, gbc);

		logoutButton = new JButton("Log Out");
		logoutButton.addActionListener(e -> DisplaySplashScreen());
		gbc.gridy++;
		add(logoutButton, gbc);

		logoutButton.addActionListener(e -> DisplaySplashScreen());
		gbc.gridy++;
		add(logoutButton, gbc);

		pack();
		repaint();
	}

	// this is gonna take you to the given screens or whatever you wanna call it
	class CustomActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "register":
				DisplayRegisterScreen();
				break;
			case "login":
				DisplayLoginForm();
				break;
			case "newUserRegister":
				RegisterNewUser();
				break;
			case "loginWithCreds":
				LoginWithCreds();
				break;
			}
		}
	}

	// calling the addnewUser stored procedure
	private void RegisterNewUser() {
		String username = usernameField.getText();
		String password = passwordField.getText();

		String conString = connection;

		if (username.isEmpty() || password.isEmpty()) {
			errorMessageArea.setText("Username and password cannot be empty.");
			return;
		}

		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // Add this line
			try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
				// Prepare the stored procedure call
				String storedProcedure = "{CALL addnewUser(?, ?)}";
				try (CallableStatement stmt = conn.prepareCall(storedProcedure)) {
					stmt.setString(1, username);
					stmt.setString(2, password);
					stmt.executeUpdate();
					errorMessageArea.setText("User registration successful!");
					usernameField.setText("");
					passwordField.setText("");
				}
			}
		} catch (ClassNotFoundException e) {
			errorMessageArea.setText("MySQL JDBC driver not found: " + e.getMessage());
		} catch (SQLException e) {
			errorMessageArea.setText("Error: " + e.getMessage());
		}
	}

	// calling the loginCreds stored procedure
	int loggedInUserID;
	int loggedInUserRole;
	String loggedInUserName;

	private void LoginWithCreds() {
		String username = usernameField.getText();
		String password = passwordField.getText();

		String conString = connection;

		if (username.isEmpty() || password.isEmpty()) {
			errorMessageArea.setText("Username and password cannot be empty.");
			return;
		}

		try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
			// Prepare the stored procedure call
			String storedProcedure = "{CALL loginCreds(?, ?)}";
			try (CallableStatement stmt = conn.prepareCall(storedProcedure)) {

				stmt.setString(1, username);
				stmt.setString(2, password);

				// Execute the query
				try (ResultSet resultSet = stmt.executeQuery()) {
					if (resultSet.next()) {
						// User found, capture ID, username, and role
						// loggedInUserID = resultSet.getInt("id");
						loggedInUserName = resultSet.getString("username");
						// loggedInUserRole = resultSet.getInt("userRole");

						// Display the appropriate screen based on user role
						DisplayFormSelectScreen();

						// Clear the username and password fields
						usernameField.setText("");
						passwordField.setText("");
					} else {
						errorMessageArea.setText("Invalid username/password.");
					}
				}
			}
		} catch (SQLException e) {
			errorMessageArea.setText("Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RecipeMangerGUI();
			}
		});
	}
}
