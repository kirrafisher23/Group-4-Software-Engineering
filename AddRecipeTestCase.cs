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
    class AddRecipeTestCase
    {
        private string connString = "User Id = G25;Password=Password;Data Source=jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
        [Test]
        public void TestAddRecipe()
        {

            AddRecipeSQL testAdd = new AddRecipeSQL();
            string recipeName = "Test Recipe";
            string recipeTime = "30 min";
            int servingSize = 4;
            string ingredients = "Ingredient 1, Ingredient 2";
            string tags = "tag 1, tag 2";
            string steps = "Steps 1, Steps 2";

            testAdd.addRecipe(recipeName, recipeTime, servingSize, ingredients, tags, steps);
            
            using (OracleConnection conn = new OracleConnection(connString))
            {
                conn.Open();
                string query = "SELECT * FROM recipe WHERE recipe_name = @recipeName";
                using (OracleCommand cmd = new OracleCommand(query, conn))
                {
                    cmd.Parameters.AddWithValue("@recipeName", recipeName);
                    using(OracleDataReader reader = cmd.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            Assert.AreEqual(recipeTime, reader["recipe_time"].ToString());
                            Assert.AreEqual(servingSize, Convert.ToInt32(reader["recipe_serving_size"]));
                        }
                        else
                        {
                            Assert.Fail("Recipe not found in the database.");
                        }
                    }
                }

            }

        }
    }
}
