/*
 * Copyright 2016 Esri, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri.geoportal.harvester.rest;

import static com.esri.geoportal.commons.utils.CrlfUtils.formatForLog;
import com.esri.geoportal.harvester.api.defs.UITemplate;
import com.esri.geoportal.harvester.engine.services.Engine;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * Connector information controller.
 * <p>
 * It provides a way to obtain information about available connectors (inbound, 
 * outbound). There is a separate REST endpoint for inbound and outbound connector.
 * <pre><code>
 
   GET /rest/harvester/connectors/inbound         - lists all inbound connectors
   GET /rest/harvester/connectors/outbound        - lists all outbound connectors
   GET /rest/harvester/connectors/inbound/{id}    - gets inbound connector by id
   GET /rest/harvester/connectors/outbound/{id}   - gets outbound connector by id
 * </code></pre>
 * Each endpoint returns a JSON array of templates, where each template is a
 * blueprint how to build UI for each adaptor, for example:
 * <pre><code>
   [ 
      {
        name: "WAF",
        label: "Web accessible folder",
        arguments: [
          {
            name: "waf-host-url",
            label: "URL",
            type: "string"
          },
          {
            name: "cred-username",
            label: "User name",
            type: "string"
          },
          {
            name: "cred-password",
            label: "User password",
            password: true,
            type: "string"
          }
        ]
      }, 
      {
        name: "CSW",
        label: "Catalogue service for the web",
        arguments: [
          {
            name: "csw-host-url",
            label: "URL",
            type: "string"
          },
          {
          name: "csw-profile-id",
          label: "Profile",
          choices: [
            {
              name: "urn:ogc:CSW:2.0.1:HTTP:OGCCORE:GeoNetwork",
              value: "GeoNetwork CSW 2.0.1 OGCCORE"
            },
            {
              name: "urn:ogc:CSW:2.0.2:HTTP:APISO:PYCSW",
              value: "pycsw"
            },
            {
              name: "urn:ogc:CSW:2.0.2:HTTP:OGCCORE:ESRI:GPT",
              value: "ArcGIS Server Geoportal Extension (GPT)"
            },
            {
              name: "urn:ogc:CSW:2.0.2:HTTP:APISO:GeoNetwork",
              value: "GeoNetwork CSW 2.0.2 APISO"
            },
            {
              name: "urn:ogc:CSW:2.0.2:HTTP:OGCISO:ESRI:GPT",
              value: "ArcGIS Server Geoportal Extension (version 10) CSW ISO AP"
            },
          ],
          default: "urn:ogc:CSW:2.0.2:HTTP:OGCCORE:ESRI:GPT",
          type: "choice"
          },
          {
            name: "cred-username",
            label: "User name",
            type: "string"
          },
          {
            name: "cred-password",
            label: "User password",
            password: true,
            type: "string"
          }
        ]
        } 
   ]
   
   or
   
   [ 
      {
        name: "GPT",
        label: "Geoportal Server New Generation",
        arguments: [
          {
            name: "gpt-host-url",
            label: "URL",
            type: "string"
          },
          {
            name: "cred-username",
            label: "User name",
            type: "string"
          },
          {
            name: "cred-password",
            label: "User password",
            password: true,
            type: "string"
          }
        ]
      }, 
      {
        name: "FOLDER",
        label: "Folder",
        arguments: [
          {
            name: "folder-root-folder",
            label: "Root folder",
            type: "string"
          }
        ]
      } 
   ]
 * </code></pre>
 */
@RestController
public class ConnectorController {
  private static final Logger LOG = LoggerFactory.getLogger(ConnectorController.class);
  
  @Autowired
  private Engine engine;
  
  /**
   * Lists all inbound connectors. A connector might be: WAF, CSW, etc.
   * @return array of connector templates
   */
  @RequestMapping(value = "/rest/harvester/connectors/inbound", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public UITemplate[] listInboundConnectors() {
    LOG.debug(String.format("GET /rest/harvester/connectors/inbound"));
    return engine.getTemplatesService().getInboundConnectorTemplates(LocaleContextHolder.getLocale()).toArray(new UITemplate[0]);
  }
  
  /**
   * Lists all outbound connectors. A connector might be: GPT, FOLDER, etc.
   * @return array of connector templates
   */
  @RequestMapping(value = "/rest/harvester/connectors/outbound", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public UITemplate[] listOutboundConnectors() {
    LOG.debug(String.format("GET /rest/harvester/connectors/outbound"));
    return engine.getTemplatesService().getOutboundConnectorTemplates(LocaleContextHolder.getLocale()).toArray(new UITemplate[0]);
  }
  
  /**
   * Get single inbound connector.
   * @param id id of the connector
   * @return connector template
   */
  @RequestMapping(value = "/rest/harvester/connectors/inbound/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public UITemplate getInboundConnector(@PathVariable String id) {
    LOG.debug(formatForLog("GET /rest/harvester/connectors/inbound/%s", id));
    return engine.getTemplatesService().getInboundConnectorTemplates(LocaleContextHolder.getLocale()).stream().filter(a->a.getType().equals(id)).findFirst().orElse(null);
  }
  
  /**
   * Get single outbound connector.
   * @param id id of the connector
   * @return connector template
   */
  @RequestMapping(value = "/rest/harvester/connectors/outbound/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public UITemplate getOutboundConnector(@PathVariable String id) {
    LOG.debug(formatForLog("GET /rest/harvester/connectors/outbound/%s", id));
    return engine.getTemplatesService().getOutboundConnectorTemplates(LocaleContextHolder.getLocale()).stream().filter(a->a.getType().equals(id)).findFirst().orElse(null);
  }
}
