<?xml version="1.0" encoding="utf-8"?>
<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ $URL: https://source.sakaiproject.org/svn/metaobj/trunk/metaobj-util/tool-lib/src/java/org/sakaiproject/metaobj/shared/control/AbstractStructuredArtifactDefinitionController.java $
  ~ $Id: AbstractStructuredArtifactDefinitionController.java 14230 2006-09-05 18:02:51Z chmaurer@iupui.edu $
  ~ **********************************************************************************
  ~
  ~  Copyright (c) 2004, 2005, 2006 The Sakai Foundation.
  ~
  ~  Licensed under the Educational Community License, Version 1.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~       http://www.opensource.org/licenses/ecl1.php
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  ~
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->

<xsl:stylesheet version="2.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xhtml="http://www.w3.org/1999/xhtml"
   xmlns:osp="http://www.osportfolio.org/OspML"
   xmlns:xs="http://www.w3.org/2001/XMLSchema">

   <!--xsl:template match="formView">
      <formView>
            <xsl:copy-of select="*"></xsl:copy-of>
      </formView>
   </xsl:template-->

   <xsl:template match="formView">


      <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
         <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
            <meta http-equiv="Content-Style-Type" content="text/css"/>
            <title>Resources</title>
            <xsl:for-each select="css/uri">
               <link type="text/css" rel="stylesheet" media="all">
                  <xsl:attribute name="href">
                     <xsl:value-of select="."/>
                  </xsl:attribute>
               </link>
            </xsl:for-each>
         </head>
         <body>
            <div class="portletBody">
               <h3>
                  <xsl:value-of select="formData/artifact/metaData/displayName"/>
               </h3>
               <p class="instruction">
                  <xsl:value-of disable-output-escaping="yes" select="formData/artifact/schema/instructions"/>
               </p>
               <xsl:apply-templates select="formData/artifact/schema/element">
                  <xsl:with-param name="currentParent" select="formData/artifact/structuredData"/>
                  <xsl:with-param name="rootNode" select="'true'"/>
               </xsl:apply-templates>
               <xsl:if test="returnUrl">
                  <a>
                     <xsl:attribute name="href">
                        <xsl:value-of select="returnUrl"/>
                     </xsl:attribute>
		Back
                  </a>
               </xsl:if>
            </div>
         </body>
      </html>
   </xsl:template>

   <!-- todo provide specail handling templates for
   certain element types -->

   <xsl:template match="element">
      <xsl:param name="currentParent"/>
      <xsl:param name="rootNode"/>
       <xsl:variable name="name" select="@name"/>
      <xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]"/>
      <xsl:choose>
         <xsl:when test="children">
            <xsl:if test="$rootNode = 'true'">
               <xsl:call-template name="produce-fields">
                  <xsl:with-param name="currentSchemaNode" select="."/>
                  <xsl:with-param name="currentNode" select="$currentNode"/>
                  <xsl:with-param name="rootNode" select="$rootNode"/>
               </xsl:call-template>
            </xsl:if>
            <xsl:if test="$rootNode='false'">
               <tr>
                  <td>
                     <xsl:call-template name="produce-label">
                        <xsl:with-param name="currentSchemaNode" select="."/>
                     </xsl:call-template>
                  </td>
                  <td>
                     <xsl:call-template name="produce-fields">
                        <xsl:with-param name="currentSchemaNode" select="."/>
                        <xsl:with-param name="currentNode" select="$currentNode"/>
                        <xsl:with-param name="rootNode" select="$rootNode"/>
                     </xsl:call-template>
                  </td>
               </tr>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <tr>
               <td>
                  <xsl:call-template name="produce-label">
                     <xsl:with-param name="currentSchemaNode" select="."/>
                  </xsl:call-template>
               </td>
               <td>
                   <xsl:choose>
                       <xsl:when test="./xs:annotation/xs:documentation[@source='ospi.isRichText']">
                           <xsl:value-of disable-output-escaping="yes" select="$currentNode"/>
                       </xsl:when>
                       <xsl:otherwise>
                           <xsl:value-of select="$currentNode"/>
                       </xsl:otherwise>
                   </xsl:choose>

               </td>
            </tr>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template name="produce-label">
      <xsl:param name="currentSchemaNode"/>
      <label>
         <xsl:choose>
            <xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
               <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']"/>
            </xsl:when>
            <xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
               <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:for-each select="$currentSchemaNode">
                  <xsl:value-of select="@name"/>
               </xsl:for-each>
            </xsl:otherwise>
         </xsl:choose>
      </label>
   </xsl:template>

   <xsl:template name="produce-fields">
      <xsl:param name="currentSchemaNode"/>
      <xsl:param name="currentNode"/>
      <xsl:param name="rootNode"/>
      <table class="itemSummary">
         <xsl:for-each select="$currentSchemaNode/children">
            <xsl:apply-templates select="@*|node()">
               <xsl:with-param name="currentParent" select="$currentNode"/>
               <xsl:with-param name="rootNode" select="'false'"/>
            </xsl:apply-templates>
         </xsl:for-each>
         <xsl:if test="$rootNode='true'">
            <xsl:call-template name="produce-metadata"/>
         </xsl:if>
      </table>
   </xsl:template>

   <xsl:template name="produce-metadata">
      <tr>
         <td>
            <label>Created</label>
         </td>
         <td>
            <xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/created"/>
         </td>
      </tr>
      <tr>
         <td>
            <label>Modified</label>
         </td>
         <td>
            <xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/modified"/>
         </td>
      </tr>
   </xsl:template>

</xsl:stylesheet>