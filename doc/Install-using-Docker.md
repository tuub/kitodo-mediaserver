# Install Kitodo.Mediaserver using Docker

**WARNING**: The Docker image of Mediaserver is not tested that much.

## Prerequisites

* Linux operating system (tested on Ubuntu 16.04 and 18.04)
* Docker

## Install

The Mediaserver Docker image contains everything you need for running the Mediaserver except the database. You can use an already existing database or run a new database container explicitly for Mediaserver.

You may also build your own image from your sources. Have a look at the [readme](../docker/README.md) from the [docker](../docker) directory.

### Using docker-compose

Docker-compose bundles the steps needed to run multiple docker containers with there settings and links in a simple YAML file. [docker-compose.yml](../docker/docker-compose.yml) is a sample configuration using the Mediaserver and a MySQL database.

* Change the username and password for your database!
* You need to configure the volumes to your needs to make the data persistent.

### Configuration options

- Tomcat port: *8080/tcp*.

The data paths are the configuration directory containing the *local.yml*:
- */usr/local/kitodo-mediaserver/config* 

And work files directories:
- */usr/local/kitodo-mediaserver/cache*
- */usr/local/kitodo-mediaserver/files*
- */usr/local/kitodo-mediaserver/hotfolder*
- */usr/local/kitodo-mediaserver/importing*
- */usr/local/kitodo-mediaserver/import_error*

Lastly there is the logs folder for the CLI and web UI:
- */usr/local/kitodo-mediaserver/logs*

There are some environment variables.
- `MS_FILESERVER_PATH`  
  The URL path to deploy the fileserver to. The default value `fileserver` will make it available via *http://localhost/fileserver/*. You may also use subdirectories. `sub#dir` is for *http://localhost/sub/dir/*. If you're using a proxy server like Apache between Tomcat and the user you should set the same path as the external path to make everything work correctly.
- `MS_UI_PATH`  
  The URL path to deploy the admin UI to. Defaults to `admin`.
- `MS_PROXY_NAME`    
  When using a proxy server you may set the hostname here. This value is written to Tomcats `<Connector proxyName="">`. Defaults to "".
- `MS_PROXY_PORT`  
  When using a proxy server you may set the hostname here. This value is written to Tomcats `<Connector proxyPort="">`. Defaults to "".
- `MS_UPDATE_DB`  
  If set to `1` the Mediaserver will migrate the database tables when starting. You may also use the CLI to update the DB manually. Defaults to `0`.

For more information how to configure the Mediaserver see [Install manually](Install-manually.md) and [Configuration file](Configuration-file.md).

## Usage

The docker image includes the Mediaserver CLI. For example using docker-compose you can import your works running CLI this way:
```bash
docker-compose exec mediaserver kitodo-mediaserver import
```
In this command "kitodo-mediaserver" is the Kitodo.Mediaserver CLI and allows to perform any action from the CLI. See [CLI usage guide](CLI-usage-guide.md) to learn how to use the CLI.
