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

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }

    MembershipIndex index = (MembershipIndex) o;

    if (groupId != null ? !groupId.equals(index.groupId) : index.groupId != null) { return false; }
    return userId != null ? userId.equals(index.userId) : index.userId == null;

  }

  @Override
  public int hashCode() {
    int result = groupId != null ? groupId.hashCode() : 0;
    result = 31 * result + (userId != null ? userId.hashCode() : 0);
    return result;
  }
}
