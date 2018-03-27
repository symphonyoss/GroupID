package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.AcceptTicketCommand;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.AddMemberCommand;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unit tests for {@link AgentCommandMenu}
 * Created by robson on 26/03/18.
 */
public class AgentCommandMenuTest {

  private static final String DEFAULT_PREFIX = "/";

  private static final String ACCEPT_TICKET_COMMAND = "Accept ticket";

  private static final String ADD_MEMBER_COMMAND = "Add member";

  private HelpDeskAiConfig config = new HelpDeskAiConfig();

  @Before
  public void init() {
    config.setDefaultPrefix(DEFAULT_PREFIX);
    config.setAcceptTicketCommand(ACCEPT_TICKET_COMMAND);
    config.setAddMemberCommand(ADD_MEMBER_COMMAND);
  }

  @Test
  public void testCommandSet() {
    AgentCommandMenu menu = new AgentCommandMenu(config);

    assertEquals(DEFAULT_PREFIX, menu.getCommandPrefix());

    Set<AiCommand> commandSet = menu.getCommandSet();

    assertEquals(2, commandSet.size());

    List<Class> classes = commandSet.stream()
        .map(AiCommand::getClass)
        .collect(Collectors.toList());

    assertTrue(classes.contains(AcceptTicketCommand.class));
    assertTrue(classes.contains(AddMemberCommand.class));
  }
}
