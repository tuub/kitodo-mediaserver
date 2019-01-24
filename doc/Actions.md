# Provided actions

If you want to know how to create your own action, have a look at [Developer-Guide](Developer-Guide.md#Actions).

This guide tells you about provided actions - or better: their action beans - which could be easily used in your [configuration file](Configuration-file.md).

## abbyyToAltoOcrConvertAction

Converts ABBYY Finereader v10 OCR files to ALTO 2.0 format.

## addFullPdfToMetsAction

Add an file entry to the METS file that points to the full PDF file containing all images in one file. This entry works in Kitodo.Presentation and DFG-Viewer to provide "Download whole work".

##### Call parameters

- `destFile` (optional): The file path to save the file to. If unset the source file gets overwritten.

## cacheDeleteAction

Deletes cached derivatives. This deletes files and folders from cache directory. It does not delete preproduced derivatives stored in the work directory.

##### Call parameters

- `age` (optional): Timespan in seconds. Delete files older than <age> only.

## cleanMetsTitleEntriesAction

Remove characters in metadata title fields.

##### Call parameters

- `pattern` (required): A regular expression to search for. Example: `<<(.*?)>>` would remove "<<" and ">>" from titles.

## preproduceDerivativesAction

Generate derivatives for all master files in a METS file using Java converter.

##### Call parameters

- `fileGrp` (required): The file group to generate derivatives for every file, e.g. `THUMBS`
- `fileId` (optional): The file ID to generate a single derivative file for.

## preproduceFullPdfFileConvertAction

Generate a full PDF containing all images in one PDF file using Java converter.

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
