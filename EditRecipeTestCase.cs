using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using NUnit.Framework;
using Oracle.DataAccess.Client;

namespace Assets.Scripts
{
    [TestFixture]
    class EditRecipeTestCase
    {
        private string connString = "User Id=G25;Password=Password;Data Source=jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";

        [Test]
        public void TestEditRecipe()
        {
            EditRecipe editRecipe = new EditRecipe();
            string recipeName = "Test Recipe";
            string updatedRecipeName = "Updated Recipe";
            string recipeTime = "30 min";
            int servingSize = 4;
            string ingredients = "Ingredient 1, Ingredient 2";
            string tags = "tag 1, tag 2";
            string steps = "Steps 1, Steps 2";

            editRecipe.editRecipe(recipeName, updatedRecipeName, recipeTime, servingSize, ingredients, tags, steps);

            using (OracleConnection conn = new OracleConnection(connString))
            {
                conn.Open();
                string query = "SELECT * FROM recipe WHERE recipe_name = @updatedRecipeName";
                using (OracleCommand cmd = new OracleCommand(query, conn))
                {
                    cmd.Parameters.AddWithValue("@updatedRecipeName", updatedRecipeName);
                    using (OracleDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            Assert.AreEqual(recipeTime, reader["recipe_time"].ToString(), "Recipe time does not match.");
                            Assert.AreEqual(servingSize.ToString(), reader["recipe_serving_size"].ToString(), "Serving size does not match.");

                        }
                        else
                        {
                            Assert.Fail("Edited recipe not found in the database.");
                        }
                    }
                }
            }
        }
    }
}
