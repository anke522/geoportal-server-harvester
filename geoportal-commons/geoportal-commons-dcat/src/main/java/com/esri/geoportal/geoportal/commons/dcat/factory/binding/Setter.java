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
package com.esri.geoportal.geoportal.commons.dcat.factory.binding;

import com.esri.geoportal.geoportal.commons.dcat.api.Dataset;

/**
 * Dataset setter.
 * @param <T> type of the value
 */
public interface Setter<T> {
  void set(Dataset dataset, T value) throws IllegalArgumentException, IllegalAccessException;
}
