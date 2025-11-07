package org.example;

/**
 * Модель ингредиента.
 * Ингредиент: начинка или соус.
 * У ингредиента есть тип (начинка или соус), название и цена.
 */
public class Ingredient {

    public org.example.IngredientType type;
    public String name;
    public float price;

    public Ingredient(org.example.IngredientType type, String name, float price) {
        this.type = type;
        this.name = name;
        this.price = price;
    }

    public float getPrice() {

        return price;
    }

    public String getName() {

        return name;
    }

    public org.example.IngredientType getType() {

        return type;
    }
}