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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            String ingredientsWithAllergens = product.optString("ingredients_text_with_allergens", "Non renseigné");

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

                txtName.setText(name);
                txtNutri.setText(txtNutri.getText().toString() + " " + nutri.toUpperCase());
                switch (nutri.toUpperCase()) {
                    case "A":
                        txtNutri.setTextColor(getResources().getColor(R.color.dark_green));
                        break;
                    case "B":
                        txtNutri.setTextColor(getResources().getColor(R.color.light_green));
                        break;
                    case "C":
                        txtNutri.setTextColor(getResources().getColor(R.color.yellow));
                        break;
                    case "D":
                        txtNutri.setTextColor(getResources().getColor(R.color.dark_orange));
                        break;
                    case "E":
                        txtNutri.setTextColor(getResources().getColor(R.color.red));
                        break;
                    default:
                        txtNutri.setTextColor(getResources().getColor(R.color.black));
                        break;
                }

                txtNutrition.setText(
                        txtNutrition.getText().toString() +"\n" +
                                "Calories : " + calories + " kcal\n" +
                                "Protéines : " + proteins + " g\n" +
                                "Lipides : " + fat + " g\n" +
                                "Saturés : " + saturatedFat + " g\n" +
                                "Glucides : " + carbs + " g\n" +
                                "Sucres : " + sugars + " g\n" +
                                "Fibres : " + fiber + " g\n" +
                                "Sel : " + salt + " g"
                );

                List<String> extractedContents = extractContent(ingredientsWithAllergens);

                String ingredients = removeTags(ingredientsWithAllergens);

                txtIngredients.setText(txtIngredients.getText().toString() + "\n" + ingredients);

                StringBuilder allergens = new StringBuilder();
                for (String content : extractedContents) {
                    if (allergens.toString().isEmpty()) {
                        allergens.append(content);
                    }
                    if (!allergens.toString().contains(content)) {
                        allergens.append(", ").append(content);
                    }
                }

                txtAllergens.setText("Allergènes :\n" + allergens);


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


    public static List<String> extractContent(String input) {
        // L'expression régulière pour capturer le contenu entre <span> et </span>
        String regex = "<span[^>]*>(.*?)</span>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> contents = new ArrayList<>();

        // Itérer sur toutes les correspondances trouvées
        while (matcher.find()) {
            // Le groupe 1 (group(1)) contient le texte capturé par (.*?)
            contents.add(matcher.group(1));
        }

        return contents;
    }

    public static String removeTags(String input) {
        // L'expression régulière pour correspondre à <span> ou </span>
        String regex = "<span[^>]*>|</span>";

        // On remplace toutes les occurrences par une chaîne vide ("")
        String resultString = input.replaceAll(regex, "");

        return resultString;
    }

}
