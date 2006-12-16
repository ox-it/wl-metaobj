/**********************************************************************************
 * $URL$
 * $Id$
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

package org.sakaiproject.metaobj.utils.xml.impl;

import org.jdom.Element;
import org.jdom.Namespace;
import org.sakaiproject.metaobj.utils.xml.NormalizationException;
import org.sakaiproject.metaobj.utils.xml.SchemaNode;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 27, 2004
 * Time: 6:31:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class BooleanElementType extends BaseElementType {


   public BooleanElementType(String typeName, Element schemaElement,
                             SchemaNode parentNode, Namespace xsdNamespace) {
      super(typeName, schemaElement, parentNode, xsdNamespace);
   }

   public String getSchemaNormalizedValue(Object value) throws NormalizationException {
      //CWM check pattern?
      
      if (getEnumeration() != null && !getEnumeration().contains(value)) {
         //throw new NormalizationException("Invalid string pattern",
         //      "Value {0} must match {1}", new Object[]{value, getEnumeration()});
         throw new NormalizationException("Required field",
               NormalizationException.REQIRED_FIELD_ERROR_CODE, new Object[0]);
      }
      return value.toString();
   }

   public String getSchemaNormalizedValue(String value) throws NormalizationException {
      return new Boolean(value).toString();
   }

   public Class getObjectType() {
      return Boolean.class;
   }

   public Object getActualNormalizedValue(String value) {
      return new Boolean(value);
   }
}