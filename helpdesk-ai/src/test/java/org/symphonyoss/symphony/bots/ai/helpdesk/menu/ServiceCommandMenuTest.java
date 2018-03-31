package org.symphonyoss.symphony.bots.ai.helpdesk.menu;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.symphony.bots.ai.helpdesk.command.CloseTicketCommand;
import org.symphonyoss.symphony.bots.ai.helpdesk.config.HelpDeskAiConfig;
import org.symphonyoss.symphony.bots.ai.helpdesk.conversation.IdleTimerManager;
import org.symphonyoss.symphony.bots.ai.model.AiCommand;
import org.symphonyoss.symphony.bots.helpdesk.service.ticket.client.TicketClient;

import java.util.Set;

/**
 * Unit tests for {@link ServiceCommandMenu}
 * Created by robson on 26/03/18.
 */
public class ServiceCommandMenuTest {

  private static final String AGENT_ROOM_PREFIX = "/";

  private static final String CLOSE_TICKET_COMMAND = "Close";

  private static final String GROUP_ID = "group";

  private HelpDeskAiConfig config = new HelpDeskAiConfig();

  private TicketClient ticketClient = new TicketClient(GROUP_ID, StringUtils.EMPTY);

  private SymphonyClient symphonyClient = new SymphonyBasicClient();

  private IdleTimerManager timerManager = new IdleTimerManager();

  @Before
  public void init() {
    config.setAgentServiceRoomPrefix(AGENT_ROOM_PREFIX);
    config.setCloseTicketCommand(CLOSE_TICKET_COMMAND);
  }

  @Test
  public void testCommandSet() {
    ServiceCommandMenu menu = new ServiceCommandMenu(config, ticketClient, symphonyClient, timerManager);

    assertEquals(AGENT_ROOM_PREFIX, menu.getCommandPrefix());

    Set<AiCommand> commandSet = menu.getCommandSet();

    assertEquals(1, commandSet.size());

    AiCommand command = commandSet.iterator().next();

    assertEquals(CloseTicketCommand.class, command.getClass());
  }

}
