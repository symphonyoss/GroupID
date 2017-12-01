package org.symphonyoss.symphony.bots.helpdesk.service.makerchecker.dao;

import org.symphonyoss.symphony.bots.helpdesk.service.model.Makerchecker;

/**
 * Created by alexandre-silva-daitan on 01/12/17.
 */
public interface MakercheckerDao {

  /**
   * Create a new makerchecker in the database.
   *
   * @param makerchecker Makerchecker data
   * @return Makerchecker 
   */
  Makerchecker createMakerchecker(Makerchecker makerchecker);

  /**
   * Update a makerchecker in the database.
   *
   * @param makerchecker Makerchecker data
   * @param id Makechecker id
   * @return Updated makerchecker
   */
  Makerchecker updateMackerchecker(Long id, Makerchecker makerchecker);

  /**
   * Gets a makerchecker from the database.
   *
   * @param id Makerchecker Id
   * @return Makerchecker
   */
  Makerchecker getMakerchecker(Long id);
}
