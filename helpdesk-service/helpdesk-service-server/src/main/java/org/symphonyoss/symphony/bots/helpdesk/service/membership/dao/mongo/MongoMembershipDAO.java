package org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.mongo;

import org.springframework.context.annotation.Conditional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.MembershipDao;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.model.MembershipEntity;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.dao.model.MembershipIndex;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .CreateMembershipException;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .DeleteMembershipException;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .DuplicateMembershipException;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception.GetMembershipException;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .MembershipNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.membership.exception
    .UpdateMembershipException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Membership;
import org.symphonyoss.symphony.bots.helpdesk.service.mongo.MongoCondition;

/**
 * Mongo DAO for membership.
 *
 * Created by rsanchez on 22/11/17.
 */
@Component
@Conditional(MongoCondition.class)
public class MongoMembershipDAO implements MembershipDao {

  private static final String COLLECTION_NAME = "helpdeskmembership";

  private final MongoTemplate mongoTemplate;

  public MongoMembershipDAO(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Membership createMembership(Membership membership) {
    try {
      MembershipEntity entity = new MembershipEntity(membership);

      this.mongoTemplate.insert(entity, COLLECTION_NAME);

      return membership;
    } catch (DuplicateKeyException e) {
      throw new DuplicateMembershipException(membership.getGroupId(), membership.getId(), e);
    } catch (Exception e) {
      throw new CreateMembershipException(membership.getGroupId(), membership.getId(), e);
    }
  }

  @Override
  public void deleteMembership(String groupId, Long id) {
    try {
      MembershipIndex index = new MembershipIndex(groupId, id);

      MembershipEntity entity =
          this.mongoTemplate.findById(index, MembershipEntity.class, COLLECTION_NAME);

      if (entity != null) {
        this.mongoTemplate.remove(entity);
      }
    } catch (Exception e) {
      throw new DeleteMembershipException(groupId, id, e);
    }
  }

  @Override
  public Membership getMembership(String groupId, Long id) {
    try {
      MembershipIndex index = new MembershipIndex(groupId, id);

      MembershipEntity entity =
          this.mongoTemplate.findById(index, MembershipEntity.class, COLLECTION_NAME);

      if (entity != null) {
        Membership membership = new Membership();
        membership.setGroupId(entity.getId().getGroupId());
        membership.setId(entity.getId().getUserId());
        membership.setType(entity.getType());

        return membership;
      }

      return null;
    } catch (Exception e) {
      throw new GetMembershipException(groupId, id, e);
    }
  }

  @Override
  public Membership updateMembership(String groupId, Long id, Membership membership) {
    Membership saved = getMembership(groupId, id);

    if (saved == null) {
      throw new MembershipNotFoundException(groupId, id);
    }

    try {
      MembershipEntity entity = new MembershipEntity(membership);
      entity.getId().setGroupId(groupId);
      entity.getId().setUserId(id);

      this.mongoTemplate.save(entity, COLLECTION_NAME);

      return membership;
    } catch (Exception e) {
      throw new UpdateMembershipException(membership.getGroupId(), membership.getId(), e);
    }
  }

}
