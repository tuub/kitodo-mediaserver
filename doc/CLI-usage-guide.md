# Kitodo.Mediaserver CLI usage guide 

The command line interface (CLI) provides basic control over Kitodo.Mediaserver.

## Basic usage

As it is a Java JAR file you need to call it using java:
```bash
java -jar /path/to/kitodo-mediaserver/kitodo-mediaserver-cli.jar
```
We assume the *local.yml* configuration file is right next to the CLI JAR file and you are using the wrapper script as shown in [Installation manual](Install-manually.md).

This command without any arguments will show you the usage help. There are different sub commands with specific options. You can show sub command usage by typing:
```bash
kitodo-mediaserver <subcommand> -h
```

**Attention**: You should keep the permissions (owners) of your data directory clean. Therefor you should run the CLI as tomcat user. Thus the fileserver and UI have access too.

## Logging

By default CLI logs output to *cli.log* in your logs folder which is configured in your *local.yml*.

## Run the Importer

A main task is to import works coming from Kitodo.Production. The CLI allows to import all works waiting in your hotfolder at once:
```bash
kitodo-mediaserver import
```

You can also start a process to run the import periodically on a defined schedule (defined by *importer.cron* in *local.yml*):
```bash
kitodo-mediaserver import -s
```
This process will run infinitely until it is killed by a user (e.g. by Ctrl+C). But you could also use it as system cron job without the `-s` parameter.

## Clear derivative files cache

You can clear the derivative files cache for all works at once:
```bash
kitodo-mediaserver cacheclear
```
By default this deletes files not touched since 30 days (defined by *fileserver.cacheClearSince*). As with the Importer you can run the cache clearing schaduled (defined by *fileserver.cacheClearCron*)
```bash
kitodo-mediaserver cacheclear -s
```

## Perform action on works

Runs an action on specific work(s).

```bash
kitodo-mediaserver peform <action> <workIDpattern...> [-p=<String=String>]...
```

```bash
# Run "testAction" on work with ID "abc123"
kitodo-mediaserver perform testAction abc123

# Run "testAction" on work with ID "abc123" with parameters param2=y and param2=y
kitodo-mediaserver perform testAction abc123 -p param1=x -p param2=y

# Run "testAction" on work "abc123" and "abc789" with parameter param1=x
kitodo-mediaserver perform testAction abc123 abc789 -p param1=x

# Run "testAction" on all works with ID starting with "abc" with parameter param1=x
kitodo-mediaserver perform testAction "abc*" -p param1=x
```

## Perform requested actions

If there are requested actions in your Mediaserver database - maybe preproduce jobs scheduled on import - you can run them using this command.

```bash
kitodo-mediaserver peformrequested
```
