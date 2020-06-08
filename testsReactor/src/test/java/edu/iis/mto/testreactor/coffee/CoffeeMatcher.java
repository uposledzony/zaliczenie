package edu.iis.mto.testreactor.coffee;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CoffeeMatcher extends TypeSafeMatcher<Coffee> {

    private Coffee toMatch;

    private CoffeeMatcher(Coffee toMatch) {
        this.toMatch = toMatch;
    }

    public static CoffeeMatcher isCoffee(Coffee toMatch) {
        return new CoffeeMatcher(toMatch);
    }

    @Override
    protected boolean matchesSafely(Coffee coffee) {
        return coffee.getMilkAmout().equals(toMatch.getMilkAmout()) && coffee.getCoffeeWeigthGr().equals(toMatch.getCoffeeWeigthGr()) && coffee.getWaterAmount().equals(toMatch.getWaterAmount());
    }

    @Override
    public void describeTo(Description description) {

    }
}
