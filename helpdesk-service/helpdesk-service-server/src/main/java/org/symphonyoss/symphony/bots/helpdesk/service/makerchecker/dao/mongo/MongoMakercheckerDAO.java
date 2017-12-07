package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.mongo;

import com.mongodb.DuplicateKeyException;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
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
      this.mongoTemplate.insert(makerchecker, COLLECTION_NAME);
      return makerchecker;

    } catch (DuplicateKeyException e) {
      throw new DuplicateMakercheckerExcpetion(makerchecker.getId(), e);
    } catch (Exception e) {
      throw new CreateMakercheckerExcpetion(makerchecker.getId(), e);
    }
  }

  @Override
  public Makerchecker updateMakerchecker(String id, Makerchecker makerchecker) {
    Makerchecker saved = getMakerchecker(id);
    if (saved == null) {
      throw new MakercheckerNotFoundException(makerchecker.getId());
    }

    try {
      saved.setMakerId(makerchecker.getMakerId());
      saved.setState(makerchecker.getState());
      this.mongoTemplate.save(saved, COLLECTION_NAME);
      return makerchecker;
    } catch (Exception e) {
      throw new UpdateMakercheckerException(makerchecker.getId(), e);
    }

  }

  @Override
  public Makerchecker getMakerchecker(String id) {

    try {
      return this.mongoTemplate.findById(id, Makerchecker.class, COLLECTION_NAME);
    } catch (Exception e) {
      throw new GetMakercheckerException(id, e);
    }

  }
}
