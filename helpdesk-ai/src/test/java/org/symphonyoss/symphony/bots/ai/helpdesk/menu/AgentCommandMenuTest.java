package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;

import java.util.Set;

/**
 * Unit tests for {@link AgentCommandMenu}
 * Created by robson on 26/03/18.
 */
public class AgentCommandMenuTest {

  @Test
  public void testCommandSet() {
    AgentCommandMenu menu = new AgentCommandMenu();

    assertEquals(StringUtils.EMPTY, menu.getCommandPrefix());

    Set<AiCommand> commandSet = menu.getCommandSet();

    assertTrue(commandSet.isEmpty());
  }
}
