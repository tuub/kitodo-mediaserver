#!/bin/sh

# Create and init database - this should only be called once on a new installation
if [ "$MS_UPDATE_DB" -eq 1 ]; then
    echo "Creating or updating Mediaserver DB tables..."
    kitodo-mediaserver updatedb
fi

# Install WARs in Tomcat webapps dir
ln -s "${MS_PATH}/kitodo-mediaserver-fileserver.war" "${CATALINA_HOME}/webapps/${MS_FILESERVER_PATH}.war"
ln -s "${MS_PATH}/kitodo-mediaserver-ui.war" "${CATALINA_HOME}/webapps/${MS_UI_PATH}.war"

# Update proxy settings for Tomcat
sed -i "s/proxyName=\"\"\\s+proxyPort=\"\"/proxyName=\"${MS_PROXY_NAME}\" proxyPort=\"${MS_PROXY_PORT}\"/g" "${CATALINA_HOME}/conf/server.xml"

# Run Tomcat
catalina.sh run

exit $?
