package org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.model;

import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

/**
 * Created by rsanchez on 23/11/17.
 */
public class MembershipEntity {

  private MembershipIndex id;

  private String type;

  public MembershipEntity() {
    super();
  }

  public MembershipEntity(Membership membership) {
    MembershipIndex index = new MembershipIndex();
    index.setGroupId(membership.getGroupId());
    index.setUserId(membership.getId());

    this.id = index;
    this.type = membership.getType();
  }

  public MembershipIndex getId() {
    return id;
  }

  public void setId(MembershipIndex id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
