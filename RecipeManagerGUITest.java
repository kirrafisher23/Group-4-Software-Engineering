import static org.junit.jupiter.api.Assertions.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

import org.junit.jupiter.api.Test;

class RecipeManagerGUITest extends RecipeManagerGUI{

	@Test
	void testLoginWithCredentialsNoError()
	{
		RecipeManagerGUI actual = new RecipeManagerGUI();
		actual.usernameField = new JTextField();
		actual.passwordField = new JTextField();
		
		actual.usernameField.setText("testUser");
		actual.passwordField.setText("testPassword");    
		actual.errorMessageArea = new JTextArea();

        assertEquals("", actual.errorMessageArea.getText());
		
	}
	
	void testLoginWithCredentialsError()
	{
		RecipeManagerGUI actual = new RecipeManagerGUI();
		actual.usernameField = new JTextField();
		actual.passwordField = new JTextField();
		
		actual.usernameField.setText("");
		actual.passwordField.setText("");    
		actual.errorMessageArea = new JTextArea();
		
		assertEquals("Username and password cannot be empty.", actual.errorMessageArea.getText());
	}
	
	@Test
	void testRegisterNewUser()
	{
		RecipeManagerGUI actual = new RecipeManagerGUI();
		
		actual.usernameField = new JTextField();
		actual.passwordField = new JTextField();
		actual.errorMessageArea = new JTextArea();
		
		actual.usernameField.setText("testUser");
		actual.passwordField.setText("testPassword");    

		 actual.RegisterNewUser();
		
        assertEquals("User registration successful!", actual.errorMessageArea.getText());
	}

}
