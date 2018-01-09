/*
 * Copyright 2016 Esri, Inc.
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
package com.esri.geoportal.harvester.ckan;

import static com.esri.geoportal.harvester.ckan.CkanConstants.*;
import com.esri.geoportal.commons.robots.BotsConfig;
import com.esri.geoportal.harvester.api.base.BotsBrokerDefinitionAdaptor;
import com.esri.geoportal.harvester.api.base.BrokerDefinitionAdaptor;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.InvalidDefinitionException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * CKAN broker definition adaptor.
 */
public class CkanBrokerDefinitionAdaptor extends BrokerDefinitionAdaptor {
  
  private final BotsBrokerDefinitionAdaptor botsAdaptor;
  private URL hostUrl;
  private String apiKey;
  private boolean emitXml = true;
  private boolean emitJson = false;


  /**
   * Creates instance of the adaptor.
   * @param def broker definition
   * @throws InvalidDefinitionException if definition is invalid
   * @throws IllegalArgumentException if invalid broker definition
   */
  public CkanBrokerDefinitionAdaptor(EntityDefinition def) throws InvalidDefinitionException {
    super(def);
    this.botsAdaptor = new BotsBrokerDefinitionAdaptor(def);
    if (StringUtils.trimToEmpty(def.getType()).isEmpty()) {
      def.setType(CkanConnector.TYPE);
    } else if (!getType().equals(def.getType())) {
      throw new InvalidDefinitionException("Broker definition doesn't match");
    } else {
      initialize(def);
    }
  }

  protected String getType() {
    return CkanConnector.TYPE;
  }
  
  /**
   * Initializes adaptor from definition.
   * @param def broker definition
   * @throws InvalidDefinitionException if definition is invalid
   */
  protected void initialize(EntityDefinition def) throws InvalidDefinitionException {
    apiKey = get(P_API_KEY);
    emitXml = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(get(P_EMIT_XML)), true);
    emitJson = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(get(P_EMIT_JSON)), false);
    
    try {
      hostUrl = new URL(get(P_HOST_URL));
    } catch (MalformedURLException ex) {
      throw new InvalidDefinitionException(String.format("Invalid %s: %s", P_HOST_URL,get(P_HOST_URL)), ex);
    }
  }

  @Override
  public void override(Map<String, String> params) {
    consume(params,P_HOST_URL);
    consume(params,P_API_KEY);
    consume(params,P_EMIT_XML);
    consume(params,P_EMIT_JSON);
    botsAdaptor.override(params);
  }
  
  public URL getHostUrl() {
    return hostUrl;
  }

  public void setHostUrl(URL hostUrl) {
    this.hostUrl = hostUrl;
    set(P_HOST_URL,hostUrl.toExternalForm());
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
    set(P_API_KEY,apiKey);
  }

  /**
   * Gets bots config.
   * @return bots config
   */
  public BotsConfig getBotsConfig() {
    return botsAdaptor.getBotsConfig();
  }

  /**
   * Sets bots config.
   * @param botsConfig bots config 
   */
  public void setBotsConfig(BotsConfig botsConfig) {
    botsAdaptor.setBotsConfig(botsConfig);
  }

  public boolean getEmitXml() {
    return emitXml;
  }

  public void setEmitXml(boolean emitXml) {
    this.emitXml = emitXml;
    set(P_EMIT_XML, BooleanUtils.toStringTrueFalse(emitXml));
  }

  public boolean getEmitJson() {
    return emitJson;
  }

  public void setEmitJson(boolean emitJson) {
    this.emitJson = emitJson;
    set(P_EMIT_JSON, BooleanUtils.toStringTrueFalse(emitJson));
  }
  
}
