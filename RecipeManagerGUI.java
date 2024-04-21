package cen4010;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class RecipeManagerGUI extends JFrame{
	GridBagConstraints gbc;
	String user = "G25";
	String pass = "Password";
	String connection = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	
	
	JLabel welcomeMessage, registerMessage, selectFormLabel, loginLabel;
	JTextField usernameField, passwordField;
	JTextArea errorMessageArea;
	JButton submitButton, backButton, viewAllRecipesButton, addRecipeButton, editRecipeButton, deleteRecipeButton, searchRecipeButton, exitButton, saveButton;
	
	RecipeDatabase recipeData;
	//int recipeID = 1;
	public RecipeManagerGUI() throws SQLException {
		//setSize(800, 600);
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		
		
		
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        Connection conn = DriverManager.getConnection(connection, user, pass);
            // Connection successful
        
        
		
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
	
	public void setRecipeDatabase(RecipeDatabase recipeData) {
		this.recipeData = recipeData;
	}

	public void ClearScreen() {
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
		//gbc.insets = new Insets(10, 10, 10, 10);
		add(welcomeMessage, gbc);
		DisplayFormSelectScreen();

		pack();
		//setSize(800, 600);
		repaint();
	}

	// display menu options
	public void DisplayFormSelectScreen() {
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
	
	public void displaySearchResults(ResultSet rs) throws SQLException {
	    ClearScreen();

	    if (rs.next()) {
	        do {
	            // Process each row of the result set and create a button for each recipe
	            String recipeName = rs.getString("recipe_name");
	            JButton recipeButton = new JButton(recipeName);
	            recipeButton.addActionListener(e -> {
					try {
						DisplayRecipeInfo(recipeName);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}); // ActionListener to view full recipe
	            gbc.gridy++;
	            add(recipeButton, gbc);
	        } while (rs.next());
	    } else {
	        JOptionPane.showMessageDialog(this, "No recipes found.");
	    }
	    JButton backButton = new JButton("Back to Main Menu");
	    backButton.addActionListener(e -> DisplayFormSelectScreen());
	    gbc.gridy++;
	    add(backButton, gbc);

	    pack();
	    repaint();
	}

	
	public void DisplaySearchRecipeForm() {
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
            try {
				recipeData.searchRecipe(searchBy, searchTerm);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
	
	private void DisplayAddRecipeForm() {
        ClearScreen();
        JLabel addRecipeLabel = new JLabel("Add a New Recipe");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); 
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

        JTextArea stepsTextArea = new JTextArea(3, 40);
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
            
            

            recipeData.addRecipe(recipeName, recipeTime, serveSize, ingredName, tagCall, steps);
            try {
				DisplayRecipeInfo(recipeName);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, "Error adding recipe: " + e1.getMessage());
			}
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
	                        recipeButton.addActionListener(e -> {
								try {
									DisplayRecipeInfo(recipeName);
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							});
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
	
	public void DisplayRecipeInfo(String recipeName) throws SQLException {
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
        
        deleteRecipeButton = new JButton("Delete Recipe");
        deleteRecipeButton.addActionListener(e -> {
            int confirmDelete = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this recipe?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmDelete == JOptionPane.YES_OPTION) {
				recipeData.deleteRecipe(recipeName);
				DisplayAllRecipes();
            }
        });
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(deleteRecipeButton, gbc);

        
        
        editRecipeButton = new JButton("Edit Recipe");
		editRecipeButton.addActionListener(e -> DisplayEditRecipeDetails(recipeName));
		//gbc.gridy++;
		gbc.gridx++;
		add(editRecipeButton, gbc);
		
		backButton = new JButton("Back to Recipe List");
        backButton.addActionListener(e -> DisplayAllRecipes());
        gbc.gridx++;
        add(backButton, gbc);

        pack();
        repaint();
    }
	
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
    			        	recipeData.saveEditedRecipe(recipeName, recipeNameField.getText(), recipeTimeField.getText(), servingSizeField.getText(), ingredientNameTextArea.getText(), tagField.getText(), stepsTextArea.getText());
    			        	try {
								DisplayRecipeInfo(recipeNameField.getText());
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
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
        	recipeData.deleteRecipe(selectedRecipe);
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
    
    
    class CustomActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Empty ActionListener
        }
    }

	
	

	/*Main Class to run everything*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					RecipeDatabase database = new RecipeDatabase();
					RecipeManagerGUI gui = new RecipeManagerGUI();
					gui.setRecipeDatabase(database);
					database.setRecipeManagerGUI(gui);
					//new RecipeManagerGUI();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
