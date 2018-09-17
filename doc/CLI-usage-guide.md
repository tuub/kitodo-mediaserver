The command line interface (CLI) provides basic control over Kitodo.Mediaserver.

## Basic usage

As it is a Java JAR file you need to call it using java:
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar
```
This command without any arguments will show you the usage help. There are different sub commands with specific options. You can show sub command usage by typing:
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar <subcommand> -h
```

## Logging

By default CLI logs output to *cli.log* in your logs folder which is configured in your *local.yml*.

## Run the Importer

A main task is to import works coming from Kitodo.Production. The CLI allows to import all works waiting in your hotfolder at once:
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar import
```

You can also start a process to run the import periodically on a defined schedule (defined by *importer.cron* in *local.yml*):
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar import -s
```
This process will run infinitely until it is killed by a user (e.g. by Ctrl+C).

## Clear derivative files cache

You can clear the derivative files cache for all works at once:
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar cacheclear
```
By default this deletes files not touched since 30 days (defined by *fileserver.cacheClearSince*). As with the Importer you can run the cache clearing schaduled (defined by *fileserver.cacheClearCron*)
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar cacheclear -s
```
