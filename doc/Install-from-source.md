**Attention**: Installing from source should only be done if you know, what you're doing - if you are a developer.

## Prerequisites

* Linux operating system (tested on Ubuntu 16.04 and 18.04)
* OpenJDK 8
* Maven 3.3
* (optionally for UI) NPM 8

## Build from source

Download the source code:

```bash
git clone https://github.com/tuub/kitodo-mediaserver.git
```

If you want to use the UI module. Install NPM dependencies:

```bash
cd kitodo-mediaserver/kitodo-mediaserver-ui
npm install
npm run gulp build
```

Build Mediaserver and create packages (JAR and WAR files). From the root directory of the sources:

```bash
mvn install
```

Now you will find the package files in the four modules directories in "target" subfolders: `kitodo-mediaserver/kitodo-mediaserver-*/target/`

Now follow the [Install manually](Install-manually.md) guide to install the Mediaserver and don't download the Mediaserver but use these JAR and WAR files you just built here. 
