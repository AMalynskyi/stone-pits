package com.alexm.game.stonepits;

import com.alexm.game.stonepits.entity.component.StonePosition;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit testing suite
 * Marked as UnitTestCategory to specify that it is quick type of testing and needs to be included in build&assemble actions
 */
@Category(UnitTestCategory.class)
@RunWith(JUnit4.class)
public class SPGameUnitTesting {

    /**
     * Unit test for equal method
     * @throws Exception
     */
    @Test
    public void testStonePositionEquality() throws Exception{
        StonePosition a = new StonePosition();
        a.setCoordinates(1f, 30.01f);

        StonePosition b = new StonePosition();
        b.setCoordinates(32.43f, 2f);

        StonePosition c = new StonePosition();
        c.setCoordinates(1f, 30.01f);

        assertThat(a).isEqualTo(c);
        assertThat(b).isNotEqualTo(c);
        assertThat(a.equals(c)).isTrue();
    }

}
