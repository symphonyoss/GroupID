package org.symphonyoss.symphony.bots.helpdesk.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.symphonyoss.symphony.bots.helpdesk.service.model.TicketSearchResponse;

/**
 * Created by rsanchez on 21/02/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { HelpDeskServiceInit.class })
public class HelpDeskServiceStories {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelpDeskServiceStories.class);

  @Autowired
  private TestRestTemplate template;

  @Test
  public void dummyTest() {
    ResponseEntity<TicketSearchResponse> entity =
        template.getForEntity("/v1/ticket/search=testgroup", TicketSearchResponse.class);

    TicketSearchResponse tickets = entity.getBody();
    LOGGER.info("Ticket search: " + tickets);
  }

}
