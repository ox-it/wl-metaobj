/*******************************************************************************
 * $URL$
 * $Id$
 * **********************************************************************************
 *
 *  Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
 *
 *  Licensed under the Educational Community License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ecl1.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/
package org.sakaiproject.metaobj.shared.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.shared.model.StructuredArtifact;
import org.sakaiproject.metaobj.shared.model.ElementBean;
import org.sakaiproject.metaobj.shared.model.ElementListBean;
import org.sakaiproject.metaobj.shared.mgt.HomeFactory;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;

import java.util.*;

public class XmlControllerBase {
   protected final Log logger = LogFactory.getLog(getClass());
   private HomeFactory homeFactory;
   private XmlValidator validator = null;
   private static final String FILE_ATTACHMENTS_FIELD =
      "org.sakaiproject.metaobj.shared.control.XmlControllerBase.field";

   protected ModelAndView handleNonSubmit(ElementBean bean, Map request,
                                          Map session, Map application, Errors errors) {
      EditedArtifactStorage sessionBean = (EditedArtifactStorage)session.get(
         EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY);

      session.put(EditedArtifactStorage.STORED_ARTIFACT_FLAG,
         "true");

      if (sessionBean == null) {
         StructuredArtifact artifact = (StructuredArtifact)bean;
         sessionBean = new EditedArtifactStorage(artifact.getCurrentSchema(),
            artifact);
         session.put(EditedArtifactStorage.EDITED_ARTIFACT_STORAGE_SESSION_KEY,
            sessionBean);
      }

      if ((request.get("editButton") != null &&
         request.get("editButton").toString().length() > 0)||
         request.get("addButton") != null) {
         handleEditAdd(bean, sessionBean, request, session, application, errors);
      }
      else if (request.get("removeButton") != null &&
         request.get("removeButton").toString().length() > 0) {
         handleRemove(bean, sessionBean, request, session, application, errors);
      }
      else if (request.get("cancelNestedButton") != null) {
         sessionBean.popCurrentElement(true);
         sessionBean.popCurrentPath();
      }
      else if (request.get("updateNestedButton") != null) {
         getValidator().validate(sessionBean.getCurrentElement(), errors, true);
         if (errors.hasErrors()) {
            return null;
         }
         sessionBean.popCurrentElement();
         sessionBean.popCurrentPath();
      }
      else if (request.get("fileHelper") != null) {
         return processFileAttachments(request, session, bean, errors);
      }

      Map map = new Hashtable();

      map.put(EditedArtifactStorage.STORED_ARTIFACT_FLAG,
         "true");

      map.put("artifactType", getSchemaName(session));
      if (request.get("parentId") != null) {
         map.put("parentId", getParentId(request));
      }
      return new ModelAndView("subList", map);
   }

   protected void handleRemove(ElementBean bean, EditedArtifactStorage sessionBean, Map request,
                               Map session, Map application, Errors errors) {
      ElementListBean parentList = findList(bean, (String)request.get("childPath"));
      int removeIndex = Integer.parseInt((String)request.get("childIndex"));
      parentList.remove(removeIndex);
   }

   protected void handleEditAdd(ElementBean bean, EditedArtifactStorage sessionBean, Map request,
                                Map session, Map application, Errors errors) {
      // find the individual element in question
      ElementListBean parentList = findList(bean, (String)request.get("childPath"));
      ElementBean newBean = null;

      if (request.get("editButton") != null &&
         request.get("editButton").toString().length() > 0) {
         int index = Integer.parseInt((String)request.get("childIndex"));
         newBean = (ElementBean)parentList.get(index);
      }
      else if (request.get("addButton") != null) {
         newBean = parentList.createBlank();
      }

      sessionBean.pushCurrentElement(newBean);
      sessionBean.pushCurrentPath((String)request.get("childPath"));

      if (request.get("addButton") != null) {
         parentList.add(newBean);
      }

   }

   protected ElementListBean findList(ElementBean bean, String path) {
      StringTokenizer tok = new StringTokenizer(path, ".");
      ElementBean current = bean;

      while (tok.hasMoreTokens()) {
         Object obj = current.get(tok.nextToken());
         if (obj instanceof ElementBean) {
            current = (ElementBean)obj;
         }
         else if (obj instanceof ElementListBean) {
            return (ElementListBean)obj;
         }
      }

      return null;
   }

   protected String getSchemaName(Map session) {
      Object schemaName = session.get(ResourceEditingHelper.CREATE_SUB_TYPE);

      if (schemaName == null) {
         return null;
      }
      if (schemaName instanceof String) {
         return (String)schemaName;
      }
      else if (schemaName instanceof String[]) {
         return ((String[])schemaName)[0];
      }
      else {
         return schemaName.toString();
      }
   }

   protected String getParentId(Map request) {
      Object parentId = request.get("parentId");

      if (parentId instanceof String) {
         return (String)parentId;
      }
      else if (parentId instanceof String[]) {
         return ((String[])parentId)[0];
      }
      else {
         return parentId.toString();
      }
   }

   public HomeFactory getHomeFactory() {
      return homeFactory;
   }

   public void setHomeFactory(HomeFactory homeFactory) {
      this.homeFactory = homeFactory;
   }

   public XmlValidator getValidator() {
      return validator;
   }

   public void setValidator(XmlValidator validator) {
      this.validator = validator;
   }

   public boolean isCancel(Map request) {
      return request.get("cancelNestedButton") != null;
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      return handleNonSubmit((ElementBean)command, request, session, application, errors);
   }

   public ModelAndView processFileAttachments(Map request, Map session, ElementBean currentBean, Errors errors) {
      String fieldName = (String) request.get("childPath");
      session.put(FILE_ATTACHMENTS_FIELD, fieldName);

      //ToolSession toolSession = getSessionManager().getCurrentToolSession();
      List attachmentRefs = new ArrayList();

      session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS,
         FilePickerHelper.CARDINALITY_SINGLE);
      if (List.class.isAssignableFrom(currentBean.getType(fieldName))) {
         List currentIds = (List) currentBean.get(fieldName);
         attachmentRefs.addAll(convertToRefList(currentIds));
         session.put(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS,
            FilePickerHelper.CARDINALITY_MULTIPLE);
      }
      else if (currentBean.get(fieldName) != null) {
         attachmentRefs.add(convertToRef(currentBean.get(fieldName).toString()));
      }

      session.put(FilePickerHelper.FILE_PICKER_ATTACHMENTS, attachmentRefs);

      return new ModelAndView("fileHelper");
   }

   public void retrieveFileAttachments(Map request, Map session, ElementBean currentBean) {
      String fieldName = (String) session.get(FILE_ATTACHMENTS_FIELD);

      if (session.get(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
            session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) {

         List refs = (List) session.get(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
         List ids = convertRefs(refs);
         // we may convert later, for now, leave backward compatible.
         // convertToGuidList(refs);

         if (List.class.isAssignableFrom(currentBean.getType(fieldName))) {
            List refList = (List) currentBean.get(fieldName);
            refList.clear();
            refList.addAll(ids);
         }
         else {
            if (refs.size() > 0) {
               currentBean.put(fieldName, ids.get(0));
            }
            else {
               currentBean.put(fieldName, "");
            }
         }
      }

      session.remove(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      session.remove(FilePickerHelper.FILE_PICKER_CANCEL);
   }

   protected List convertRefs(List refs) {
      List ret = new ArrayList();
      for (Iterator<Reference> i=refs.iterator();i.hasNext();) {
         ret.add(convertRef(i.next()));
      }
      return ret;
   }

   protected String convertRef(Reference reference) {
      return reference.getId();
   }

   /**
    * not currently used... will use if we move to storing id rather than ref in the
    * xml form data
    * @param refs
    * @return list of converted guids
    */
   protected List convertToGuidList(List refs) {
      List idList = new ArrayList();

      for (Iterator i=refs.iterator();i.hasNext();) {
         Reference ref = (Reference) i.next();

         idList.add(ContentHostingService.getUuid(ref.getId()));
      }

      return idList;
   }

   /**
    * @param id string
    * @return ref
    */
   protected Reference convertToRef(String id) {
      return EntityManager.newReference(ContentHostingService.getReference(id));
   }

   /**
    * @param refs
    * @return list of refs
    */
   protected List convertToRefList(List refs) {
      List refList = new ArrayList();

      for (Iterator<ElementBean> i=refs.iterator();i.hasNext();) {
         refList.add(convertToRef(i.next().getBaseElement().getTextTrim()));
      }

      return refList;
   }

}