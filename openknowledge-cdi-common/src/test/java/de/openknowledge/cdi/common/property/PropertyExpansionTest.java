package de.openknowledge.cdi.common.property;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 */
@RunWith(CdiJunit4TestRunner.class)
public class PropertyExpansionTest {

  @Inject
  @Property(name = "testSystemProperty", source = "de/openknowledge/cdi/common/property/test.properties")
  private String systemProperty;


  @Inject
  @Property(name = "backwardsReference", source = "de/openknowledge/cdi/common/property/test.properties")
   private int backwardsReference;

  @Test
  public void testSystemPropertyExpansion() {
    assertEquals(System.getProperty("java.io.tmpdir"), systemProperty);
    assertEquals(50, backwardsReference);
  }
}
