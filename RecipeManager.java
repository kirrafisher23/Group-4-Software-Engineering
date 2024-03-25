

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class RecipeManager extends JFrame{

	
	GridBagConstraints gbc;
	String connection = "jdbc:mysql://localhost:3306/recipemanager?";
	String user = "guest"; //have to change user and pass to your own user and pass 
	String pass = "guest";
	JLabel welcomeMessage, registerMessage, selectFormLabel, loginLabel;
	JTextField usernameField, passwordField;
	JTextArea errorMessageArea;
	JButton submitButton, backButton, viewAllRecipesButton, addRecipeButton, editRecipeButton, deleteRecipeButton, searchRecipeButton, exitButton, saveButton;
	int recipeID = 1;
	public RecipeManager() {
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(20, 20, 20, 20);
		DisplaySplashScreen();
		setTitle("Recipe Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
		errorMessageArea = new JTextArea(5, 20);
		errorMessageArea.setEditable(false);
		JScrollPane errorScrollPane = new JScrollPane(errorMessageArea);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(errorScrollPane, gbc);
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
		DisplayFormSelectScreen();

		pack();
		repaint();
	}

	// display menue options
	private void DisplayFormSelectScreen() {
		ClearScreen();
		selectFormLabel = new JLabel("Welcome, select an option:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(selectFormLabel, gbc);
		
        searchRecipeButton = new JButton("Search Recipe");
        searchRecipeButton.addActionListener(e -> DisplaySearchRecipeForm());
        gbc.gridy++;
        add(searchRecipeButton, gbc);

		viewAllRecipesButton = new JButton("View All Recipes");
		viewAllRecipesButton.addActionListener(e -> DisplayAllRecipes());
		gbc.gridy++;
		add(viewAllRecipesButton, gbc);

		addRecipeButton = new JButton("Add Recipe");
		addRecipeButton.addActionListener(e -> DisplayAddRecipeForm());
		gbc.gridy++;
		add(addRecipeButton, gbc);
		
		//edit recipe button
		editRecipeButton = new JButton("Edit Recipe");
		editRecipeButton.addActionListener(e -> DisplayEditRecipeForm());
		gbc.gridy++;
		add(editRecipeButton, gbc);
		
		deleteRecipeButton = new JButton("Delete Recipe");
		deleteRecipeButton.addActionListener(e -> DisplayDeleteRecipeForm());
		gbc.gridy++;
		add(deleteRecipeButton, gbc);

		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(e -> System.exit(0));
		gbc.gridy++;
		add(exitButton, gbc);

		pack();
		repaint();
	}
	
	/**
	 * method to display the add recipe form
	 */
	private void DisplayAddRecipeForm() {
        ClearScreen();
        JLabel addRecipeLabel = new JLabel("Add a New Recipe");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10); // Reduced top and bottom insets
        add(addRecipeLabel, gbc);

        JLabel recipeNameLabel = new JLabel("Recipe Name:");
        gbc.gridy++;
        add(recipeNameLabel, gbc);

        JTextField recipeNameField = new JTextField(40);
        gbc.gridy++;
        add(recipeNameField, gbc);

        JLabel recipeTimeLabel = new JLabel("Recipe Time:");
        gbc.gridy++;
        add(recipeTimeLabel, gbc);

        JTextField recipeTimeField = new JTextField(40);
        gbc.gridy++;
        add(recipeTimeField, gbc);

        JLabel servingSizeLabel = new JLabel("Serving Size:");
        gbc.gridy++;
        add(servingSizeLabel, gbc);

        JTextField servingSizeField = new JTextField(40);
        gbc.gridy++;
        add(servingSizeField, gbc);

        JLabel ingredientNameLabel = new JLabel("Ingredients:");
        gbc.gridy++;
        add(ingredientNameLabel, gbc);

        JTextArea ingredientNameTextArea = new JTextArea(3, 40);
        JScrollPane ingredientNameScrollPane = new JScrollPane(ingredientNameTextArea);
        gbc.gridy++;
        add(ingredientNameScrollPane, gbc);

        JLabel tagLabel = new JLabel("Tag:");
        gbc.gridy++;
        add(tagLabel, gbc);

        JTextField tagField = new JTextField(40);
        gbc.gridy++;
        add(tagField, gbc);

        JLabel stepsLabel = new JLabel("Recipe Steps:");
        gbc.gridy++;
        add(stepsLabel, gbc);

        JTextArea stepsTextArea = new JTextArea(3, 40); // Reduced number of rows
        JScrollPane stepsScrollPane = new JScrollPane(stepsTextArea);
        gbc.gridy++;
        add(stepsScrollPane, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String recipeName = recipeNameField.getText();
            String recipeTime = recipeTimeField.getText();
            String servingSizeText = servingSizeField.getText();
            int serveSize = 0;
            if (!servingSizeText.isEmpty()) {
                try {
                    serveSize = Integer.parseInt(servingSizeText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid serving size. Please enter a valid number.");
                    return;
                }
            }
            String ingredName = ingredientNameTextArea.getText();
            String tagCall = tagField.getText();
            String steps = stepsTextArea.getText();

            addRecipe(recipeName, recipeTime, serveSize, ingredName, tagCall, steps);
        });
        gbc.gridy++;
        add(submitButton, gbc);
        
        backButton = new JButton("Back to Main Menu");
    	backButton.addActionListener(e -> DisplayFormSelectScreen());
    	gbc.gridy++;
    	add(backButton, gbc);

        pack();
        repaint();
    }

	/**
	 * adds the recipe to the database when the user selects submit on the add recipe form
	 * @param recipeName
	 * @param recipeTime
	 * @param serveSize
	 * @param ingredName
	 * @param tagCall
	 * @param steps
	 */
    private void addRecipe(String recipeName, String recipeTime, int serveSize, String ingredName, String tagCall, String steps) {
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
    
    /**
     * method to display all recipes
     */
    public void DisplayAllRecipes() {
        ClearScreen();
        String conString = connection;
        try(Connection conn = DriverManager.getConnection(conString, user, pass)){
            String query = "SELECT * FROM recipe";
            try(PreparedStatement stmt = conn.prepareStatement(query)){
                try(ResultSet resultSet = stmt.executeQuery()){
                    while(resultSet.next()) {
                        String recipeName = resultSet.getString("recipe_name");
                        JButton recipeButton = new JButton(recipeName);
                        recipeButton.addActionListener(e -> DisplayRecipeInfo(recipeName));
                        gbc.gridy++;
                        add(recipeButton, gbc);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> DisplayFormSelectScreen());
        gbc.gridy++;
        add(backButton, gbc);

        pack();
        repaint();
    }

    /**
     * method to display the recipe info for a recipe selected in the view all recipes area
     * @param recipeName
     */
    private void DisplayRecipeInfo(String recipeName) {
    	ClearScreen();
        String conString = connection;
        try(Connection conn = DriverManager.getConnection(conString, user, pass)){
            String recipeQuery = "SELECT * FROM recipe WHERE recipe_name = ?";
            try(PreparedStatement recipeStmt = conn.prepareStatement(recipeQuery)){
                recipeStmt.setString(1, recipeName);
                try(ResultSet recipeResultSet = recipeStmt.executeQuery()){
                    if(recipeResultSet.next()) {
                        JLabel recipeNameLabel = new JLabel("Recipe Name: " + recipeResultSet.getString("recipe_name"));
                        gbc.gridy++;
                        add(recipeNameLabel, gbc);

                        JLabel recipeTimeLabel = new JLabel("Recipe Time: " + recipeResultSet.getString("recipe_time"));
                        gbc.gridy++;
                        add(recipeTimeLabel, gbc);

                        JLabel servingSizeLabel = new JLabel("Serving Size: " + recipeResultSet.getInt("recipe_serving_size"));
                        gbc.gridy++;
                        add(servingSizeLabel, gbc);

                        int recipeId = recipeResultSet.getInt("recipe_id");
                        String ingredientsQuery = "SELECT ingredient_name FROM ingredients WHERE recipeID = ?";
                        try(PreparedStatement ingredientsStmt = conn.prepareStatement(ingredientsQuery)){
                            ingredientsStmt.setInt(1, recipeId);
                            try(ResultSet ingredientsResultSet = ingredientsStmt.executeQuery()){
                                JLabel ingredientsLabel = new JLabel("Ingredients:");
                                gbc.gridy++;
                                add(ingredientsLabel, gbc);

                                while(ingredientsResultSet.next()) {
                                    String ingredientName = ingredientsResultSet.getString("ingredient_name");
                                    JLabel ingredientLabel = new JLabel(ingredientName);
                                    gbc.gridy++;
                                    add(ingredientLabel, gbc);
                                }
                            }
                        }

                        String tagsQuery = "SELECT tag FROM tags WHERE recipeID = ?";
                        try(PreparedStatement tagStmt = conn.prepareStatement(tagsQuery)){
                        	tagStmt.setInt(1, recipeId);
                        	try(ResultSet tagResultSet = tagStmt.executeQuery()){
                        		JLabel tagLabel = new JLabel("Tags:");
                        		gbc.gridy++;
                        		add(tagLabel, gbc);
                        		
                        		while(tagResultSet.next()) {
                        			String tagName = tagResultSet.getString("tag");
                        			JLabel tagsLabel = new JLabel(tagName);
                        			gbc.gridy++;
                        			add(tagsLabel, gbc);
                        		}
                        	}
                        }
                        
                        String stepsQuery = "SELECT steps FROM recipe_steps WHERE recipeID = ?";
                        try(PreparedStatement stepsStmt = conn.prepareStatement(stepsQuery)){
                        	stepsStmt.setInt(1, recipeId);
                        	try(ResultSet stepsResultSet = stepsStmt.executeQuery()){
                        		JLabel recipeStepsLabel = new JLabel("Recipe Steps:");
                        		gbc.gridy++;
                        		add(recipeStepsLabel, gbc);
                        		
                        		while(stepsResultSet.next()) {
                        			String stepsName = stepsResultSet.getString("steps");
                        			JLabel stepsLabel = new JLabel(stepsName);
                        			gbc.gridy++;
                        			add(stepsLabel, gbc);
                        		}
                        	}
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        backButton = new JButton("Back to Recipe List");
        backButton.addActionListener(e -> DisplayAllRecipes());
        gbc.gridy++;
        add(backButton, gbc);

        pack();
        repaint();
    }
    
    /**
     * display edit recipe form where user can select a recipe to edit
     */
    public void DisplayEditRecipeForm() {
    	ClearScreen();
    	JLabel editRecipeLabel = new JLabel("Select a recipe to edit:");
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	add(editRecipeLabel, gbc);
    	
    	JComboBox<String> recipeComboBox = new JComboBox<>();
    	String conString = connection;
    	try(Connection conn = DriverManager.getConnection(conString, user, pass)){
    		String query = "SELECT recipe_name FROM recipe";
    		try(PreparedStatement stmt = conn.prepareStatement(query)){
    			try(ResultSet resultSet = stmt.executeQuery()){
    				while(resultSet.next()) {
    					recipeComboBox.addItem(resultSet.getString("recipe_name"));
    					
    				}
    			}
    		}
    	} catch (SQLException e) {
    		JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
    	gbc.gridy++;
    	add(recipeComboBox, gbc);
    	JButton editButton = new JButton("Edit");
    	editButton.addActionListener(e -> {
    		String selectedRecipe = (String) recipeComboBox.getSelectedItem();
    		DisplayEditRecipeDetails(selectedRecipe);
    	});
    	gbc.gridy++;
    	add(editButton, gbc);
    	
    	backButton = new JButton("Back to Main Menu");
    	backButton.addActionListener(e -> DisplayFormSelectScreen());
    	gbc.gridy++;
    	add(backButton, gbc);
    	
    	pack();
    	repaint();
    	
    }
    
    /**
     * method that allows user to edit the recipe they selected
     * @param recipeName
     */
    public void DisplayEditRecipeDetails(String recipeName) {
    	ClearScreen();
    	JLabel editRecipeLabel = new JLabel("Edit Recipe: " + recipeName);
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	add(editRecipeLabel, gbc);
    	String conString = connection;
    	try(Connection conn = DriverManager.getConnection(conString, user, pass)){
    		String query = "SELECT * FROM recipe WHERE recipe_name = ?";
    		try(PreparedStatement stmt = conn.prepareStatement(query)){
    			stmt.setString(1, recipeName);
    			try(ResultSet resultSet = stmt.executeQuery()){
    				if(resultSet.next()) {
    					
    					JLabel recipeNameLabel = new JLabel("Recipe Name:");
    			        gbc.gridy++;
    			        add(recipeNameLabel, gbc);

    			        JTextField recipeNameField = new JTextField(40);
    			        gbc.gridy++;
    			        add(recipeNameField, gbc);

    			        JLabel recipeTimeLabel = new JLabel("Recipe Time:");
    			        gbc.gridy++;
    			        add(recipeTimeLabel, gbc);

    			        JTextField recipeTimeField = new JTextField(40);
    			        gbc.gridy++;
    			        add(recipeTimeField, gbc);

    			        JLabel servingSizeLabel = new JLabel("Serving Size:");
    			        gbc.gridy++;
    			        add(servingSizeLabel, gbc);

    			        JTextField servingSizeField = new JTextField(40);
    			        gbc.gridy++;
    			        add(servingSizeField, gbc);

    			        JLabel ingredientNameLabel = new JLabel("Ingredients:");
    			        gbc.gridy++;
    			        add(ingredientNameLabel, gbc);

    			        JTextArea ingredientNameTextArea = new JTextArea(3, 40);
    			        JScrollPane ingredientNameScrollPane = new JScrollPane(ingredientNameTextArea);
    			        gbc.gridy++;
    			        add(ingredientNameScrollPane, gbc);

    			        JLabel tagLabel = new JLabel("Tag:");
    			        gbc.gridy++;
    			        add(tagLabel, gbc);

    			        JTextField tagField = new JTextField(40);
    			        gbc.gridy++;
    			        add(tagField, gbc);

    			        JLabel stepsLabel = new JLabel("Recipe Steps:");
    			        gbc.gridy++;
    			        add(stepsLabel, gbc);

    			        JTextArea stepsTextArea = new JTextArea(3, 40); 
    			        JScrollPane stepsScrollPane = new JScrollPane(stepsTextArea);
    			        gbc.gridy++;
    			        add(stepsScrollPane, gbc);
    			        
    			        JButton saveButton = new JButton("Save");
    			        saveButton.addActionListener(e -> {
    			        	saveEditedRecipe(recipeName, recipeNameField.getText(), recipeTimeField.getText(), servingSizeField.getText(), ingredientNameTextArea.getText(), tagField.getText(), stepsTextArea.getText());
    			        });
    			        gbc.gridy++;
    			        add(saveButton, gbc);
    				}
    				else {
    					JOptionPane.showMessageDialog(this, "Recipe not found.");
    				}
    			}
    		}
    		
    	} catch (SQLException e) {
    		JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
    	
    	backButton = new JButton("Back to Main Menu");
    	backButton.addActionListener(e -> DisplayFormSelectScreen());
    	gbc.gridy++;
    	add(backButton, gbc);
    	
    	pack();
    	repaint();
    	
    	
    }
    
    /**
     * method to save the changes the user made to the recipe in the database
     * @param oldRecipeName
     * @param newRecipeName
     * @param newRecipeTime
     * @param newServingSize
     * @param newIngredients
     * @param newTags
     * @param newSteps
     */
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
    
    
    /**
     * method for the user to select a recipe to delete
     */
    public void DisplayDeleteRecipeForm() {
    	ClearScreen();
    	JLabel deleteRecipeLabel = new JLabel("Select a recipe to delete:");
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	add(deleteRecipeLabel, gbc);
    	
    	JComboBox<String> recipeComboBox = new JComboBox<>();
    	String conString = connection;

        try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
        	String query = "SELECT recipe_name FROM recipe";
        	try(PreparedStatement stmt = conn.prepareStatement(query)){
        		try(ResultSet resultSet = stmt.executeQuery()){
    				while(resultSet.next()) {
    					recipeComboBox.addItem(resultSet.getString("recipe_name"));
    					
    				}
        		}
        	}
        	 
        } catch (SQLException e) {
        	JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
		}
        gbc.gridy++;
        add(recipeComboBox, gbc);
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
        	String selectedRecipe = (String) recipeComboBox.getSelectedItem();
        	deleteRecipe(selectedRecipe);
        });
        gbc.gridy++;
        add(deleteButton, gbc);
        
        backButton = new JButton("Back to Main Menu");
    	backButton.addActionListener(e -> DisplayFormSelectScreen());
    	gbc.gridy++;
    	add(backButton, gbc);
    	
    	pack();
    	repaint();
    }
    
    /**
     * method to delete a recipe when the user clicks the delete button
     * @param recipeName
     */
    private void deleteRecipe(String recipeName) {
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
    private void DisplaySearchRecipeForm() {
        ClearScreen();
        JLabel searchRecipeLabel = new JLabel("Search Recipe");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(searchRecipeLabel, gbc);

        JLabel searchByLabel = new JLabel("Search By:");
        gbc.gridy++;
        add(searchByLabel, gbc);

        String[] searchOptions = {"Tag", "Recipe Name"};
        JComboBox<String> searchByComboBox = new JComboBox<>(searchOptions);
        gbc.gridy++;
        add(searchByComboBox, gbc);

        JLabel searchTermLabel = new JLabel("Search Term:");
        gbc.gridy++;
        add(searchTermLabel, gbc);

        JTextField searchTermField = new JTextField(20);
        gbc.gridy++;
        add(searchTermField, gbc);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String searchBy = (String) searchByComboBox.getSelectedItem();
            if (searchBy.equalsIgnoreCase("Tag")) {
                searchBy = "tag";
            } else if (searchBy.equalsIgnoreCase("Recipe Name")) {
                searchBy = "name";
            }
            String searchTerm = searchTermField.getText();
            searchRecipe(searchBy, searchTerm);
        });
        gbc.gridy++;
        add(searchButton, gbc);

        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> DisplayFormSelectScreen());
        gbc.gridy++;
        add(backButton, gbc);

        pack();
        repaint();
    }

    private void searchRecipe(String searchBy, String searchTerm) {
        ClearScreen();
        String conString = connection;

        try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
            String storedProcedure = "{CALL search_recipe(?, ?)}";
            try (CallableStatement stmt = conn.prepareCall(storedProcedure)) {
                stmt.setString(1, searchTerm);
                stmt.setString(2, searchBy);

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean recipeFound = false;
                    while (rs.next()) {
                        recipeFound = true;
                        String recipeName = rs.getString("recipe_name");
                        String recipeTime = rs.getString("recipe_time");
                        int servingSize = rs.getInt("recipe_serving_size");

                        JLabel recipeNameLabel = new JLabel("Recipe Name: " + recipeName);
                        gbc.gridy++;
                        add(recipeNameLabel, gbc);

                        JLabel recipeTimeLabel = new JLabel("Recipe Time: " + recipeTime);
                        gbc.gridy++;
                        add(recipeTimeLabel, gbc);

                        JLabel servingSizeLabel = new JLabel("Serving Size: " + servingSize);
                        gbc.gridy++;
                        add(servingSizeLabel, gbc);

                        JSeparator separator = new JSeparator();
                        gbc.gridy++;
                        add(separator, gbc);
                    }

                    if (!recipeFound) {
                        JLabel noRecipeLabel = new JLabel("No recipes found.");
                        gbc.gridy++;
                        add(noRecipeLabel, gbc);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        backButton = new JButton("Back to Search");
        backButton.addActionListener(e -> DisplaySearchRecipeForm());
        gbc.gridy++;
        add(backButton, gbc);

        pack();
        repaint();
    }

    // this is gonna take you to the given screens or whatever you wanna call it
    class CustomActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Empty ActionListener
        }
    }

	
	

	/*Main Class to run everything*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new RecipeManager();
			}
		});
	}
}