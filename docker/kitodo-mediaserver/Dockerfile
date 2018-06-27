FROM tomcat:8.5-jre8

# the Kitodo Mediaserver version to build this Docker image with
ARG MS_VERSION=1.0-SNAPSHOT
ARG MS_GITHUB=tuub/kitodo-mediaserver
ARG MS_BUILD_FROM_SOURCE=0

# installation path
ENV MS_PATH=/usr/local/kitodo-mediaserver \
    MS_DATA_PATH=/srv/kitodo/mediaserver

# install GraphicsMagick and Ghostscript
RUN set -xe; \
	apt-get update; \
	apt-get install -y --install-recommends graphicsmagick ghostscript; \
	rm -rf /var/lib/apt/lists/*;

# add binaries to image - this is needed for MS_BUILD_FROM_SOURCE=1
COPY kitodo-mediaserver-cli-${MS_VERSION}.jar ${MS_PATH}/kitodo-mediaserver-cli.jar
COPY kitodo-mediaserver-fileserver-${MS_VERSION}.war ${MS_PATH}/kitodo-mediaserver-fileserver.jar
COPY kitodo-mediaserver-ui-${MS_VERSION}.war ${MS_PATH}/kitodo-mediaserver-ui.war
COPY local.yml ${MS_PATH}/config/local.yml.dist

# download Mediaserver
RUN set -xe; \
    mkdir -p ${MS_PATH} ${MS_PATH}/config; \
    cd ${MS_PATH}; \
    if [ "$MS_BUILD_FROM_SOURCE" -ne 1 ]; then \
        wget -q https://github.com/${MS_GITHUB}/releases/download/${MS_VERSION}/kitodo-mediaserver-fileserver-${MS_VERSION}.war -O kitodo-mediaserver-fileserver.war; \
        wget -q https://github.com/${MS_GITHUB}/releases/download/${MS_VERSION}/kitodo-mediaserver-cli-${MS_VERSION}.jar -O kitodo-mediaserver-cli.jar; \
        wget -q https://github.com/${MS_GITHUB}/releases/download/${MS_VERSION}/kitodo-mediaserver-ui-${MS_VERSION}.war -O kitodo-mediaserver-ui.war; \
        wget -q https://raw.githubusercontent.com/${MS_GITHUB}/${MS_VERSION}/kitodo-mediaserver-core/src/main/resources/config/local.yml -O ${MS_PATH}/config/local.yml.dist; \
    fi

# install Mediaserver
RUN set -xe; \
    cd "${CATALINA_HOME}"; \
    rm -rf webapps; \
    mkdir fileserver ui; \
    ln -s ${MS_PATH}/kitodo-mediaserver-fileserver.war fileserver/ROOT.war; \
    ln -s ${MS_PATH}/kitodo-mediaserver-ui.war ui/ROOT.war; \
    mkdir -p ${MS_DATA_PATH}/config \
        ${MS_DATA_PATH}/cache \
        ${MS_DATA_PATH}/files \
        ${MS_DATA_PATH}/hotfolder \
        ${MS_DATA_PATH}/importing \
        ${MS_DATA_PATH}/import_error \
        ${MS_DATA_PATH}/logs

# use external config file for all java apps
# in Docker we have to pass JAVA_OPTS to every java call: https://c4.vc/2015/12/23/Docker-Spring-Boot-and-JAVA-OPTS/
ENV JAVA_OPTS="$JAVA_OPTS -Dspring.config.location=classpath:/config/,${MS_PATH}/config/"

# starter script
COPY run-container.sh ${MS_PATH}/

# Tomcat configuration
COPY server.xml ${CATALINA_HOME}/conf/

# CLI wrapper script for easier external usage
COPY cli /usr/local/bin/

VOLUME ${MS_PATH}/config
VOLUME ${MS_DATA_PATH}/cache
VOLUME ${MS_DATA_PATH}/files
VOLUME ${MS_DATA_PATH}/hotfolder
VOLUME ${MS_DATA_PATH}/importing
VOLUME ${MS_DATA_PATH}/import_error
VOLUME ${MS_DATA_PATH}/logs

EXPOSE 8980/tcp
EXPOSE 8981/tcp

CMD ${MS_PATH}/run-container.sh
