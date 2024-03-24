package cen4010;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeManagerTests {
	
	private Connection conn;
    private RecipeManager recipeManager;

    @BeforeEach
    public void setUp() throws SQLException {
    	String connection = "jdbc:mysql://localhost:3306/recipemanager";
        String user = "root";
        String pass = "password";
        conn = DriverManager.getConnection(connection, user, pass);
    	this.recipeManager =  new RecipeManager();
    }

    @After
    public void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * test AddRecipe method by adding a test recipe and checking if the database contains the recipe
     */
    @Test
    public void testAddRecipe() {
        //Test recipe info
        String recipeName = "Test Recipe";
        String recipeTime = "00:30:00";
        int servingSize = 4;
        String ingredients = "Ingredient 1, Ingredient 2";
        String tags = "Tag 1, Tag 2";
        String steps = "Step 1, Step 2";

        //Add test recipe to the database
        recipeManager.addRecipe(recipeName, recipeTime, servingSize, ingredients, tags, steps);

        //Check if the test recipe is in the database
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM recipe WHERE recipe_name = '" + recipeName + "'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                if (resultSet.next()) {
                    assertEquals(recipeTime, resultSet.getString("recipe_time"));
                    assertEquals(servingSize, resultSet.getInt("recipe_serving_size"));
                } else {
                    fail("Recipe not found in the database.");
                }
            }
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
	
    /**
     * update a recipe that already exists in the database test
     */
    @Test
    public void testEditRecipe() {
        //Create a test recipe
        String recipeName = "Test Recipe";
        String updatedRecipeName = "Updated Recipe";
        String recipeTime = "00:30:00";
        String servingSize = "4";
        String ingredients = "Ingredient 1, Ingredient 2";
        String tags = "Tag 1, Tag 2";
        String steps = "Step 1, Step 2";

        recipeManager.saveEditedRecipe(recipeName, updatedRecipeName, recipeTime, servingSize, ingredients, tags, steps);

        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM recipe WHERE recipe_name = '" + updatedRecipeName + "'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                if (resultSet.next()) {
                    assertEquals(recipeTime, resultSet.getString("recipe_time"));
                    assertEquals(servingSize, resultSet.getString("recipe_serving_size"));
                } else {
                    fail("Edited recipe not found in the database.");
                }
            }
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
    
    /*@Test
    public void testDisplayRecipeInfo() {
        // Test data
        String recipeName = "Test Recipe2";
        String recipeTime = "00:30:00";
        int servingSize = 4;
        String ingredientName = "Ingredient 1";
        String tagName = "Tag 5";
        String stepsName = "Step 1";

        
        recipeManager.addRecipe(recipeName, recipeTime, servingSize, ingredientName, tagName, stepsName);
        // Call the DisplayRecipeInfo method
        recipeManager.DisplayRecipeInfo(recipeName);

        // Verify that the GUI components are updated correctly
        JLabel recipeNameLabel = (JLabel) recipeManager.getComponent(0); // Assuming recipe name is the first component added
        assertEquals("Recipe Name: " + recipeName, recipeNameLabel.getText());

        JLabel recipeTimeLabel = (JLabel) recipeManager.getComponent(1); // Assuming recipe time is the second component added
        assertEquals("Recipe Time: " + recipeTime, recipeTimeLabel.getText());

        JLabel servingSizeLabel = (JLabel) recipeManager.getComponent(2); // Assuming serving size is the third component added
        assertEquals("Serving Size: " + servingSize, servingSizeLabel.getText());

        JLabel ingredientsLabel = (JLabel) recipeManager.getComponent(3); // Assuming ingredients label is the fourth component added
        assertEquals("Ingredients:", ingredientsLabel.getText());

        JLabel ingredientLabel = (JLabel) recipeManager.getComponent(4); // Assuming ingredient is the fifth component added
        assertEquals(ingredientName, ingredientLabel.getText());

        JLabel tagsLabel = (JLabel) recipeManager.getComponent(5); // Assuming tags label is the sixth component added
        assertEquals("Tags:", tagsLabel.getText());

        JLabel tagLabel = (JLabel) recipeManager.getComponent(6); // Assuming tag is the seventh component added
        assertEquals(tagName, tagLabel.getText());

        JLabel stepsLabel = (JLabel) recipeManager.getComponent(7); // Assuming steps label is the eighth component added
        assertEquals("Recipe Steps:", stepsLabel.getText());

        JLabel stepLabel = (JLabel) recipeManager.getComponent(8); // Assuming step is the ninth component added
        assertEquals(stepsName, stepLabel.getText());
    }*/
    /**
     * works the same as the add and edit tests
     */
    @Test
    public void testDeleteRecipe() {
        String recipeName = "Test Recipe";
        String recipeTime = "00:30:00";
        int servingSize = 4;
        String ingredients = "Ingredient 1, Ingredient 2";
        String tags = "Tag 3, Tag 4";
        String steps = "Step 1, Step 2";

        recipeManager.addRecipe(recipeName, recipeTime, servingSize, ingredients, tags, steps);

        recipeManager.deleteRecipe(recipeName);

        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM recipe WHERE recipe_name = '" + recipeName + "'";
            try (ResultSet resultSet = stmt.executeQuery(query)) {
                assertFalse(resultSet.next(), "Recipe was not deleted from the database.");
            }
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }


}
