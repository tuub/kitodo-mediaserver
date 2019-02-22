FROM tomcat:8.5-jre8

# the Kitodo Mediaserver version to build this Docker image with
ARG MS_VERSION=1.0.0
ARG MS_GITHUB=tuub/kitodo-mediaserver
ARG MS_BUILD_FROM_SOURCE=0

# installation path
ENV MS_PATH=/usr/local/kitodo-mediaserver \
    MS_DATA_PATH=/var/local/kitodo/mediaserver \
    MS_FILESERVER_PATH=fileserver \
    MS_UI_PATH=admin \
    MS_PROXY_NAME="" \
    MS_PROXY_PORT="" \
    MS_UPDATE_DB=0

# use external config file for all java apps
# in Docker we have to pass JAVA_OPTS to every java call: https://c4.vc/2015/12/23/Docker-Spring-Boot-and-JAVA-OPTS/
ENV JAVA_OPTS="$JAVA_OPTS -Dspring.config.additional-location=${MS_PATH}/config/"

# Install dependencies
RUN set -xe; \
	apt-get update; \
	apt-get install -y --install-recommends graphicsmagick ghostscript icc-profiles-free; \
	rm -rf /var/lib/apt/lists/*;

# Create directories
RUN set -xe; \
    mkdir -p "${MS_PATH}/config" \
        "${MS_DATA_PATH}/cache" \
        "${MS_DATA_PATH}/files" \
        "${MS_DATA_PATH}/hotfolder" \
        "${MS_DATA_PATH}/importing" \
        "${MS_DATA_PATH}/import_error" \
        "${MS_DATA_PATH}/logs"

# Add binaries to image
# "dummy" and ".ja[r]" forces COPY to succeed even if tha jar doesn't exist.
# Because this is needed by MS_BUILD_FROM_SOURCE=1
COPY dummy \
    kitodo-mediaserver-cli-${MS_VERSION}.ja[r] \
    kitodo-mediaserver-fileserver-${MS_VERSION}.wa[r] \
    kitodo-mediaserver-ui-${MS_VERSION}.wa[r] \
    "${MS_PATH}/"
COPY dummy local.ym[l] "${MS_PATH}/config/"

# Download Mediaserver ad clear Tomcat webapps
RUN set -xe; \
    cd "${MS_PATH}"; \
    if [ "$MS_BUILD_FROM_SOURCE" -ne 1 ]; then \
        wget -q "https://github.com/${MS_GITHUB}/releases/download/v${MS_VERSION}/kitodo-mediaserver-fileserver-${MS_VERSION}.war"; \
        wget -q "https://github.com/${MS_GITHUB}/releases/download/v${MS_VERSION}/kitodo-mediaserver-cli-${MS_VERSION}.jar"; \
        wget -q "https://github.com/${MS_GITHUB}/releases/download/v${MS_VERSION}/kitodo-mediaserver-ui-${MS_VERSION}.war"; \
    fi; \
    mv kitodo-mediaserver-cli-${MS_VERSION}.jar kitodo-mediaserver-cli.jar; \
    mv kitodo-mediaserver-fileserver-${MS_VERSION}.war kitodo-mediaserver-fileserver.war; \
    mv kitodo-mediaserver-ui-${MS_VERSION}.war kitodo-mediaserver-ui.war; \
    rm -rf "${CATALINA_HOME}/webapps/"*

# starter script
COPY run-container.sh "${MS_PATH}/"

# Tomcat configuration
COPY server.xml "${CATALINA_HOME}/conf/"

# CLI wrapper script for easier external usage
COPY kitodo-mediaserver /usr/local/bin/

VOLUME ${MS_PATH}/config
VOLUME ${MS_DATA_PATH}/cache
VOLUME ${MS_DATA_PATH}/files
VOLUME ${MS_DATA_PATH}/hotfolder
VOLUME ${MS_DATA_PATH}/importing
VOLUME ${MS_DATA_PATH}/import_error
VOLUME ${MS_DATA_PATH}/logs

EXPOSE 8080/tcp

CMD "${MS_PATH}/run-container.sh"
