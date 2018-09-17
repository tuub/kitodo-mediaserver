## Prerequisites

* Linux operating system (tested on Ubuntu 16.04 and 18.04)
* Docker

## Install

The Mediaserver Docker image contains everything you need for running the Mediaserver except the database. You can use an already existing database or run a new database container explicitly for Mediaserver.

### Using docker-compose

Docker-compose bundles the steps needed to run multiple docker containers with there settings and links in a simple YAML file. This is a sample configuration using the Mediaserver and a MySQL database.

* Change the username and password for your database!
* You need to configure the volumes to your needs to make the data persistent.
* `MS_CREATE_TABLES=1` creates the database tables. Remove this variable afterwards.

To have access to your data files and network ports you have to mount or map them from your docker container to the host machine. The Ports you may want to map are:
- for the fileserver: **TCP port 8980**
- for the web UI: **TCP port 8981**

The data pathes are the configuration directory containing the *local.yml*:
- */usr/local/kitodo-mediaserver/config* 

And work files directories:
- */srv/kitodo/mediaserver/cache*
- */srv/kitodo/mediaserver/files*
- */srv/kitodo/mediaserver/hotfolder*
- */srv/kitodo/mediaserver/importing*
- */srv/kitodo/mediaserver/import_error*

Lastly there is the logs folder for the CLI and web UI:
- */srv/kitodo/mediaserver/logs*

For more information how to configure the Mediaserver see [Install manually](Install-manually.md) and [Configuration file](Configuration-file.md).

## Usage

The docker image includes the Mediaserver CLI. For example using docker-compose you can import your works running CLI this way:
```bash
docker-compose exec mediaserver cli import
```
In this command "cli" is the Kitodo.Meidaserver CLI and allows to perform any action from the CLI. See [CLI usage guide](CLI-usage-guide.md) to learn how to use the CLI.
