import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class RecipeMangerGUI extends JFrame {

    GridBagConstraints gbc;
    CustomActionListener cal;
    String connection = "jdbc:mysql://localhost:3306/recipemanager?";
    String user = "guest"; //have to change user and pass to your own user and pass
    String pass = "guest";
    JLabel selectFormLabel;
    JButton viewRecipesButton, addRecipeButton, editRecipeButton, deleteRecipeButton, searchRecipeButton;

    public RecipeMangerGUI() {
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        cal = new CustomActionListener();
        DisplayFormSelectScreen();
        setTitle("Recipe Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // the preferred size of the JFrame
        setPreferredSize(new Dimension(760, 760));
        pack();
        setVisible(true);
    }

    private void ClearScreen() {
        getContentPane().removeAll();
        getContentPane().repaint();
        getContentPane().revalidate();
    }

    // once logged in this is the screen that will display
    private void DisplayFormSelectScreen() {
        ClearScreen();
        selectFormLabel = new JLabel("Welcome to the Recipe Manager! Select an option:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(selectFormLabel, gbc);

        searchRecipeButton = new JButton("Search Recipe");
        searchRecipeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Search Recipe - Coming Soon!"));
        gbc.gridy++;
        add(searchRecipeButton, gbc);

        viewRecipesButton = new JButton("View Recipes");
        viewRecipesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Recipes - Coming Soon!"));
        gbc.gridy++;
        add(viewRecipesButton, gbc);

        addRecipeButton = new JButton("Add Recipe");
        addRecipeButton.addActionListener(e -> DisplayAddRecipeForm());
        gbc.gridy++;
        add(addRecipeButton, gbc);

        //edit recipe button
        editRecipeButton = new JButton("Edit Recipe");
        editRecipeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Recipe - Coming Soon!"));
        gbc.gridy++;
        add(editRecipeButton, gbc);

        deleteRecipeButton = new JButton("Delete Recipe");
        deleteRecipeButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Delete Recipe - Coming Soon!"));
        gbc.gridy++;
        add(deleteRecipeButton, gbc);

        pack();
        repaint();
    }

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

        pack();
        repaint();
    }

    private void addRecipe(String recipeName, String recipeTime, int serveSize, String ingredName, String tagCall, String steps) {
        String conString = connection;

        try (Connection conn = DriverManager.getConnection(conString, user, pass)) {
            String storedProcedure = "{CALL add_recipe(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = conn.prepareCall(storedProcedure)) {
                stmt.setString(1, recipeName);
                stmt.setString(2, recipeTime);
                stmt.setInt(3, serveSize);
                stmt.setString(4, ingredName);
                stmt.setString(5, tagCall);
                stmt.setString(6, steps);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Recipe added successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // this is gonna take you to the given screens or whatever you wanna call it
    class CustomActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Empty ActionListener
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