package org.example;

import org.example.Bun;
import org.example.Database;
import org.example.IngredientType;
import org.example.Ingredient;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс с методами по работе с базой данных.
 */
public class Database {

    private final List<Bun> buns = new ArrayList<>();
    private final List<org.example.Ingredient> ingredients = new ArrayList<>();

    public Database() {
        buns.add(new Bun("black bun", 100));
        buns.add(new Bun("white bun", 200));
        buns.add(new Bun("red bun", 300));

        ingredients.add(new org.example.Ingredient(org.example.IngredientType.SAUCE, "hot sauce", 100));
        ingredients.add(new org.example.Ingredient(org.example.IngredientType.SAUCE, "sour cream", 200));
        ingredients.add(new org.example.Ingredient(org.example.IngredientType.SAUCE, "chili sauce", 300));

        ingredients.add(new org.example.Ingredient(org.example.IngredientType.FILLING, "cutlet", 100));
        ingredients.add(new org.example.Ingredient(org.example.IngredientType.FILLING, "dinosaur", 200));
        ingredients.add(new org.example.Ingredient(org.example.IngredientType.FILLING, "sausage", 300));
    }

    public List<Bun> availableBuns() {
        return buns;
    }

    public List<org.example.Ingredient> availableIngredients() {

        return ingredients;
    }

}