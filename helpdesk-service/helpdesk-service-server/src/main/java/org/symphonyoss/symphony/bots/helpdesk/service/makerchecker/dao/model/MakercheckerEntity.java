package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.model;

import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

/**
 * Created by alexandre-silva-daitan on 01/12/17.
 */
public class MakercheckerEntity {

  private MakerCheckerIndex id;

  private String state;

  public MakercheckerEntity() {
    super();
  }

  public MakercheckerEntity(Makerchecker makerchecker) {
    MakerCheckerIndex index = new MakerCheckerIndex();
    index.setOwnerId(makerchecker.getOwnerId());
    index.setRoomId(makerchecker.getRoomId());
    index.setAgentId(makerchecker.getAgentId());

    this.id = index;
    this.state = makerchecker.getState();
  }

  public MakerCheckerIndex getId() {
    return id;
  }

  public void setId(
      MakerCheckerIndex id) {
    this.id = id;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
