package de.openknowledge.cdi.common.property;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 */

@RunWith(CdiJunit4TestRunner.class)
public class WildcardPropertiesInjectionTest {

  @Inject
  @Property(name = "framework.*", source = "de/openknowledge/cdi/common/property/test.properties")
  private Properties testProperty;

  @Test
  public void success() {
    assertEquals(2, testProperty.size());
    assertEquals("123", testProperty.get("framework.property.one"));
    assertEquals("12345", testProperty.get("framework.property.two"));

  }

}
