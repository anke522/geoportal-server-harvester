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
package com.esri.geoportal.geoportal.commons.dcat.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Catalog top level placeholder.
 */
public class Catalog {
  @JsonProperty(value = "@context")
  public String context;
  
  @JsonProperty(value = "@id")
  public String id;
  
  @JsonProperty(value = "@type")
  public String type;
  
  @JsonProperty(required = true, defaultValue = "https://project-open-data.cio.gov/v1.1/schema")
  public String conformsTo;
  
  public String describedBy;
  
  @JsonProperty(required = true)
  public Dataset [] dataset;
}
