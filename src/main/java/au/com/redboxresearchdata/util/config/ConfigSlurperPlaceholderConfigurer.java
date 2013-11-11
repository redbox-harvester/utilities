package au.com.redboxresearchdata.util.config;
/*
 * Copyright 2009 the original author or authors.
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

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Subclass of PropertyPlaceholderConfigurer that resolves placeholders from
 * a properties object loaded using a Groovy's ConfigSlurper.
 *
 * ConfigSlurperPlaceholderConfigurer also supports the concept of per environment
 * configuration via "environment" and "defaultEnvironment" properties.
 * The "environment" property will be typically set using a system property.
 *
 * Example XML context definition:
 *
 * <pre class="code">&lt;bean class="org.springframework.beans.factory.config.ConfigSlurperPlaceholderConfigurer"&gt;
 *     &lt;property name="environment" value="#{systemProperties['runtime.environment']}" /&gt;
 *     &lt;property name="defaultEnvironment" value="production" /&gt;
 *     &lt;property name="location" value="/WEB-INF/config.groovy" /&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"&gt;
 *     &lt;property name="driverClassName"&gt;&lt;value&gt;${dataSource.driverClassName}&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="url"&gt;&lt;value&gt;${dataSource.url}&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="username"&gt;&lt;value&gt;${dataSource.username}&lt;/value&gt;&lt;/property&gt;
 *     &lt;property name="password"&gt;&lt;value&gt;${dataSource.password}&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * Example config.groovy:
 *
 * <pre class="code">dataSource {
 *     driverClassName = "org.hsqldb.jdbcDriver"
 *     username = "sa"
 *     password = ""
 * }
 * environments {
 *     development {
 *         dataSource {
 *             url = "jdbc:hsqldb:mem:devDB"
 *         }
 *     }
 *     test {
 *         dataSource {
 *             url = "jdbc:hsqldb:mem:testDb"
 *         }
 *     }
 *     production {
 *         dataSource {
 *             url = "jdbc:hsqldb:file:prodDb;shutdown=true"
 *             password = "secret"
 *         }
 *     }
 * }</pre>
 *
 * @author Marcel Overdijk
 * @author Tareq Abedrabbo
 * @see #setEnvironment
 * @see #setDefaultEnvironment
 * @see #setLocations
 * @see #setProperties
 * @see #setSystemPropertiesModeName
 * @see groovy.util.ConfigObject
 * @see groovy.util.ConfigSlurper
 */
public class ConfigSlurperPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private String environment;
    private String defaultEnvironment;
    private Resource[] locations;
    private ConfigObject config;
    @SuppressWarnings("unchecked") private Map flatConfig;

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public void setLocation(FileSystemResource location) {
        this.locations = new Resource[] { location };
    }

    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }

    /**
     * Load properties into the given instance.
     * @param props the Properties instance to load into
     * @throws java.io.IOException in case of I/O errors
     * @see #setLocations
     */
    @Override
    protected void loadProperties(Properties props) throws IOException {
        config = new ConfigObject();
        ConfigSlurper configSlurper = new ConfigSlurper(getEnvironment());
        for (Resource location : locations) {
            config.merge(configSlurper.parse(location.getURL()));
        }
        flatConfig = config.flatten();
        props.putAll(config.toProperties());
    }

    private String getEnvironment() {
        if (this.environment == null || this.environment.trim().length() == 0) {
            return this.defaultEnvironment;
        } else {
            return this.environment;
        }
    }

    /**
     * Returns the ConfigObject.
     *
     * @return The ConfigObject
     */
    public ConfigObject getConfig() {
        return this.config;
    }

    /**
     * Returns the ConfigObject as a flattened map for easy access from Java in a properties file like way.
     *
     * @return The flattened ConfigObject
     */
    @SuppressWarnings("unchecked")
    public Map getFlatConfig() {
        return this.flatConfig;
    }
}