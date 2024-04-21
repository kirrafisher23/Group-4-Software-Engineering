
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.GridBagConstraints;
import java.sql.*;
import oracle.jdbc.*;

public class RecipeDatabase extends JFrame{
	String user = "G25";
	String pass = "Password";
	String connection = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	RecipeManagerGUI recipeManagerGUI;
    
    private GridBagConstraints gbc;

    public RecipeDatabase() throws SQLException {
    	DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection conn = DriverManager.getConnection(connection, user, pass);
    }
    
    public void setRecipeManagerGUI(RecipeManagerGUI recipeManagerGUI) {
    	this.recipeManagerGUI = recipeManagerGUI;
    }
    
    public void searchRecipe(String searchBy, String searchTerm) throws SQLException {
        
    	if(searchBy.isEmpty() || searchTerm.isEmpty()) {
    		JOptionPane.showMessageDialog(this, "Please enter tag or recipe name.");
    		return;
    	}
    	try {
            // Perform database search
            ResultSet rs = performDatabaseSearch(searchBy, searchTerm);

            // Notify GUI about search results
            recipeManagerGUI.displaySearchResults(rs);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private ResultSet performDatabaseSearch(String searchBy, String searchTerm) throws SQLException {
        String conString = connection;
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement stmt = null;

        try {
            conn = DriverManager.getConnection(conString, user, pass);
            String storedProcedure = "{CALL search_recipe(?, ?, ?)}";
            stmt = conn.prepareCall(storedProcedure);
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchBy);
            stmt.registerOutParameter(3, OracleTypes.CURSOR);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(3);
        } finally {
            if (stmt == null) {
                stmt.close(); // Close the statement
            }
            if (conn == null) {
                conn.close(); // Close the connection
            }
        }

        return rs;
    }

    
    public void addRecipe(String recipeName, String recipeTime, int serveSize, String ingredName, String tagCall, String steps) {
        String conString = connection;

        try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
            String storedProcedure = "{CALL add_recipe(?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = conn.prepareCall(storedProcedure)) {
                stmt.setString(1, recipeName);
                stmt.setString(2, recipeTime);
                stmt.setInt(3, serveSize);
                stmt.setString(4, ingredName);
                stmt.setString(5, tagCall);
                stmt.setString(6, steps);
                stmt.registerOutParameter(7, Types.INTEGER);
                stmt.executeUpdate();
                int recipeId = stmt.getInt(7);
                JOptionPane.showMessageDialog(this, "Recipe added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
   
    
    public void saveEditedRecipe(String oldRecipeName, String newRecipeName, String newRecipeTime, String newServingSize, String newIngredients,String newTags, String newSteps) {
   	 String conString = connection;
   	    try (Connection conn = DriverManager.getConnection(conString, user, pass)) {

   	        String getRecipeIdQuery = "SELECT recipe_id FROM recipe WHERE recipe_name = ?";
   	        int recipeId;
   	        try (PreparedStatement stmt = conn.prepareStatement(getRecipeIdQuery)) {
   	            stmt.setString(1, oldRecipeName);
   	            try (ResultSet rs = stmt.executeQuery()) {
   	                if (rs.next()) {
   	                    recipeId = rs.getInt("recipe_id");
   	                } else {
   	                    JOptionPane.showMessageDialog(this, "Recipe not found.");
   	                    return;
   	                }
   	            }
   	        }

   	        // Update ingredients table
   	        String updateIngredientsQuery = "UPDATE ingredients SET ingredient_name = ? WHERE recipeID = ?";
   	        try (PreparedStatement stmt = conn.prepareStatement(updateIngredientsQuery)) {
   	            stmt.setString(1, newIngredients);
   	            stmt.setInt(2, recipeId);
   	            stmt.executeUpdate();
   	        }

   	        // Update tags table
   	        String updateTagsQuery = "UPDATE tags SET tag = ? WHERE recipeID = ?";
   	        try (PreparedStatement stmt = conn.prepareStatement(updateTagsQuery)) {
   	            stmt.setString(1, newTags);
   	            stmt.setInt(2, recipeId);
   	            stmt.executeUpdate();
   	        }

   	        // Update recipe_steps table
   	        String updateStepsQuery = "UPDATE recipe_steps SET steps = ? WHERE recipeID = ?";
   	        try (PreparedStatement stmt = conn.prepareStatement(updateStepsQuery)) {
   	            stmt.setString(1, newSteps);
   	            stmt.setInt(2, recipeId);
   	            stmt.executeUpdate();
   	        }

   	        String updateRecipeQuery = "UPDATE recipe SET recipe_name = ?, recipe_time = ?, recipe_serving_size = ? WHERE recipe_name = ?";
   	        try (PreparedStatement stmt = conn.prepareStatement(updateRecipeQuery)) {
   	            stmt.setString(1, newRecipeName);
   	            stmt.setString(2, newRecipeTime);
   	            stmt.setInt(3, Integer.parseInt(newServingSize));
   	            stmt.setString(4, oldRecipeName);
   	            stmt.executeUpdate();
   	        }

   	        JOptionPane.showMessageDialog(this, "Recipe updated successfully!");
   	    } catch (SQLException e) {
   	        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
   	    }
   }
    
    public void deleteRecipe(String recipeName) {
    	String conString = connection;
        try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
            conn.setAutoCommit(false); 

            // Get recipe ID
            int recipeId;
            String getRecipeIdQuery = "SELECT recipe_id FROM recipe WHERE recipe_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getRecipeIdQuery)) {
                stmt.setString(1, recipeName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        recipeId = rs.getInt("recipe_id");
                    } else {
                        JOptionPane.showMessageDialog(this, "Recipe not found.");
                        return;
                    }
                }
            }

            // Delete from recipe_tags
            String deleteTagsQuery = "DELETE FROM recipe_tags WHERE recipeID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTagsQuery)) {
                stmt.setInt(1, recipeId);
                stmt.executeUpdate();
            }
            
            String delTagsQuery = "DELETE FROM tags WHERE recipeID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(delTagsQuery)) {
                stmt.setInt(1, recipeId);
                stmt.executeUpdate();
            }

            // Delete from recipe_steps
            String deleteStepsQuery = "DELETE FROM recipe_steps WHERE recipeID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteStepsQuery)) {
                stmt.setInt(1, recipeId);
                stmt.executeUpdate();
            }

            // Delete from recipe_ingredients
            String deleteIngredientsQuery = "DELETE FROM recipe_ingredients WHERE recipeID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteIngredientsQuery)) {
                stmt.setInt(1, recipeId);
                stmt.executeUpdate();
            }
            String delIngredientsQuery = "DELETE FROM ingredients WHERE recipeID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(delIngredientsQuery)) {
                stmt.setInt(1, recipeId);
                stmt.executeUpdate();
            }

            // Delete from recipe
            String deleteRecipeQuery = "DELETE FROM recipe WHERE recipe_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteRecipeQuery)) {
                stmt.setInt(1, recipeId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit(); 
                    JOptionPane.showMessageDialog(this, "Recipe deleted successfully!");
                } else {
                    conn.rollback(); 
                    JOptionPane.showMessageDialog(this, "Recipe not found.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
}
