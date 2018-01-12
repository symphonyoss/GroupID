package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client;

import org.symphonyoss.symphony.bots.helpdesk.service.HelpDeskApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.api.MakercheckerApi;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiClient;
import org.symphonyoss.symphony.bots.helpdesk.service.client.ApiException;
import org.symphonyoss.symphony.bots.helpdesk.service.client.Configuration;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

import java.util.List;

/**
 * Created by alexandre-silva-daitan on 04/12/17.
 */
public class MakercheckerClient {
  public enum AttachmentStateType {

    OPENED("OPENED"),
    APPROVED("APPROVED"),
    DENIED("DENIED");

    private String state;

    AttachmentStateType(String state) {
      this.state = state;
    }

    public String getState() {
      return state;
    }
  }


  private final String groupId;

  private final MakercheckerApi makercheckerApi;

  public MakercheckerClient(String groupId, String ticketServiceUrl) {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(ticketServiceUrl);
    makercheckerApi = new MakercheckerApi(apiClient);
    this.groupId = groupId;
  }

  public Makerchecker getMakerchecker(String id) {
    try {
      return makercheckerApi.getMakerchecker(id);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Get makerchecker failed: " + id, e);
    }
  }

  public Makerchecker createMakerchecker(String id, Long makerId, String streamId, String attachmentId,
      String messageId, Long timeStamp, List<String> proxyToStreamId) {
    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setId(id);
    makerchecker.setMakerId(makerId);
    makerchecker.setGroupId(groupId);
    makerchecker.setStreamId(streamId);
    makerchecker.setTimeStamp(timeStamp);
    makerchecker.setMessageId(messageId);
    makerchecker.setAttachmentId(attachmentId);
    makerchecker.setProxyToStreamIds(proxyToStreamId);
    makerchecker.setState(AttachmentStateType.OPENED.getState());

    try {
      return makercheckerApi.createMakerchecker(makerchecker);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Creating ticket failed: " + id, e);
    }
  }

  public Makerchecker updateMakerchecker(Makerchecker makerchecker) {
    try {
      return makercheckerApi.updateMakerchecker(makerchecker.getId(), makerchecker);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Updating makerchecker failed: " + makerchecker.getId(), e);
    }
  }
}
