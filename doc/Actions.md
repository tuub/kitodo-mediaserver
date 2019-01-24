# Provided actions

Actions in Kitodo.Mediaserver can be anything that can be performed on works or work files: conversions, creation of derivatives, manipulations of the mets file, setting of access rights etc. Actions may be executed by import, from the admin ui or using the cli tool. For examples of action configurations, see the [configuration file documentation](Configuration-file.md)

If you want to know how to create your own action, have a look at [Developer-Guide](Developer-Guide.md#Actions).

This guide tells you about provided actions - or better: their action beans - which could be easily used in your [configuration file](Configuration-file.md).

## abbyyToAltoOcrConvertAction

Converts ABBYY Finereader v10 OCR files to ALTO 2.0 format.

## addFullPdfToMetsAction

Add a file entry to the METS file that points to the full PDF file containing all images in one file. This entry works in Kitodo.Presentation and DFG-Viewer to provide "Download whole work".

##### Call parameters

- `destFile` (optional): The file path to save the file to. If unset the METS file gets overwritten.

## cacheDeleteAction

Deletes cached derivatives. This deletes files and folders from cache directory. It does not delete preproduced derivatives stored in the work directory.

##### Call parameters

- `age` (optional): Timespan in seconds. Only deletes files older than `age`.

## cleanMetsTitleEntriesAction

Remove characters in metadata title fields.

##### Call parameters

- `pattern` (required): A regular expression to search for. Example: `<<(.*?)>>` would remove "<<" and ">>" from titles.

## preproduceDerivativesAction

Generate derivatives for master files in a METS file using Java converter.

##### Call parameters

- `fileGrp` (required): The file group to generate derivatives for, e.g. `THUMBS`
- `fileId` (optional): If omitted, derivatives of all files in the file group will be created.

## preproduceFullPdfFileConvertAction

Generate a full PDF containing containing all images and possibly embedded OCR in one PDF file using Java converter.

## registerDoi

Register [DOI](https://de.wikipedia.org/wiki/Digital_Object_Identifier).

##### Config file parameters

- `identifier.*`

## setAllowedNetworkAction

Set the network access policy. The default networks are `global` (access for everyone) and `disabled` (acccess for nobody). You can add further networks in your [configuration file](Configuration-file.md) parameter `fileserver.allowedNetworks`.

##### Call parameters

- `network` (required): The network to set.
- `reduceMets` (optional): If `network` is set to "disabled", you may set this to "true". The METS file will be backed up and a reduced METS file takes it place. It will contain some metadata but no file informations.

## testAction

It does nothing but writing a message to the log. It may helpful for testing.

## viewerIndexingAction

Run the indexing of the work in Kitodo.Presentation. It basically calls an URL.

##### Config file parameters

- `indexing.*`
