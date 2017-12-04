package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.model;

/**
 * Created by alexandre-silva-daitan on 01/12/17.
 */
public class MakerCheckerIndex {

  private String ownerId;
  private String agentId;
  private String roomId;

  public MakerCheckerIndex() { super(); }

  public MakerCheckerIndex(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }

    MakerCheckerIndex index = (MakerCheckerIndex) o;

    if (ownerId != null ? !ownerId.equals(index.ownerId) : index.ownerId != null) { return false; }
    if (agentId != null ? !agentId.equals(index.agentId) : index.agentId != null) { return false; }
    return roomId != null ? roomId.equals(index.roomId) : index.roomId == null;
  }

  @Override
  public int hashCode() {
    int result = ownerId != null ? ownerId.hashCode() : 0;
    result = 31 * result + (agentId != null ? agentId.hashCode() : 0);
    result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
    return result;
  }
}
