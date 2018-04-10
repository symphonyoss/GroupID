package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.client;

import org.symphonyoss.symphony.bots.helpdesk.service.BaseClient;
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
public class MakercheckerClient extends BaseClient {

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

  /**
   * Get maker/checker data.
   *
   * @param jwt User JWT
   * @param id Maker/checker id
   * @return Maker/checker object
   */
  public Makerchecker getMakerchecker(String jwt, String id) {
    String authorization = getAuthorizationHeader(jwt);

    try {
      return makercheckerApi.getMakerchecker(id, authorization);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Get makerchecker failed: " + id, e);
    }
  }

  /**
   * Create maker/checker object
   *
   * @param jwt User JWT
   * @param id Maker/checker ID
   * @param makerId User id that creates this object
   * @param streamId Stream ID
   * @param attachmentId Attachment ID
   * @param attachmentName Attachment name
   * @param messageId Message ID
   * @param timeStamp Creation timestamp
   * @param proxyToStreamId List of streams
   * @return Maker/checker object created
   */
  public Makerchecker createMakerchecker(String jwt, String id, Long makerId, String streamId,
      String attachmentId, String attachmentName, String messageId, Long timeStamp,
      List<String> proxyToStreamId) {
    String authorization = getAuthorizationHeader(jwt);

    Makerchecker makerchecker = new Makerchecker();
    makerchecker.setId(id);
    makerchecker.setMakerId(makerId);
    makerchecker.setGroupId(groupId);
    makerchecker.setStreamId(streamId);
    makerchecker.setTimeStamp(timeStamp);
    makerchecker.setMessageId(messageId);
    makerchecker.setAttachmentId(attachmentId);
    makerchecker.setAttachmentName(attachmentName);
    makerchecker.setProxyToStreamIds(proxyToStreamId);
    makerchecker.setState(AttachmentStateType.OPENED.getState());

    try {
      return makercheckerApi.createMakerchecker(makerchecker, authorization);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Creating makerchecker failed: " + id, e);
    }
  }

  /**
   * Update maker/checker data
   *
   * @param jwt User JWT
   * @param makerchecker Maker/checker object to be updated
   * @return Maker/checker object updated
   */
  public Makerchecker updateMakerchecker(String jwt, Makerchecker makerchecker) {
    String authorization = getAuthorizationHeader(jwt);

    try {
      return makercheckerApi.updateMakerchecker(makerchecker.getId(), makerchecker, authorization);
    } catch (ApiException e) {
      throw new HelpDeskApiException("Updating makerchecker failed: " + makerchecker.getId(), e);
    }
  }
}
