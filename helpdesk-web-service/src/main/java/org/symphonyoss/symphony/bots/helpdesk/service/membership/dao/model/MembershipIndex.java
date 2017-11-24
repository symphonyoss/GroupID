package org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.model;

/**
 * Created by rsanchez on 23/11/17.
 */
public class MembershipIndex {

  private String groupId;

  private Long userId;

  public MembershipIndex() {
    super();
  }

  public MembershipIndex(String groupId, Long userId) {
    this.groupId = groupId;
    this.userId = userId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
