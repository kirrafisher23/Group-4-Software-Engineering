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
    class DeleteRecipeTestCase
    {

        private string connString = "User Id = G25;Password=Password;Data Source=jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
        [Test]
        public void TestDeleteRecipe()
        {
            AddRecipeSQL testAdd = new AddRecipeSQL();
            DeleteRecipeSQL delRecipe = new DeleteRecipeSQL();
            string recipeName = "Test Recipe";
            string recipeTime = "30 min";
            int servingSize = 4;
            string ingredients = "Ingredient 1, Ingredient 2";
            string tags = "tag 3, tag 4";
            string steps = "Steps 1, Steps 2";

            testAdd.addRecipe(recipeName, recipeTime, servingSize, ingredients, tags, steps);

            delRecipe.DeleterRecipe(recipeName);

            using (OracleConnection conn = new OracleConnection(connString))
            {
                connString.Open();
                string query = "SELECT * FROM recipe WHERE recipe_name = :recipeName";
                using (OracleCommand cmd = new OracleCommand(query, conn))
                {
                    cmd.Parameters.Add(new OracleParameter("recipeName", recipeName));
                    using (OracleDataReader reader = cmd.ExecuteReader())
                    {
                        Assert.IsFalse(reader.Read(), "Recipe was not deleted from the database.");
                    }

                }
            }

            
        }
    }
}
