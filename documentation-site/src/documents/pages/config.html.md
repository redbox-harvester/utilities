```
title: Config
layout: page
pageOrder: 1
```

Convenience class to load configuration files that are of [ConfigSlurper][ConfigSlurper] format.

##Purpose

This class is used to manage and load configuration in [ConfigSlurper][ConfigSlurper] format, and facilitate ease of upgrades and customisation.

See [sample implementation here][SampleConfig].

##Design

The key fields are the "file.runtimePath" and "file.customPath". When the base configuration is initially loaded, the "runtimePath" is checked and if it is exists it will be used as the configuration. If the "runtimePath" does not exist, the base configuration is used to seed it. If the "customPath" exists and is newer than the "runtimePath" or if the "runtimePath" is in the process of being created, then the "customPath" is merged, updating the "runtimePath" file. 
To achieve ease of upgrade, institutions must edit the file specified at "customPath". On upgrade, they can backup any data they want to the "customPath", then remove the "runtimePath" from the file system. When the upgrade version of the base configuration is loaded, the upgrade configuration is merged with the previous custom configuration. Of course, the custom configuration must not contain entries that match the newly added config entries on the upgrade version, or else the new entries will be overridden by the values at the custom version. There is no facility to prevent this from happening, so please do your homework!

Source is in [here][ConfigSrc].

[ConfigSlurper]: http://groovy.codehaus.org/ConfigSlurper "ConfigSlurper Documentation"
[SampleConfig]: https://raw.githubusercontent.com/redbox-harvester/redbox-dataset-jdbc-harvester-template/master/src/main/resources/deploy-manager/harvester-config.groovy "Sample Config file"
[ConfigSrc]: https://github.com/redbox-harvester/utilities/blob/master/src/main/groovy/au/com/redboxresearchdata/util/config/Config.groovy "Config class source"