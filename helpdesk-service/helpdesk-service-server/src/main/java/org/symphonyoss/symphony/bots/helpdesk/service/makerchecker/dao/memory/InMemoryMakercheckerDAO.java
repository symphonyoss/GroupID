package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.memory;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.memory.MemoryCondition;
import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexandre-silva-daitan on 01/12/17.
 */

@Component
@Conditional(MemoryCondition.class)
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
      throw new RuntimeException("");
    }

    saved.setAgentId(makerchecker.getAgentId());
    saved.state(makerchecker.getState());

    return createMakerchecker(saved);
  }

  @Override
  public Makerchecker getMakerchecker(String id) {
    return this.database.get(id);
  }


}
