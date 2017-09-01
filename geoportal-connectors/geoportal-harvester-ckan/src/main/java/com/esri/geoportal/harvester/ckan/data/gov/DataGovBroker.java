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
package com.esri.geoportal.harvester.ckan.data.gov;

import com.esri.geoportal.commons.constants.ItemType;
import com.esri.geoportal.commons.constants.MimeType;
import com.esri.geoportal.commons.constants.MimeTypeUtils;
import com.esri.geoportal.commons.http.BotsHttpClient;
import com.esri.geoportal.commons.meta.ArrayAttribute;
import com.esri.geoportal.commons.meta.Attribute;
import com.esri.geoportal.commons.meta.MapAttribute;
import com.esri.geoportal.commons.meta.MetaBuilder;
import com.esri.geoportal.commons.meta.MetaException;
import com.esri.geoportal.commons.meta.StringAttribute;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_DESCRIPTION;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_IDENTIFIER;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_MODIFIED;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_REFERENCES;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_RESOURCE_URL;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_RESOURCE_URL_SCHEME;
import static com.esri.geoportal.commons.meta.util.WKAConstants.WKA_TITLE;
import com.esri.geoportal.commons.robots.Bots;
import com.esri.geoportal.commons.robots.BotsUtils;
import com.esri.geoportal.commons.ckan.client.Client;
import com.esri.geoportal.commons.ckan.client.Dataset;
import com.esri.geoportal.commons.ckan.client.Response;
import com.esri.geoportal.harvester.api.DataReference;
import com.esri.geoportal.harvester.api.base.SimpleDataReference;
import com.esri.geoportal.harvester.api.defs.EntityDefinition;
import com.esri.geoportal.harvester.api.ex.DataInputException;
import com.esri.geoportal.harvester.api.ex.DataProcessorException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.InputConnector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * CKAN broker.
 */
/*package*/ class DataGovBroker implements InputBroker {
  private static final Logger LOG = LoggerFactory.getLogger(DataGovBroker.class);
  
  private final DataGovConnector connector;
  private final DataGovBrokerDefinitionAdaptor definition;
  private final MetaBuilder metaBuilder;
  
  private CloseableHttpClient httpClient;
  private Client client;
 
  private static final ObjectMapper mapper = new ObjectMapper();
  static {
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
  
  /**
   * Creates instance of the broker.
   * @param connector connector
   * @param definition definition
   * @param metaBuilder meta builder
   */
  public DataGovBroker(DataGovConnector connector, DataGovBrokerDefinitionAdaptor definition, MetaBuilder metaBuilder) {
    this.connector = connector;
    this.definition = definition;
    this.metaBuilder = metaBuilder;
  }

  @Override
  public void initialize(InitContext context) throws DataProcessorException {
    definition.override(context.getParams());
    CloseableHttpClient http = HttpClientBuilder.create().build();
    if (context.getTask().getTaskDefinition().isIgnoreRobotsTxt()) {
      httpClient = http;
    } else {
      Bots bots = BotsUtils.readBots(definition.getBotsConfig(), http, definition.getHostUrl());
      httpClient = new BotsHttpClient(http,bots);
    }
    client = new Client(httpClient, definition.getHostUrl(), definition.getApiKey());
  }

  @Override
  public void terminate() {
    if (httpClient!=null) {
      try {
        httpClient.close();
      } catch (IOException ex) {
        LOG.error(String.format("Error terminating broker."), ex);
      }
    }
  }

  @Override
  public URI getBrokerUri() throws URISyntaxException {
    return new URI("CKAN",definition.getHostUrl().toExternalForm(),null);
  }

  @Override
  public Iterator iterator(IteratorContext iteratorContext) throws DataInputException {
    return new CkanIterator(iteratorContext);
  }

  @Override
  public String toString() {
    return String.format("CKAN [%s]", definition.getHostUrl());
  }

  @Override
  public InputConnector getConnector() {
    return connector;
  }

  @Override
  public EntityDefinition getEntityDefinition() {
    return definition.getEntityDefinition();
  }
  
  /**
   * CKAN iterator.
   */
  private class CkanIterator implements InputBroker.Iterator {
    private final IteratorContext iteratorContext;
    private final TransformerFactory tf = TransformerFactory.newInstance();
    
    private java.util.Iterator<Dataset> dataSetsIter;
    
    private final int limit = 10;
    private int offset = 0;

    public CkanIterator(IteratorContext iteratorContext) {
      this.iteratorContext = iteratorContext;
    }

    @Override
    public boolean hasNext() throws DataInputException {
      try {
        if (dataSetsIter!=null && dataSetsIter.hasNext()) {
          return true;
        }

        Response response = client.listPackages(limit, offset);
        offset+=limit;
        
        if (response!=null && response.result!=null && response.result.results!=null) {
          List<Dataset> results = response.result.results;
          if (results!=null && results.size()>0) {
            dataSetsIter = results.iterator();
            return hasNext();
          }
        }
        
        return false;
      } catch (IOException|URISyntaxException ex) {
        throw new DataInputException(DataGovBroker.this, String.format("Error reading data from: %s", this), ex);
      }
    }

    @Override
    public DataReference next() throws DataInputException {
      try {
        Dataset dataSet = dataSetsIter.next();
        
        HashMap<String,Attribute> attrs = new HashMap<>();
        String id = firstNonBlank(dataSet.id);
        attrs.put(WKA_IDENTIFIER, new StringAttribute(id));
        attrs.put(WKA_TITLE, new StringAttribute(firstNonBlank(dataSet.title,dataSet.name)));
        attrs.put(WKA_DESCRIPTION, new StringAttribute(firstNonBlank(dataSet.notes)));
        attrs.put(WKA_MODIFIED, new StringAttribute(dataSet.metadata_modified));
        
        if (dataSet.resources!=null) {
          final List<Attribute> references = new ArrayList<>();
          dataSet.resources.forEach(resource->{
            HashMap<String,Attribute> reference = new HashMap<>();
            if (resource.url!=null) {
              String scheme = generateSchemeName(resource.url);
              reference.put(WKA_RESOURCE_URL, new StringAttribute(resource.url));
              if (scheme!=null) {
                reference.put(WKA_RESOURCE_URL_SCHEME, new StringAttribute(scheme));
              }
            }
            references.add(new MapAttribute(reference));
          });
          if (references.size()==1) {
            Map<String, Attribute> namedAttributes = references.get(0).getNamedAttributes();
            attrs.put(WKA_RESOURCE_URL, namedAttributes.get(WKA_RESOURCE_URL));
            if (namedAttributes.containsKey(WKA_RESOURCE_URL_SCHEME)) {
              attrs.put(WKA_RESOURCE_URL_SCHEME, namedAttributes.get(WKA_RESOURCE_URL_SCHEME));
            }
          } else if (!references.isEmpty()) {
            attrs.put(WKA_REFERENCES, new ArrayAttribute(references));
          }
        }
        
        Document doc = metaBuilder.create(new MapAttribute(attrs));
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);

        SimpleDataReference ref = new SimpleDataReference(getBrokerUri(), definition.getEntityDefinition().getLabel(), id, parseIsoDate(dataSet.metadata_modified), URI.create(id));
        if (definition.getEmitXml()) {
          ref.addContext(MimeType.APPLICATION_XML, writer.toString().getBytes("UTF-8"));
        }
        if (definition.getEmitJson()) {
          ref.addContext(MimeType.APPLICATION_JSON, mapper.writeValueAsString(dataSet).getBytes("UTF-8"));
        }
        
        return ref;
        
      } catch (MetaException|TransformerException|URISyntaxException|UnsupportedEncodingException|IllegalArgumentException|JsonProcessingException ex) {
        throw new DataInputException(DataGovBroker.this, String.format("Error reading data from: %s", this), ex);
      }
    }
    
    private String firstNonBlank(String...strs) {
      return Arrays.asList(strs).stream().filter(s->!StringUtils.isBlank(s)).findFirst().orElse(null);
    }
    
  }
  
  private String generateSchemeName(String url) {
    String serviceType = url!=null? ItemType.matchPattern(url).stream()
            .filter(it->it.getServiceType()!=null)
            .map(ItemType::getServiceType)
            .findFirst().orElse(null): null;
    if (serviceType!=null) {
      return "urn:x-esri:specification:ServiceType:ArcGIS:"+serviceType;
    }
    if (url!=null) {
      int idx = url.lastIndexOf(".");
      if (idx>=0) {
        String ext = url.substring(idx+1);
        MimeType mimeType = MimeTypeUtils.mapExtension(ext);
        if (mimeType!=null) {
          return "urn:"+mimeType.getName();
        }
      }
    }
    return null;
  }

  /**
   * Parses ISO date
   *
   * @param strDate ISO date as string
   * @return date object or <code>null</code> if unable to parse date
   */
  private Date parseIsoDate(String strDate) {
    try {
      return Date.from(ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(strDate)).toInstant());
    } catch (Exception ex) {
      return null;
    }
  }
}
