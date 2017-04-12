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

import com.esri.geoportal.geoportal.commons.dcat.api.Catalog;
import com.esri.geoportal.geoportal.commons.dcat.api.Dataset;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;

/**
 * Catalog writer.
 */
public class CatalogWriter implements Closeable {
  private static final ObjectMapper mapper = new ObjectMapper();
  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
  private final JsonGenerator gen;
  
  public CatalogWriter(Writer writer) throws IOException {
    this.gen = new JsonFactory().createGenerator(writer);
  }

  public void writeCatalog(Catalog catalog) throws IOException {
    gen.writeStartObject();
    
    if (catalog.type!=null)
      gen.writeStringField("@type", catalog.type);
    
    if (catalog.context!=null)
      gen.writeStringField("@context", catalog.context);
    
    if (catalog.id!=null)
      gen.writeStringField("@id", catalog.id);
    
    if (catalog.conformsTo==null){
      throw new IOException("Error generating DCAT: no conformsTo");
    }
    
    gen.writeStringField("conformsTo", catalog.conformsTo);
    
    if (catalog.describedBy!=null)
      gen.writeStringField("describedBy", catalog.describedBy);
    
    gen.writeArrayFieldStart("dataset");
    
    if (catalog.dataset!=null) {
      for (Dataset ds: catalog.dataset) {
        ObjectNode treeNode = mapper.convertValue(ds, ObjectNode.class);
        mapper.writeTree(gen, treeNode);
      }
    }
    
    gen.writeEndArray();
    gen.writeEndObject();
  }
  
  @Override
  public void close() throws IOException {
    if (gen!=null) {
      gen.close();
    }
  }
}
