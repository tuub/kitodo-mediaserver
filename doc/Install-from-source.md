**Attention**: Installing from source should only be done if you know, what you're doing - if you are a developer.

## Prerequisites

* Linux operating system (tested on Ubuntu 16.04 and 18.04)
* OpenJDK 8 (OpenJDK 9 and newer will not work)
* Maven >=3.3
* (optionally for UI) NodeJS >=8 and NPM >=5.7.1

## Build from source

Download the source code:

```bash
git clone https://github.com/tuub/kitodo-mediaserver.git
```

Build Mediaserver and create packages (JAR and WAR files). From the root directory of the sources:

```bash
mvn install
```

Or build without UI module:

```bash
mvn install -pl \!kitodo-mediaserver-ui
```

You will find the package files in the four modules directories in "target" subfolders: `kitodo-mediaserver/kitodo-mediaserver-*/target/`

Now follow the [Install manually](Install-manually.md) guide to install the Mediaserver and don't download the Mediaserver but use these JAR and WAR files you just built here. 
