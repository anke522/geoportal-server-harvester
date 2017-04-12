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

/**
 * Temporal
 */
public class Temporal {
  private final IsoDate from;
  private final IsoDate to;

  public Temporal(IsoDate from, IsoDate to) {
    this.from = from;
    this.to = to;
  }
  
  @Override
  public String toString() {
    return String.format("%s/%s", from, to);
  }
}
