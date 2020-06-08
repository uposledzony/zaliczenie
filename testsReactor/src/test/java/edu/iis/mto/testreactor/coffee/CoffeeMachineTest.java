package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

class CoffeeMachineTest {

    @Mock
    private Grinder mockGrinder;
    @Mock
    private MilkProvider milkProviderMock;
    @Mock
    private CoffeeReceipes coffeeRecipesMock;

    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

    @Test
    void passingNullGrinderShouldCauseNPEThrownDuringCoffeeMachineInitialization() {
        Executable invalidConstructorCall = () -> new CoffeeMachine(null, milkProviderMock, coffeeRecipesMock);
        assertThrows(NullPointerException.class, invalidConstructorCall);
    }

    @Test
    void passingNullMilkProviderShouldCauseNPEThrownDuringCoffeeMachineInitialization() {
        Executable invalidConstructorCall = () -> new CoffeeMachine(mockGrinder, null, coffeeRecipesMock);
        assertThrows(NullPointerException.class, invalidConstructorCall);
    }
    @Test
    void passingNullCoffeeRecipesShouldCauseNPEThrownDuringCoffeeMachineInitialization() {
        Executable invalidConstructorCall = () -> new CoffeeMachine(mockGrinder, milkProviderMock, null);
        assertThrows(NullPointerException.class, invalidConstructorCall);
    }

}
