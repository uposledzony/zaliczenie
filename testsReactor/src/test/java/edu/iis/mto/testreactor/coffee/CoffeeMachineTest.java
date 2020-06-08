package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoffeeMachineTest {

    @Mock
    private Grinder mockGrinder;
    @Mock
    private MilkProvider milkProviderMock;
    @Mock
    private CoffeeReceipes coffeeRecipesMock;

    private CoffeeReceipe defaultTestReceipe;
    private CoffeOrder defaultOrder;
    
    @BeforeEach
    void setUp() {
        defaultTestReceipe = CoffeeReceipe.builder().build();
        defaultOrder = CoffeOrder.builder().withSize(CoffeeSize.STANDARD).withType(CoffeType.CAPUCCINO).build();
    }

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

    @Test
    void ifCoffeeReceipesGetReceipeMethodReturnedNullReceipeThenCoffeeMachineShouldThrowUnsupportedCoffeeException() {
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(mockGrinder.grind(Mockito.any())).thenReturn(23.0);
        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);
        Executable invalidMakeCoffeeCall = () -> testedMachine.make(defaultOrder);
        assertThrows(UnsupportedCoffeeException.class, invalidMakeCoffeeCall);
    }

    @Test
    void ifGrinderCannotGrindCoffeeOfSomeTypeThenCoffeeMachineShouldThrowNoCoffeeBeansExceptionAfterMakeMethodCall() {
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(false);
        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);
        Executable invalidMakeCoffeeCall = () -> testedMachine.make(defaultOrder);
        assertThrows(NoCoffeeBeansException.class, invalidMakeCoffeeCall);
    }
}
