/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.account.webui.component.model;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.component.model.SelectItemOption;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 11, 2007  
 */
public class UIPortalTemplateConfigOption extends SelectItemOption<String> {
private List<String> accessGroup_ ;  
  
  @SuppressWarnings("unused")
  public UIPortalTemplateConfigOption(String label, String value, String desc, String icon) throws Exception {
    super(label, value, desc, icon);
    accessGroup_ = new ArrayList<String>() ;
  }
  
  public List<String> getGroups() { return accessGroup_ ; }  
  
  public UIPortalTemplateConfigOption addGroup(String accessGroup) {
    accessGroup_.add(accessGroup) ;
    return this ;
  }
}
