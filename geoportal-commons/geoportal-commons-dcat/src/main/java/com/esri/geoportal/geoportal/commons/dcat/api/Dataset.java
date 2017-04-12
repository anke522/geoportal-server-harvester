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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dataset.
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Dataset {
  @JsonProperty(value = "@type")
  public String type;
  
  @JsonProperty(required = true)
  public AccessLevel accessLevel;
  
  @JsonProperty(required = true)
  public String title;
  
  @JsonProperty(required = true)
  public String description;
  
  @JsonProperty(required = true)
  public String [] keyword;
  
  @JsonProperty(required = true)
  public IsoDate modified;
  
  @JsonProperty(required = true)
  public Publisher publisher;
  
  @JsonProperty(required = true)
  public String identifier;
  
  @JsonProperty(required = true)
  public String [] bureauCode;
  
  public String license;
  
  public String rights;
  
  public String spatial;
  
  public IsoDuration accrualPeriodicity;
  
  @JsonProperty(required = true)
  public String [] programCode;
  
  public String conformsTo;
  
  public Boolean dataQuality;
  
  public String describedBy;
  
  public String describedByType;
  
  public String isPartOf;
  
  public IsoDate issued;
  
  public String language;
  
  public String landingPage;
  
  public String primaryITInvestmentUII;
  
  public String [] references;
  
  public String systemOfRecords;
  
  public String [] theme;
}
