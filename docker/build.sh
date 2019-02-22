#!/usr/bin/env bash

usage() {
    cat <<EOT
$0 - Build Kitodo Mediaserver Docker image

Usage: $0 -v 1.0.0
    Build image for official Kitodo.Mediaserver v1.0.0.

Usage: $0 -s
    Build image for your own Kitodo.Mediaserver build.

Options:
  -g, --github=namespace        The GitHub namespace to download Mediaserver from
                                (e.g. "tuub/kitodo-mediaserver")
  -n, --name                    Docker image name
  -v, --version=version         The version to build, e.g. "1.0.0",
                                matches Mediaserver version and docker image version
  -s, --source                  Build image from own source build
  -f, --force                   Force building image and don't use cache
EOT
    exit 3
}

MS_VERSION=1.0.0
MS_NAME=tubub/kitodo-mediaserver
MS_GITHUB=tuub/kitodo-mediaserver
MS_BUILD_FROM_SOURCE=0
MS_BUILD_FORCE=

OPTIONS=$(getopt -n $0 -o g:v:n:sfh --long github:,version:,name:,source,force,help -- "$@")
[[ $? -ne 0 ]] && usage

eval set -- "$OPTIONS"

while true ; do
    case $1 in
        -v|--version)
            MS_VERSION="$2"
            shift
            ;;
        -n|--name)
            MS_NAME="$2"
            shift
            ;;
        -g|--github)
            MS_GITHUB="$2"
            shift
            ;;
        -s|--source)
            MS_BUILD_FROM_SOURCE=1
            ;;
        -f|--force)
            MS_BUILD_FORCE=--no-cache
            ;;
        --)
            shift
            break
            ;;
        *)
            usage
            ;;
    esac
    shift
done

cat <<EOT
MS_VERSION=$MS_VERSION
MS_NAME=$MS_NAME
MS_GITHUB=$MS_GITHUB
MS_BUILD_FROM_SOURCE=$MS_BUILD_FROM_SOURCE
MS_BUILD_FORCE=$MS_BUILD_FORCE
EOT

if [ $MS_BUILD_FROM_SOURCE -eq 1 ]; then
    cp ../kitodo-mediaserver-cli/target/*.jar kitodo-mediaserver/ || exit 1
    cp ../kitodo-mediaserver-fileserver/target/*.war kitodo-mediaserver/ || exit 1
    cp ../kitodo-mediaserver-ui/target/*.war kitodo-mediaserver/ || exit 1
    cp ../kitodo-mediaserver-local/src/main/resources/config/local.yml kitodo-mediaserver/ || exit 1
fi

docker build \
    -t "$MS_NAME:$MS_VERSION" \
    --build-arg MS_VERSION="$MS_VERSION" \
    --build-arg MS_GITHUB="$MS_GITHUB" \
    --build-arg MS_BUILD_FROM_SOURCE="$MS_BUILD_FROM_SOURCE" \
    $MS_BUILD_FORCE \
    kitodo-mediaserver

exit $?
