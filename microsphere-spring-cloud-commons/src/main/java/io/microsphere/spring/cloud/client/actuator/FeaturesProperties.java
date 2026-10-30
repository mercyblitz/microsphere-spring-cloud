/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.spring.cloud.client.actuator;

import io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.PROPERTY_NAME_PREFIX;
import static java.util.Collections.emptyMap;

/**
 * The {@link ConfigurationProperties} for {@link FeaturesEndpoint}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationProperties
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = PROPERTY_NAME_PREFIX)
public class FeaturesProperties {

    private Map<String, List<String>> _abstract;

    private Map<String, Map<String, String>> named;

    /**
     * Sets the abstract features configuration where the key is the module name and the value
     * is the list of the feature class names.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Corresponding configuration property:
     * // microsphere.spring.cloud.features.abstract.jdbc=javax.sql.DataSource
     * featuresProperties.setAbstract(Map.of("jdbc", List.of("javax.sql.DataSource")));
     * }</pre>
     *
     * @param _abstract the abstract features configuration
     * @see FeaturesConstants#ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX
     */
    public void setAbstract(Map<String, List<String>> _abstract) {
        this._abstract = _abstract;
    }

    /**
     * Returns the abstract features configuration where the key is the module name and the value
     * is the list of the feature class names.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Map<String, List<String>> abstractFeatures = featuresProperties.getAbstract();
     * }</pre>
     *
     * @return the abstract features configuration, or an empty {@link Map} if not configured
     * @see FeaturesConstants#ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX
     */
    public Map<String, List<String>> getAbstract() {
        if (_abstract == null) {
            return emptyMap();
        }
        return _abstract;
    }

    /**
     * Sets the named features configuration where the outer key is the module name and the inner
     * map associates the feature name with the feature class name.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Corresponding configuration property:
     * // microsphere.spring.cloud.features.named.web.rest-template=org.springframework.web.client.RestTemplate
     * featuresProperties.setNamed(Map.of("web", Map.of("rest-template", "org.springframework.web.client.RestTemplate")));
     * }</pre>
     *
     * @param named the named features configuration
     * @see FeaturesConstants#NAMED_FEATURE_PROPERTY_NAME_PREFIX
     */
    public void setNamed(Map<String, Map<String, String>> named) {
        this.named = named;
    }

    /**
     * Returns the named features configuration where the outer key is the module name and the inner
     * map associates the feature name with the feature class name.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Map<String, Map<String, String>> namedFeatures = featuresProperties.getNamed();
     * }</pre>
     *
     * @return the named features configuration, or an empty {@link Map} if not configured
     * @see FeaturesConstants#NAMED_FEATURE_PROPERTY_NAME_PREFIX
     */
    public Map<String, Map<String, String>> getNamed() {
        if (named == null) {
            return emptyMap();
        }
        return named;
    }
}
