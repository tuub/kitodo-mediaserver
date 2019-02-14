# Install Kitodo.Mediaserver manually

This document describes how to install Kitodo Mediaserver.

## Prerequisites

* **Linux operating system** (tested on Ubuntu 16.04 and 18.04)
* **OpenJDK JRE 8** (OpenJDK 9 and newer will not work)
* **Apache Tomcat 8.5** - the application server

If you want to use the "IM" convert actions (see [actions](Actions.md) and [configuration](Configuration-file.md)) you have to install:

* **GraphicsMagick** (default) or ImageMagick. 
* **Ghostscript** - for PDF support

If you want to use the Java convert actions to generate PDF files, you'll need this: 

* A default **ICC profile** - The Java converter always creates PDF/A-1b files. Therefor a ICC profile is required. On Ubuntu you may install *icc-profiles-free* package

Mediaserver may run on other application servers supporting Java Servlet 3 too but this is untested and not supported. Tomcat 7.0 is known not to be working.

* We assume Tomcat is installed to **/var/lib/tomcat8**.

## Install Mediaserver

* We install the Mediaserver to **/usr/local/kitodo-mediaserver**.
* We install version **1.0**.
* The working data is stored in **/var/local/kitodo-mediaserver**.
* You need to be super user (root).

### Create installation directories

```bash
mkdir -p /usr/local/kitodo-mediaserver
```

### Download Mediaserver

```bash
# The version to be downloaded
MS_VERSION=1.0

cd /usr/local/kitodo-mediaserver

# Download Fileserver
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-fileserver-${MS_VERSION}.war -O kitodo-mediaserver-fileserver.war

# Download CLI
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-cli-${MS_VERSION}.jar -O kitodo-mediaserver-cli.jar

# Download web UI
wget -q https://github.com/tuub/kitodo-mediaserver/releases/download/${MS_VERSION}/kitodo-mediaserver-ui-${MS_VERSION}.war -O kitodo-mediaserver-ui.war
```

If you build the code from source, you would want to keep your local settings in the `local.yml` file stored in `kitodo-mediaserver-local/src/main/resources/config/`. If you're installing from the released jar and war files, you may create a `local.yml` file using the `default.yml`in the following manner:

```bash
# Download default local configuration file
wget -q https://raw.githubusercontent.com/tuub/kitodo-mediaserver/${MS_VERSION}/kitodo-mediaserver-core/src/main/resources/config/default.yml -O local.yml

# Optionally comment out every parameter in config file.
# You may activate and change the parameters you need after that.
sed -i 's/^\([^#]\)/#\1/g' local.yml
```

### Link the Fileserver and UI to Tomcat

```bash
ln -s /usr/local/kitodo-mediaserver/kitodo-mediaserver-fileserver.war /var/lib/tomcat8/webapps/
# UI is optionally
ln -s /usr/local/kitodo-mediaserver/kitodo-mediaserver-ui.war /var/lib/tomcat8/webapps/
```

### Create working directories

```bash
mkdir -p /var/local/kitodo-mediaserver
mkdir /var/local/kitodo-mediaserver/hotfolder
mkdir /var/local/kitodo-mediaserver/importing
mkdir /var/local/kitodo-mediaserver/import_temp
mkdir /var/local/kitodo-mediaserver/import_error
mkdir /var/local/kitodo-mediaserver/files
mkdir /var/local/kitodo-mediaserver/logs
```

The cache directory is only needed if you want to use derivatives caching:

```bash
mkdir /var/local/kitodo-mediaserver/cache
```

### Permissions

It's a good way to give full access to your tomcat user to the whole data dir.
 
```bash
chown -R tomcat8 /var/local/kitodo-mediaserver/*
```

If you need a more granular setting, these applications require write access to folders:

- Fileserver:
    - `cache`: The owner needs to be the user that runs the fileserver. The fileserver stores cache files in it and it "touches" the files to save the last access. This is done in Java and will work only if the file is owned by the running user.
    - `logs`
- CLI:
    - ALL - The CLI with several commands needs write access to all data folders.
- UI:
    - `files`, `cache`: It may run actions on works that will modify the work or cache for that work.
    - `logs`
- Kitodo.Production (or other production software):
    - `hotfolder`: Store works in it to be imported by the Mediaserver. You may set a specific owner group containing both tomcat users or use ACLs.

## Configure the Mediaserver

In */usr/local/kitodo-mediaserver/* you will find the configuration file *local.yml*. This file carries all your Mediaserver configuration. Have a look at the [Configuration file](Configuration-file.md) reference to find out more about all settings.

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
  hotfolderPath: /var/local/kitodo-mediaserver/hotfolder/
  importingFolderPath: /var/local/kitodo-mediaserver/importing/
  errorFolderPath: /var/local/kitodo-mediaserver/import_error/
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
  cachePath: /var/local/kitodo-mediaserver/cache/
```

### Tomcat configuration

To tell Tomcat to use your own external configuration file, we need to set a parameter in `CATALINA_OPTS` or `JAVA_OPTS` environment variable. This path would be the directory where your CLI JAR and your configuration files are saved. Create or edit *setenv.sh* (see Tomcat documentation) and add:

```bash
export CATALINA_OPTS="$CATALINA_OPTS -Dspring.config.additional-location=/usr/local/kitodo-mediaserver/"
```

On Ubuntu 16.04 and 18.04 you may set these variables in */etc/default/tomcat8*.

**Attention**: This setting will affect all apps deployed on this Tomcat instance. If you have other apps running, you should use a special context XML file for the Mediaserver which is a bit more complex.

### CLI command

Create a wrapper script for the CLI.

File */usr/local/bin/kitodo-mediaserver*:

```bash
#!/usr/bin/env bash

# Command line wrapper for Kitodo.Mediaserver CLI
sudo -u tomcat8 java -jar /usr/local/kitodo-mediaserver/kitodo-mediaserver-cli.jar "$@"

exit $?
```

And give execution rights:
```bash
chmod +x /usr/local/bin/kitodo-mediaserver
```

## Initialize database

Use the CLI to create the database schema:
```bash
kitodo-mediaserver updatedb
```
Note: This works without JAVA_OPTS variable if config directory is in CLI-JAR directory.

## Run the Mediaserver

The Fileserver and the UI are ready to run but you'll need some work data to be served. Use the CLI to import your Kitodo.Production works to the Mediaserver. Have a look at [CLI usage guide](CLI-usage-guide.md) to learn how to import works.

To start the fileserver and the UI you have to start Tomcat. With the default Tomcat setup the fileserver should be reachable under *http://<span>a</span>ny.domain.name/kitodo-mediaserver-fileserver/* and the UI should be under *http://<span>a</span>ny.domain.name/kitodo-mediaserver-ui/*. Have a look at [Web UI usage guide](Web-UI-usage-guide.md) to learn how to use the web UI.

To test the fileserver you can open an URL from one of the METS/MODS files of imported works. For example search for `<mets:file ID="FILE_0001_THUMBS"` in your METS/MODS file and open the URL under `xlink:href="..."` in your web browser. If an image is shown and the image size is correct, it worked.
