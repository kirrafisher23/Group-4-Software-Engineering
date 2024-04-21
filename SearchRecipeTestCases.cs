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
    class SearchRecipeTestCases
    {
        private string connString = "User Id = G25;Password=Password;Data Source=jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
        private OracleConnection conn;

        [SetUp]
        public void Setup()
        {
            conn = new OracleConnection(connectionString);
            conn.Open();
        }

        [TearDown]
        public void Teardown()
        {
            conn.Close();
            conn.Dispose();
        }

        [Test]
        public void TestSearchRecipeTag()
        {
            string searchTerm = "tag A edit";
            SearchRecipeAndAssert(searchTerm, "tag");
        }

        [Test]
        public void TestSearchRecipeName()
        {
            string searchTerm = "recipe A edit";
            SearchRecipeAndAssert(searchTerm, "recipe_name");
        }

        private void SearchRecipeAndAssert(string searchTerm, string searchType)
        {
            string query = "SELECT * FROM recipe WHERE " + searchType + " = :searchTerm";
            using (OracleCommand cmd = new OracleCommand(query, conn))
            {
                cmd.Parameters.Add(new OracleParameter("searchTerm", searchTerm));
                using (OracleDataReader reader = cmd.ExecuteReader())
                {
                    var labels = recipeManager.Controls.OfType<Label>();
                    bool recipeFound = labels.Any(label => label.Text.StartsWith("Recipe Name: "));
                    Assert.That(recipeFound, Is.True, "Recipe not found after search.");

                }
            }
        }
    }
}
