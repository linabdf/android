package com.example.nutriscore;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductActivity extends AppCompatActivity {

    TextView txtName, txtNutri, txtCalories;
    ImageView imgProduct;
    TextView txtIngredients, txtAllergens, txtNutrition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        txtName = findViewById(R.id.txtName);
        txtNutri = findViewById(R.id.txtNutri);
        txtCalories = findViewById(R.id.txtCalories);
        imgProduct = findViewById(R.id.imgProduct);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtAllergens = findViewById(R.id.txtAllergens);
        txtNutrition = findViewById(R.id.txtNutrition);

        String barcode = getIntent().getStringExtra("BARCODE");

        new Thread(() -> fetchProduct(barcode)).start();
    }

    private void fetchProduct(String barcode) {
        try {
            URL url = new URL(
                    "https://world.openfoodfacts.net/api/v2/product/" + barcode
            );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject root = new JSONObject(json.toString());
            JSONObject product = root.getJSONObject("product");

            // 🔹 Infos générales
            String name = product.optString("product_name", "Nom inconnu");
            String nutri = product.optString("nutriscore_grade", "N/A");
            String imageUrl = product.optString("image_url", "");

            // 🔹 Ingrédients & allergènes
            String ingredients = product.optString("ingredients_text", "Non renseigné");
            String allergens = product.optString("allergens", "Non renseigné");

            // 🔹 Valeurs nutritionnelles
            JSONObject nutriments = product.optJSONObject("nutriments");

            String calories = nutriments != null ? nutriments.optString("energy-kcal_100g", "N/A") : "N/A";
            String proteins = nutriments != null ? nutriments.optString("proteins_100g", "N/A") : "N/A";
            String fat = nutriments != null ? nutriments.optString("fat_100g", "N/A") : "N/A";
            String saturatedFat = nutriments != null ? nutriments.optString("saturated-fat_100g", "N/A") : "N/A";
            String carbs = nutriments != null ? nutriments.optString("carbohydrates_100g", "N/A") : "N/A";
            String sugars = nutriments != null ? nutriments.optString("sugars_100g", "N/A") : "N/A";
            String fiber = nutriments != null ? nutriments.optString("fiber_100g", "N/A") : "N/A";
            String salt = nutriments != null ? nutriments.optString("salt_100g", "N/A") : "N/A";

            runOnUiThread(() -> {

                txtName.setText("Nom : " + name);
                txtNutri.setText("Nutri-Score : " + nutri.toUpperCase());
                txtCalories.setText("Calories (100g) : " + calories);

                txtIngredients.setText("Ingrédients :\n" + ingredients);
                txtAllergens.setText("Allergènes :\n" + allergens);

                txtNutrition.setText(
                        "Valeurs nutritionnelles (100g)\n" +
                                "Protéines : " + proteins + " g\n" +
                                "Lipides : " + fat + " g\n" +
                                "Saturés : " + saturatedFat + " g\n" +
                                "Glucides : " + carbs + " g\n" +
                                "Sucres : " + sugars + " g\n" +
                                "Fibres : " + fiber + " g\n" +
                                "Sel : " + salt + " g"
                );

                if (!imageUrl.isEmpty()) {
                    Glide.with(ProductActivity.this)
                            .load(imageUrl)
                            .into(imgProduct);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
