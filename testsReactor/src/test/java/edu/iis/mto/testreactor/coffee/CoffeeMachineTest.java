package edu.iis.mto.testreactor.coffee;

import static edu.iis.mto.testreactor.coffee.CoffeeMatcher.isCoffee;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import edu.iis.mto.testreactor.coffee.milkprovider.MilkProviderException;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CoffeeMachineTest {

    private static final int NON_ZERO_MILK_AMOUNT = 100;
    public static final double GRINDED_COFFEE_AMOUNT = 23.0;
    public static final int STANDARD_WATER = 200;
    public static final int SMALL_WATER = 100;
    public static final int DOUBLE_WATER = 400;
    @Mock
    private Grinder mockGrinder;
    @Mock
    private MilkProvider milkProviderMock;
    @Mock
    private CoffeeReceipes coffeeRecipesMock;

    private CoffeeReceipe defaultTestReceipe;
    private CoffeeReceipe faultyReceipe;
    private CoffeOrder defaultOrder;

    private static Map<CoffeeSize, Integer> getValidSizes() {
        var map = new HashMap<CoffeeSize, Integer>();
        map.put(CoffeeSize.STANDARD, STANDARD_WATER);
        map.put(CoffeeSize.SMALL, SMALL_WATER);
        map.put(CoffeeSize.DOUBLE, DOUBLE_WATER);

        return map;
    }

    private static Map<CoffeeSize, Integer> getFaultySizes() {
        var map = new HashMap<CoffeeSize, Integer>();
        map.put(CoffeeSize.STANDARD, null);
        map.put(CoffeeSize.SMALL, null);
        map.put(CoffeeSize.DOUBLE, null);

        return map;
    }

    @BeforeEach
    void setUp() {
        defaultTestReceipe = CoffeeReceipe.builder().withWaterAmounts(getValidSizes()).withMilkAmount(0).build();
        defaultOrder = CoffeOrder.builder().withSize(CoffeeSize.STANDARD).withType(CoffeType.CAPUCCINO).build();
        faultyReceipe = CoffeeReceipe.builder().withWaterAmounts(getFaultySizes()).withMilkAmount(0).build();
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
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);
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

    @Test
    void ifWaterAmountIsNullInProvidedReceipeCoffeeMachineShouldThrowUnsupportedCoffeeSizeExceptionAfterMakeMethodCall() {
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(coffeeRecipesMock.getReceipe(Mockito.any())).thenReturn(faultyReceipe);
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);
        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);
        Executable invalidMakeCoffeeCall = () -> testedMachine.make(defaultOrder);
        assertThrows(UnsupportedCoffeeSizeException.class, invalidMakeCoffeeCall);
    }

    @Test
    void ifMilkProviderThrewExceptionThenCoffeeShouldHaveAmountOfMilkThatIsEqualToZero() throws MilkProviderException {
        var testedReceipe = CoffeeReceipe.builder().withWaterAmounts(getValidSizes()).withMilkAmount(NON_ZERO_MILK_AMOUNT).build();
        doThrow(MilkProviderException.class).when(milkProviderMock).heat();
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(coffeeRecipesMock.getReceipe(Mockito.any())).thenReturn(testedReceipe);
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);
        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);
        var coffee = testedMachine.make(defaultOrder);

        assertThat(coffee.getMilkAmout(), is(equalTo(0)));
    }

    @Test
    void ifMilkAmountInReceipeIsEqualToZeroThenMilkProviderShouldNotBeCalled() throws MilkProviderException {
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(coffeeRecipesMock.getReceipe(Mockito.any())).thenReturn(defaultTestReceipe);
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);
        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);
        testedMachine.make(defaultOrder);

        verify(milkProviderMock, times(0)).heat();
        verify(milkProviderMock, times(0)).pour(0);
    }

    @Test
    void coffeeMachineShouldMakeStandardCapuccinoWithAmountOfWaterEqualStandardWaterAmountAndNonZeroMilkAmountAndCoffeeGrams() {
        var expectedCoffee = standardCapuccino(NON_ZERO_MILK_AMOUNT, STANDARD_WATER, GRINDED_COFFEE_AMOUNT);
        var testedReceipe = CoffeeReceipe.builder().withWaterAmounts(getValidSizes()).withMilkAmount(NON_ZERO_MILK_AMOUNT).build();
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(coffeeRecipesMock.getReceipe(Mockito.any())).thenReturn(testedReceipe);
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);

        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);

        var gotCoffee = testedMachine.make(defaultOrder);

        assertThat(expectedCoffee, isCoffee(gotCoffee));
    }

    @Test
    void ifMilkAmountInReceipeIs0ThenCoffeeShouldHave0Milk() {
        when(mockGrinder.canGrindFor(Mockito.any())).thenReturn(true);
        when(coffeeRecipesMock.getReceipe(Mockito.any())).thenReturn(defaultTestReceipe);
        when(mockGrinder.grind(Mockito.any())).thenReturn(GRINDED_COFFEE_AMOUNT);

        var testedMachine = new CoffeeMachine(mockGrinder, milkProviderMock, coffeeRecipesMock);

        var gotCoffee = testedMachine.make(defaultOrder);
        assertThat(gotCoffee.getMilkAmout(), is(equalTo(0)));
    }

    private Coffee standardCapuccino(int milkAmount, int waterAmount, double coffeeGrams){
        var c = new Coffee();
        c.setMilkAmout(milkAmount);
        c.setCoffeeWeigthGr(coffeeGrams);
        c.setWaterAmount(waterAmount);
        return c;
    }
}
