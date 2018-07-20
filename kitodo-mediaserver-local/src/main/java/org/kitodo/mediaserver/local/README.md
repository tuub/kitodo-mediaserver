# Local actions and configurations

## Action implementations

To add local action implementations to Kitodo.Mediaserver, please implement the interface `org.kitodo.mediaserver.core.api.IAction`
and annotate the implementation as a component with a unique name, e.g. with @Component("myUniqueAction").

The action will then be known to the system under this name and can be added i.e. to the import workflow or the ui using the configuration.

## Configurations

In the file local.yml, all configurations defined in default.yml can be overwritten. All local configurations should be defined
in this file.
