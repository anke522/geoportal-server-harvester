/*
 * Copyright 2017 Esri, Inc.
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
package com.esri.geoportal.geoportal.commons.dcat.factory;

import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ckan defaults.
 */
public class CkanDefaults extends AbstractMap<String,String> {
  private static final Logger LOG = LoggerFactory.getLogger(CkanDefaults.class);
  private static final Map<String,String> defaults = new HashMap<>();
  private static final CkanDefaults instance = new CkanDefaults();
  
  static {
    Properties props = new Properties();
    try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/default.properties")) {
      props.load(input);
      
      props.forEach((key,value)->defaults.put(key.toString(), value.toString()));
    } catch (Exception ex) {
      LOG.warn("Error loading dataset factory defaults.", ex);
    }
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    return defaults.entrySet();
  }
  
  public static CkanDefaults getDefault() {
    return instance;
  }
  
}
