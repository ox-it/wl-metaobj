package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.utils.mvc.intf.Controller;
import org.sakaiproject.metaobj.utils.mvc.intf.FormController;
import org.sakaiproject.metaobj.utils.mvc.intf.CancelableController;
import org.sakaiproject.metaobj.shared.mgt.StructuredArtifactDefinitionManager;
import org.sakaiproject.content.api.ResourceEditingHelper;
import org.sakaiproject.content.api.ResourceToolActionPipe;
import org.sakaiproject.content.api.ResourceToolAction;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: johnellis
 * Date: Jan 29, 2007
 * Time: 11:07:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormCreateResourceHelper implements Controller, FormController, CancelableController {

   private StructuredArtifactDefinitionManager structuredArtifactDefinitionManager;

   public ModelAndView handleRequest(Object requestModel,
                                     Map request, Map session, Map application, Errors errors) {
      FormCreateHelperBean bean = (FormCreateHelperBean) requestModel;
      session.put(ResourceEditingHelper.CREATE_SUB_TYPE, bean.getFormId());

      return new ModelAndView("formHelper");
   }

   public StructuredArtifactDefinitionManager getStructuredArtifactDefinitionManager() {
      return structuredArtifactDefinitionManager;
   }

   public void setStructuredArtifactDefinitionManager(StructuredArtifactDefinitionManager structuredArtifactDefinitionManager) {
      this.structuredArtifactDefinitionManager = structuredArtifactDefinitionManager;
   }

   /**
    * Create a map of all data the form requries.
    * Useful for building up drop down lists, etc.
    *
    * @param request
    * @param command
    * @param errors
    * @return
    */
   public Map referenceData(Map request, Object command, Errors errors) {
      Map ref = new Hashtable();

      ref.put("formList", getStructuredArtifactDefinitionManager().findHomes(false));

      return ref;
   }

   public boolean isCancel(Map request) {
      Object cancel = request.get("canceling");
      if (cancel == null) {
         return false;
      }
      return cancel.equals("true");
   }

   public ModelAndView processCancel(Map request, Map session, Map application,
                                     Object command, Errors errors) throws Exception {
      ResourceToolActionPipe pipe = (ResourceToolActionPipe)session.get(ResourceToolAction.ACTION_PIPE);
      pipe.setActionCanceled(true);
      pipe.setActionCompleted(false);
      return new ModelAndView("cancel");
   }

}