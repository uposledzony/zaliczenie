package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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

}
