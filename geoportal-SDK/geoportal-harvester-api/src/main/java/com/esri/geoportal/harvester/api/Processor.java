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
package com.esri.geoportal.harvester.api;

import com.esri.geoportal.harvester.api.ex.DataException;
import com.esri.geoportal.harvester.api.specs.InputBroker;
import com.esri.geoportal.harvester.api.specs.OutputBroker;
import java.util.List;

/**
 * Processor.
 */
@FunctionalInterface
public interface Processor {

  /**
   * Creates process.
   *
   * @param source data source
   * @param destinations data destination
   * @return instance of the process
   */
  Process createProcess(InputBroker source, List<OutputBroker> destinations);

  /**
   * Process
   */
  public interface Process {

    /**
     * Gets process title.
     *
     * @return process title
     */
    String getTitle();

    /**
     * Begins the process.
     */
    void begin();

    /**
     * Aborts the process.
     */
    void abort();

    /**
     * Gets process status.
     *
     * @return process status
     */
    Status getStatus();

    /**
     * Adds listened.
     *
     * @param listener listener
     */
    void addListener(Listener listener);
  }

  /**
   * Process status.
   */
  enum Status {
    /**
     * just submitted
     */
    submitted,
    /**
     * currently being executing
     */
    working,
    /**
     * aborting
     */
    aborting,
    /**
     * completed (with or without errors or aborted)
     */
    completed
  }

  /**
   * Process listener.
   */
  interface Listener {

    /**
     * Called when status changed
     *
     * @param status new status
     */
    void onStatusChange(Status status);

    /**
     * Called when data reference has been processed
     *
     * @param dataReference data reference
     */
    void onDataProcessed(DataReference dataReference);

    /**
     * Called for output onError.
     *
     * @param ex onError
     */
    public void onError(DataException ex);
  }

}