package org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.model.MembershipIndex;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.mongo.MongoMembershipDAO;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .MembershipNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO component responsible for managing membership objects in-memory. This component will be
 * created only if the {@link MongoMembershipDAO} component wasn't created previously.
 * <p>
 * This class should be used only for tests purpose.
 * <p>
 * Created by rsanchez on 22/11/17.
 */
@Component
@ConditionalOnMissingBean(MongoMembershipDAO.class)
public class InMemoryMembershipDAO implements MembershipDao {

  private final Map<MembershipIndex, Membership> database = new HashMap<>();

  @Override
  public Membership createMembership(Membership membership) {
    MembershipIndex index = new MembershipIndex();
    index.setGroupId(membership.getGroupId());
    index.setUserId(membership.getId());

    this.database.put(index, membership);
    return membership;
  }

  @Override
  public void deleteMembership(String groupId, Long id) {
    MembershipIndex index = new MembershipIndex(groupId, id);
    this.database.remove(index);
  }

  @Override
  public Membership getMembership(String groupId, Long id) {
    MembershipIndex index = new MembershipIndex(groupId, id);
    return this.database.get(index);
  }

  @Override
  public Membership updateMembership(String groupId, Long id, Membership membership) {
    Membership saved = getMembership(groupId, id);

    if (saved == null) {
      throw new MembershipNotFoundException(groupId, id);
    }

    membership.setGroupId(groupId);
    membership.setId(id);

    return createMembership(membership);
  }

}
