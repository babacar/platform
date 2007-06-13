/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
    events = {
      @EventConfig(listeners = UIListUsers.ViewUserInfoActionListener.class),
      @EventConfig(listeners = UIListUsers.DeleteUserActionListener.class, confirm = "UIListUsers.deleteUser")
    }
)
public class UIListUsers extends UISearch {
  
  private static String[] USER_BEAN_FIELD = {"userName", "lastName", "firstName", "email"} ;
  private static String[] USER_ACTION = {"ViewUserInfo", "DeleteUser"} ;  

  private static List<SelectItemOption<String>> OPTIONS_ = new ArrayList<SelectItemOption<String>>(4);
  static{
    OPTIONS_.add(new SelectItemOption<String>("Username", "userName"));
    OPTIONS_.add(new SelectItemOption<String>("First name", "firstName"));
    OPTIONS_.add(new SelectItemOption<String>("Last name", "lastName"));
    OPTIONS_.add(new SelectItemOption<String>("Email", "email"));
  }
  
  private Query lastQuery_ ;
  private String userSelected_;
  
	public UIListUsers() throws Exception {
		super(OPTIONS_) ;
    UIGrid uiGrid = addChild(UIGrid.class, null, "UIListUsers") ;
    uiGrid.configure("userName", USER_BEAN_FIELD, USER_ACTION) ;
		search(new Query()) ;
	}
  
  public void setUserSelected(String userName) {
    userSelected_ = userName;
  }

  public String getUserSelected() {
    
    return userSelected_;
  }
	public void search(Query query) throws Exception {
    lastQuery_ = query ;
    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    uiGrid.getUIPageIterator().setPageList(service.getUserHandler().findUsers(query)) ;
    UIPageIterator pageIterator = uiGrid.getUIPageIterator();
    if(pageIterator.getAvailable() == 0 ) {
      UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
      
      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
    }
	}
  
  public void quickSearch(UIFormInputSet quickSearchInput) throws Exception {
    Query query = new Query();
    UIFormStringInput input = (UIFormStringInput) quickSearchInput.getChild(0);
    UIFormSelectBox select = (UIFormSelectBox) quickSearchInput.getChild(1);
    String name = input.getValue();
    if(name == null || name.equals("")) {
      search(new Query()) ;
      return ;
    }
    String selectBoxValue = select.getValue();
    if(selectBoxValue.equals("userName")) query.setUserName(name) ;
    if(selectBoxValue.equals("firstName")) query.setFirstName(name) ; 
    if(selectBoxValue.equals("lastName")) query.setLastName(name) ;
    if(selectBoxValue.equals("email")) query.setEmail(name) ;
    search(query);
  }

  @SuppressWarnings("unused")
  public void advancedSearch(UIFormInputSet advancedSearchInput) throws Exception {
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.write("<div class=\"UIListUsers\">");
    renderChildren();
    w.write("</div>");
  }
	
	static  public class ViewUserInfoActionListener extends EventListener<UIListUsers> {
    public void execute(Event<UIListUsers> event) throws Exception {
    	String username = event.getRequestContext().getRequestParameter(OBJECTID) ;
      
    	UIListUsers uiListUsers = event.getSource();
      OrganizationService service = uiListUsers.getApplicationComponent(OrganizationService.class);
      if(service.getUserHandler().findUserByName(username) == null ) {
        uiListUsers.search(new Query()) ;
        return ;
      }
      
    	uiListUsers.setRendered(false);
    	UIUserManagement uiUserManager = uiListUsers.getParent();
    	UIUserInfo uiUserInfo = uiUserManager.getChild(UIUserInfo.class);
      uiUserInfo.setUser(username);
    	uiUserInfo.setRendered(true);
      
    	UIComponent uiToUpdateAjax = uiListUsers.getAncestorOfType(UIUserManagement.class) ;
    	event.getRequestContext().addUIComponentToUpdateByAjax(uiToUpdateAjax) ;
    }
  }
  
  static  public class DeleteUserActionListener extends EventListener<UIListUsers> {
    public void execute(Event<UIListUsers> event) throws Exception {
      UIListUsers uiListUser = event.getSource() ;
      String userName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      OrganizationService service = uiListUser.getApplicationComponent(OrganizationService.class) ;
      service.getUserHandler().removeUser(userName, true) ;
      uiListUser.search(uiListUser.lastQuery_);
      
      UIComponent uiToUpdateAjax = uiListUser.getAncestorOfType(UIUserManagement.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiToUpdateAjax) ;
    }
  }
}
