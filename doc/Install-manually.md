This document describes how to install Kitodo Mediaserver.

## Prerequisites

* Linux operating system (tested on Ubuntu 16.04 and 18.04)
* OpenJDK JRE 8
* Apache Tomcat 8.5 - the application server
* GraphicsMagick (default) or ImageMagick - for file conversion
* Ghostscript - for PDF support

Mediaserver may run on other application servers supporting Java Servlet 3 too but this is untested and not supported. Same applies for Tomcat 7.0 and Tomcat 8.0.

* We assume Tomcat is installed to **/usr/local/tomcat**.

## Install Mediaserver

* We install the Mediaserver to **/usr/local/kitodo-mediaserver**.
* We install version **1.0**.
* The working data is stored in **/var/lib/kitodo-mediaserver**.

### Create installation directories

```bash
mkdir -p /usr/local/kitodo-mediaserver/config
cd /usr/local/kitodo-mediaserver
```

### Download Mediaserver

```bash
# The version to be downloaded
MS_VERSION=1.0

# Download Fileserver
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-fileserver-${MS_VERSION}.war -O kitodo-mediaserver-fileserver.war

# Download CLI
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-cli-${MS_VERSION}.jar -O kitodo-mediaserver-cli.jar

# Download web UI
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-ui-${MS_VERSION}.war -O kitodo-mediaserver-ui.war

# Download default configuration file
wget -q https://raw.githubusercontent.com/tuub/kitodo-mediaserver/${MS_VERSION}/kitodo-mediaserver-core/src/main/resources/config/local.yml -O config/local.yml;
```

### Link the Fileserver and UI to Tomcat

```bash
ln -s /usr/local/kitodo-mediaserver/kitodo-mediaserver-fileserver.war /usr/local/tomcat/webapps/
# UI is optionally
ln -s /usr/local/kitodo-mediaserver/kitodo-mediaserver-ui.war /usr/local/tomcat/webapps/
```

### Create working directories

```bash
mkdir -p /var/lib/kitodo-mediaserver
mkdir /var/lib/kitodo-mediaserver/hotfolder
mkdir /var/lib/kitodo-mediaserver/importing
mkdir /var/lib/kitodo-mediaserver/import_error
```

The cache directory is only needed if you want to use derivatives caching:

```bash
mkdir /var/lib/kitodo-mediaserver/cache
```

## Configure the Mediaserver

In */usr/local/kitodo-mediaserver/config/* you will find the configuration file *local.yml*. This file carries all your Mediaserver configuration. Have a look at the [Configuration file](Configuration-file.md) reference to find out more about all settings.

### Database connection

You need to set up the database connection there (look at the [Configuration file](Configuration-file.md) reference). This example is using a MySQL database:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost/mediaserver?autoReconnect=true&useSSL=false
    username: kitodo
    password: kitodo
```

### Data directories

You also have to set up your working directories. 

```yaml
importer:
  hotfolderPath: /var/lib/kitodo-mediaserver/hotfolder/
  importingFolderPath: /var/lib/kitodo-mediaserver/importing/
  errorFolderPath: /var/lib/kitodo-mediaserver/import_error/
```

### Fileserver URL

The fileserver needs to know the URL it is operating on. This URL needs to match the URLs in the METS/MODS files. You can set the root URL like this:

```yaml
fileserver:
  rootUrl: http://any.domain.name:8980/files/
```

Related to this URL is the path where the fileserver listens on image requests. I must contain the work ID (`{workId}`):

```yaml
fileserver:
  filePathPattern: /files/{workId}/**
```

### Derivatives caching

If you want to use derivative files caching, you need to activate it:

```yaml
fileserver:
  caching: true
  cachePath: /var/lib/kitodo-mediaserver/cache/
```

### Tomcat configuration

To tell Tomcat to use your own external configuration file, we need to set a parameter in `CATALINA_OPTS` or `JAVA_OPTS` environment variable. Create or edit */usr/local/tomcat/bin/setenv.sh* and add:

```bash
export CATALINA_OPTS="$CATALINA_OPTS -Dspring.config.location=classpath:/config/,/usr/local/kitodo-mediaserver/config/"
```

On Ubuntu 16.04 you may set these variables in */etc/default/tomcat8*.

**Attention**: This setting will affect all apps deployed on this Tomcat instance. If you have other apps running, you should use a special context XML file for the Mediaserver which is a bit more complex.

### Setting config path for CLI

The JAVA_OPTS needs to be set for the CLI too. You may create a wrapper script for the CLI.
File */usr/local/bin/kitodo-mediaserver-cli*:

```bash
#!/usr/bin/env bash

# Command line wrapper for Kitodo.Mediaserver CLI
JAVA_OPTS="$JAVA_OPTS -Dspring.config.location=classpath:/config/,/usr/local/kitodo-mediaserver/config/"
java $JAVA_OPTS -jar /usr/local/kitodo-mediaserver/kitodo-mediaserver-cli.jar "$@"

exit $?
```

## Initialize database

Use the CLI to create the database schema:
```bash
java -jar kitodo-mediaserver-cli.jar updatedb
```
Note: This works without JAVA_OPTS variable if config directory is in the working directory.

## Run the Mediaserver

The Fileserver and the UI are ready to run but you'll need some work data to be served. Use the CLI to import your Kitodo.Production works to the Mediaserver. Have a look at [CLI usage guide](CLI-usage-guide.md) to learn how to import works.

To start the fileserver and the UI you have to start Tomcat. With the default Tomcat setup the fileserver should be reachable under http://<span>a</span>ny.domain.name/kitodo-mediaserver-fileserver/ and the UI should be under http://<span>a</span>ny.domain.name/kitodo-mediaserver-ui/. Have a look at [Web UI usage guide](Web-UI-usage-guide.md) to learn how to use the web UI.

To test the fileserver you can open an URL from one of the METS/MODS files of imported works. For example search for `<mets:file ID="FILE_0001_THUMBS"` in your METS/MODS file and open the URL under `xlink:href="..."` in your web browser. If an image is shown and the image size is correct, it worked.
