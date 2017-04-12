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

import com.esri.geoportal.commons.meta.MapAttribute;
import com.esri.geoportal.commons.meta.MetaAnalyzer;
import com.esri.geoportal.commons.meta.MetaException;
import com.esri.geoportal.commons.meta.util.MultiMetaAnalyzerWrapper;
import com.esri.geoportal.geoportal.commons.dcat.api.Dataset;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * Dataset factory.
 */
public class DatasetFactory {
  
  private final Map<String,String> defaults;
  private final MetaAnalyzer metaAnalyzer;

  public DatasetFactory(Map<String,String> defaults, MetaAnalyzer metaAnalyzer) {
    this.defaults = defaults;
    this.metaAnalyzer = metaAnalyzer;
  }

  public DatasetFactory() {
    this(CkanDefaults.getDefault(), MultiMetaAnalyzerWrapper.getDefault());
  }
  
  public Dataset createDataset(Document xml) throws MetaException {
    MapAttribute attributes = metaAnalyzer.extract(xml);
    Dataset dataset = new Dataset();
    return dataset;
  }
}
