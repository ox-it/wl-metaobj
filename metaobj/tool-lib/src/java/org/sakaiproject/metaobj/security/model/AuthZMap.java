/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/admin-tools/su/src/java/org/sakaiproject/tool/su/SuTool.java $
 * $Id: SuTool.java 6970 2006-03-23 23:25:04Z zach.thomas@txstate.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.metaobj.security.model;

import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.shared.model.Id;

import java.util.HashMap;

public class AuthZMap extends HashMap {
   private AuthorizationFacade authzFacade;
   private String prefix;
   private Id qualifier;

   public AuthZMap(AuthorizationFacade authzFacade, Id qualifier) {
      this.authzFacade = authzFacade;
      this.prefix = "";
      this.qualifier = qualifier;
   }

   public AuthZMap(AuthorizationFacade authzFacade, String prefix, Id qualifier) {
      this.authzFacade = authzFacade;
      this.prefix = prefix;
      this.qualifier = qualifier;
   }

   public Object get(Object key) {
      if (super.get(key) == null) {
         super.put(key, new Boolean(authzFacade.isAuthorized(prefix + key.toString(), qualifier)));
      }
      return super.get(key);
   }
}
