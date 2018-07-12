# Kitodo.Mediaserver Docker image

This is the source to build a Docker image for Kitodo Mediaserver.

It's based on the official [Tomcat 8.5 using JRE 8 (tomcat:8.5-jre8)](https://hub.docker.com/_/tomcat/). So you may use its features.

## Build image

Run the build command to build a Kitodo.Mediaserver docker image. This builds an image from the official GitHub repository:
```
./build.sh --version=<version>
```

You can also build a docker image from your current source. First build Kitodo.Mediaserver then run:
```
./build.sh --source --version=<version>
```

## Use image

There is a sample *docker-compose.yml* file you can use. Modify it to your needs.
