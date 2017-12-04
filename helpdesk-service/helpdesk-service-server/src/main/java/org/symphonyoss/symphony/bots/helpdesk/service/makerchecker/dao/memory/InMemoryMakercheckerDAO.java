package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.memory;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.MakercheckerDao;
import org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao.model.MakerCheckerIndex;
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
  private final Map<MakerCheckerIndex, Makerchecker> database = new HashMap<>();

  @Override
  public Makerchecker createMakerchecker(Makerchecker makerchecker) {
    MakerCheckerIndex index = new MakerCheckerIndex();
    index.setAgentId(makerchecker.getAgentId());
    index.setRoomId(makerchecker.getRoomId());
    index.setOwnerId(makerchecker.getOwnerId());

    this.database.put(index, makerchecker);
    return makerchecker;
  }

  @Override
  public Makerchecker updateMakerchecker(Long id, Makerchecker makerchecker) {
    Makerchecker saved = getMakerchecker(id);
    if (saved == null) {
      throw new RuntimeException("");
    }

    saved.setAgentId(makerchecker.getAgentId());
    saved.state(makerchecker.getState());

    return createMakerchecker(makerchecker);
  }

  @Override
  public Makerchecker getMakerchecker(Long id) {
    MakerCheckerIndex index = new MakerCheckerIndex(id.toString());
    return this.database.get(index);
  }


}
