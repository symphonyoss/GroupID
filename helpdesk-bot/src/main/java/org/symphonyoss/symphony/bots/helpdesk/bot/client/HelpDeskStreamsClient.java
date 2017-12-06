package org.symphonyoss.symphony.bots.helpdesk.bot.client;

import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientConfigID;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.impl.StreamsClientImpl;
import org.symphonyoss.symphony.clients.model.SymRoomAttributes;
import org.symphonyoss.symphony.clients.model.SymRoomDetail;
import org.symphonyoss.symphony.clients.model.SymRoomSystemInfo;
import org.symphonyoss.symphony.clients.model.SymRoomTag;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.V3RoomAttributes;
import org.symphonyoss.symphony.pod.model.V3RoomDetail;

/**
 * Created by rsanchez on 05/12/17.
 */
public class HelpDeskStreamsClient extends StreamsClientImpl {

  private final ApiClient apiClient;
  private final SymAuth symAuth;

  public HelpDeskStreamsClient(SymAuth symAuth, SymphonyClientConfig config) {
    super(symAuth, config);

    this.symAuth = symAuth;
    this.apiClient = Configuration.getDefaultApiClient();
    this.apiClient.setBasePath(config.get(SymphonyClientConfigID.POD_URL));
  }

  public SymRoomDetail createChatRoom(SymRoomAttributes roomAttributes, boolean roomHistory) throws StreamsException {
    if(roomAttributes == null) {
      throw new NullPointerException("Room Attributes were not provided..");
    } else {
      HelpDeskStreamsApi streamsApi = new HelpDeskStreamsApi(this.apiClient);

      try {
        return toSymRoomDetail(
            streamsApi.v3RoomCreatePost(toV3RoomAttributes(roomAttributes, roomHistory),
                this.symAuth.getSessionToken().getToken()));
      } catch (ApiException e) {
        throw new StreamsException(
            "Failed to obtain room information while creating room: " + roomAttributes.getName(),
            e);
      }
    }
  }

  private V3RoomAttributes toV3RoomAttributes(SymRoomAttributes roomAttributes, boolean roomHistory) {
    V3RoomAttributes v3RoomAttributes = new V3RoomAttributes();
    v3RoomAttributes.setPublic(roomAttributes.getPublic());
    v3RoomAttributes.setCopyProtected(roomAttributes.getCopyProtected());
    v3RoomAttributes.setDescription(roomAttributes.getDescription());
    v3RoomAttributes.setDiscoverable(roomAttributes.getDiscoverable());
    v3RoomAttributes.setKeywords(SymRoomTag.toRoomTags(roomAttributes.getKeywords()));
    v3RoomAttributes.setMembersCanInvite(roomAttributes.getMembersCanInvite());
    v3RoomAttributes.setName(roomAttributes.getName());
    v3RoomAttributes.setReadOnly(roomAttributes.getReadOnly());
    v3RoomAttributes.setViewHistory(roomHistory);
    return v3RoomAttributes;
  }

  private SymRoomDetail toSymRoomDetail(V3RoomDetail roomDetail) {
    SymRoomDetail symRoomDetail = new SymRoomDetail();
    symRoomDetail.setRoomAttributes(toSymRoomAttributes(roomDetail.getRoomAttributes()));
    symRoomDetail.setRoomSystemInfo(SymRoomSystemInfo.toSymRoomSystemInfo(roomDetail.getRoomSystemInfo()));
    return symRoomDetail;
  }

  private SymRoomAttributes toSymRoomAttributes(V3RoomAttributes roomAttributes) {
    SymRoomAttributes symRoomAttributes = new SymRoomAttributes();
    symRoomAttributes.setPublic(roomAttributes.getPublic());
    symRoomAttributes.setCopyProtected(roomAttributes.getCopyProtected());
    symRoomAttributes.setDescription(roomAttributes.getDescription());
    symRoomAttributes.setDiscoverable(roomAttributes.getDiscoverable());
    symRoomAttributes.setKeywords(SymRoomTag.toSymRoomTagsV2(roomAttributes.getKeywords()));
    symRoomAttributes.setMembersCanInvite(roomAttributes.getMembersCanInvite());
    symRoomAttributes.setName(roomAttributes.getName());
    symRoomAttributes.setReadOnly(roomAttributes.getReadOnly());

    return symRoomAttributes;
  }
}
