package uk.ac.ebi.intact.core.lifecycle.correctionassigner;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SelectionRandomizerTest {
    @Test
    public void testRandomSelection() throws Exception {
        SelectionRandomizer<String> selectionRandomizer = new SelectionRandomizer<String>();
        selectionRandomizer.addObject("lala", 80);
        selectionRandomizer.addObject("lolo", 20);

        String selected = selectionRandomizer.randomSelection();

        Assert.assertTrue(selected.equals(selected) || "lolo".equals(selected));
    }

    @Test
    public void testRandomSelection_allTheSame() throws Exception {
        SelectionRandomizer<String> selectionRandomizer = new SelectionRandomizer<String>();
        selectionRandomizer.addObject("lala", 80);

        String selected = selectionRandomizer.randomSelection();

        Assert.assertEquals("lala", selected);
    }

    @Test
    public void testRandomSelectionWithObj() throws Exception {
        SelectionRandomizer<String> selectionRandomizer = new SelectionRandomizer<String>();
        selectionRandomizer.addObject("lala", 80);
        selectionRandomizer.addObject("lolo", 20);

        String selected = selectionRandomizer.randomSelection("lala");

        Assert.assertTrue("lolo".equals(selected));
    }

    @Test
    public void testRandomSelectionWithObj_allTheSame() throws Exception {
        SelectionRandomizer<String> selectionRandomizer = new SelectionRandomizer<String>();
        selectionRandomizer.addObject("lala", 80);

        String selected = selectionRandomizer.randomSelection("lala");

        Assert.assertNull(selected);
    }
}
