package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.memory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.mongo.MongoMakercheckerDAO;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.exception
    .MakercheckerNotFoundException;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO component responsible for managing maker/checker objects in-memory. This component will be
 * created only if the {@link MongoMakercheckerDAO} component wasn't created previously.
 * <p>
 * This class should be used only for tests purpose.
 * <p>
 * Created by alexandre-silva-daitan on 01/12/17.
 */
@Component
@ConditionalOnMissingBean(MongoMakercheckerDAO.class)
public class InMemoryMakercheckerDAO implements MakercheckerDao {
  private final Map<String, Makerchecker> database = new HashMap<>();

  @Override
  public Makerchecker createMakerchecker(Makerchecker makerchecker) {
    this.database.put(makerchecker.getId(), makerchecker);
    return makerchecker;
  }

  @Override
  public Makerchecker updateMakerchecker(String id, Makerchecker makerchecker) {
    Makerchecker saved = getMakerchecker(id);
    if (saved == null) {
      throw new MakercheckerNotFoundException(makerchecker.getId());
    }

    saved.setId(makerchecker.getId());

    return createMakerchecker(saved);
  }

  @Override
  public Makerchecker getMakerchecker(String id) {
    return this.database.get(id);
  }


}
