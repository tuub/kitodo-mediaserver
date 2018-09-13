#!/bin/sh

# default values
MS_CREATE_TABLES="${MS_CREATE_TABLES:-0}"
MS_SCHEDULER_IMPORT="${MS_SCHEDULER_IMPORT:-0}"
MS_SCHEDULER_CACHECLEAR="${MS_SCHEDULER_CACHECLEAR:-0}"

# If config dir is empty, copy the default config file over
if [ ! -f "${MS_PATH}/config/local.yml" ]; then
    echo "Creating default config file..."
    cp "${MS_PATH}/config/local.yml.dist" "${MS_PATH}/config/local.yml"
fi

# Create and init database - this should only be called once on a new installation
if [ "$MS_CREATE_TABLES" -eq 1 ]; then
    echo "Creating Mediaserver DB tables..."
    java $JAVA_OPTS -jar "${MS_PATH}/kitodo-mediaserver-cli.jar" initdb
    echo "Creating DB tables finished."
fi

# run import scheduler
if [ "$MS_SCHEDULER_IMPORT" -eq 1 ]; then
    echo "Start import scheduler..."
    java $JAVA_OPTS -jar "${MS_PATH}/kitodo-mediaserver-cli.jar" import -s &
    echo "Import scheduler started."
fi

# run cache-clear scheduler
if [ "$MS_SCHEDULER_CACHECLEAR" -eq 1 ]; then
    echo "Start cache-clear scheduler..."
    java $JAVA_OPTS -jar "${MS_PATH}/kitodo-mediaserver-cli.jar" cacheclear -s &
    echo "Cache-clear scheduler started."
fi

# Run Tomcat
catalina.sh run

exit $?
