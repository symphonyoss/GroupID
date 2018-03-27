package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;

import java.util.Set;

/**
 * Unit tests for {@link ClientCommandMenu}
 * Created by robson on 26/03/18.
 */
public class ClientCommandMenuTest {

  @Test
  public void testCommandSet() {
    ClientCommandMenu menu = new ClientCommandMenu();

    Set<AiCommand> commandSet = menu.getCommandSet();

    assertEquals(StringUtils.EMPTY, menu.getCommandPrefix());
    assertTrue(commandSet.isEmpty());
  }

}
