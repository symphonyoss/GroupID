package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.mongo;

import com.mongodb.DuplicateKeyException;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.model.MakerCheckerIndex;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.model.MakercheckerEntity;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .CreateMakercheckerExcpetion;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .DuplicateMakercheckerExcpetion;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .GetMakercheckerException;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .MakercheckerNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .UpdateMakercheckerException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;
import org.symphonyoss.symphony.bots.helpdesk.service.mongo.MongoCondition;

/**
 * Created by alexandre-silva-daitan on 01/12/17.
 */

@Component
@Conditional(MongoCondition.class)
public class MongoMakercheckerDAO implements MakercheckerDao {

  private static final String COLLECTION_NAME = "helpdeskmakerchecker";

  private final MongoTemplate mongoTemplate;

  public MongoMakercheckerDAO(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Makerchecker createMakerchecker(Makerchecker makerchecker) {
    try {
      MakercheckerEntity entity = new MakercheckerEntity(makerchecker);
      this.mongoTemplate.insert(entity, COLLECTION_NAME);
      return makerchecker;

    } catch (DuplicateKeyException e) {
      throw new DuplicateMakercheckerExcpetion(makerchecker.getId(), e);
    } catch (Exception e) {
      throw new CreateMakercheckerExcpetion(makerchecker.getId(), e);
    }
  }

  @Override
  public Makerchecker updateMakerchecker(Long id, Makerchecker makerchecker) {
    Makerchecker saved = getMakerchecker(id);
    if (saved == null) {
      throw new MakercheckerNotFoundException(makerchecker.getId());
    }

    try {
      MakercheckerEntity entity = new MakercheckerEntity(makerchecker);
      this.mongoTemplate.save(entity, COLLECTION_NAME);
      return makerchecker;
    } catch (Exception e) {
      throw new UpdateMakercheckerException(makerchecker.getId(), e);
    }

  }

  @Override
  public Makerchecker getMakerchecker(Long id) {

    try {
      MakerCheckerIndex index = new MakerCheckerIndex(id.toString());

      MakercheckerEntity entity =
          this.mongoTemplate.findById(index, MakercheckerEntity.class, COLLECTION_NAME);

      if (entity != null) {
        Makerchecker makerchecker = new Makerchecker();
        makerchecker.setAgentId(entity.getId().getAgentId());
        makerchecker.setOwnerId(entity.getId().getOwnerId());
        makerchecker.setRoomId(entity.getId().getRoomId());
        makerchecker.setState(entity.getState());

        return makerchecker;
      }
      return null;
    } catch (Exception e) {
      throw new GetMakercheckerException(id, e);
    }

  }
}
