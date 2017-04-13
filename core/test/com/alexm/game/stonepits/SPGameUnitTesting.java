package com.alexm.game.stonepits;

import com.alexm.game.stonepits.entity.component.StonePosition;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.*;

/**
 * User: Oleksandr Malynskyi
 * Date: 04/11/17
 */
@Category(UnitTestCategory.class)
@RunWith(JUnit4.class)
public class SPGameUnitTesting {


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
