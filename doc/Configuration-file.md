The Mediaserver uses multiple configuration files. Only [default.yml](https://github.com/tuub/kitodo-mediaserver/blob/master/kitodo-mediaserver-core/src/main/resources/config/default.yml) is provided by the Mediaserver itself. It contains default values and should not be modified. In the optional *local.yml* you can set your configuration. And in the optional *secrets.yml* you can save usernames, passwords and other secret tokens that should not be stored in Git.

It is recommended to save the configuration files (*local.yml*, *secrets.yml*) next to your CLI JAR file. Thus the CLI application will find this file without any further parameters.

The configuration files are in [YAML](https://en.wikipedia.org/wiki/YAML) format.

## Database configuration

You have to use the Spring settings to tell Mediaserver your database settings.

### MySQL

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost/mediaserver?autoReconnect=true&useSSL=false
    username: kitodo
    password: kitodo
```

The `url` parameter uses the format `jdbc:mysql://DB-HOSTNAME/DB-NAME?autoReconnect=true&useSSL=false`

## Configuration parameters

* **`logging`**:
  * `path` (string: filesystem path): Path to the directory where log files should be saved.
  
* **`fileserver`**:
  * `rootUrl` (string: URL): The base URL for all files served by fileserver. It has to match the URLs in your METS/MODS files.
  * `filePathPattern` (string: URL path): The URL path to use for the file server. `/files/{workId}/**` will work for an URL like *http://<span>d</span>omain.de/files/ABC123/...*.
  * `caching` (bool): `true` or `false` - Enable or disable caching of produced derivatives. 
  * `cachePath` (string: filesystem path): Path to the cache files. The folder needs write permissions from the fileserver process.
  * `convertAction` (string: Spring bean): A Spring Action Bean defining the convert action. By default there are two convert actions available for on-demand single file conversion: `onDemandIMSingleFileConvertAction`, using ImageMagick or GraphicsMagick shell command, and `onDemandAwtPdfboxSingleFileConvertAction` using an internal conversion implementation in Java. The widely tested ImageMagick variant might be more stable. The Java implementation is faster and with `onDemandAwtPdfboxSingleFileConvertAction` you can create searchable PDF/A-1b files using your OCR texts.
  * `cacheClearCron` (string: UNIX Cron format): If scheduling is used this is the schedule in [UNIX cron](https://en.wikipedia.org/wiki/Cron) format like `0 5 2 * * *`.
  * `cacheClearSince` (int: seconds): File that are touched since this value should be deleted by a cache clear run.
  * `allowedNetworks` (map: IP subnets): Contains multiple IP subnet definitions defining the access level for a work. Every work can have one network. There are two default networks: `global: 0.0.0.0/0,::/0` allows access from everywhere. `disabled: 0.0.0.0/32,::/128` disables access for everyone. `disabled` also allows to set a comment and to create a reduced METS/MODS file with less information about the work.
  * `disabledWorkImagePath` (String: filesystem path): A path to a image file which is used as placeholder for disabled works. 

* **`conversion`**:
  * `pdf`:
    * `defaultSize` (int): The default maximum size in pixels the images are resized to when converting to PDF format including extension for watermarks.
    * `addOcrText` (bool): `true` or `false` - Whether to add OCR text layer to PDF or not. See also: `mets.fulltextFileGrp`
    * `maxMemory` (int): Use this amount of RAM in Megabyte for PDF generation. If the conversion process needs more memory it will use a disk cache in the system temp folder.
    * `iccProfile` (string: filesystem path): Path to an ICC profile file. This color profile gets embedded to files if the converter supports it. Mainly this is for PDF/A support by PdfboxFileConverter. On Ubuntu with installed *icc-profiles-free* package this uses the provided *sRGB.icc* file.
  * `jpeg`:
    * `defaultSize` (int): The default maximum size in pixels the images are resized to when converting to image format including extension for watermarks.
  * `useGraphicsMagick` (bool): `true` or `false` - Whether to use [GraphicsMagick](http://www.graphicsmagick.org/) for image conversion.
  * `pathExtractionPatterns` (list of regex strings): TODO
  * `watermark`:
    * `enabled` (bool): `true` or `false` - Whether to add watermarks or not.
    * `minSize` (int: pixels): Defines the minimum horizontal size (x) when a watermark should be applied.
    * `renderMode` (string): `text` or `image`
    * `gravity` (string): The edge position of the image from where to apply the text. Use cardinal direction like "north", "northeast", "southwest" etc  
    * `offsetX` (int: pixels): Defines the horizontal offset (x), based on the `gravity`
    * `offsetY` (int: pixels): Defines the vertical offset (y), based on the `gravity`
    * `textMode`:
      * `content` (string): The text to be applied
      * `font` (string): The font family to use for the `content`, e.g. "Arial"
      * `colorRGB` (string: RGB): The font color of the `content`, as rgb values: e.g. "22,33,44".
      * `size` (int: points): The font size of the `content`.      
    * `imageMode`:
      * `path` (string: filesystem path): The path to a watermark image.
      * `opacity` (int: percent): A percentage value for transparency (0 = not visible, 100 = fully visible)
    * `extendCanvas`:
      * `enabled` (boolean): `true` or `false` - Whether to extend the canvas of the image
      * `backgroundColorRGB` (string: RGB): The background color of the extended area, as rgb values: e.g. "22,33,44".
      * `addX` (int: pixels): Defines how many pixels should be added to the canvas. Check watermark image dimensions (image mode), font size (text mode) and offsets to achieve good results.  
      * `addY` (int: pixels): Defines how many pixels should be added to the canvas. Check watermark image dimensions (image mode), font size (text mode) and offsets to achieve good results.

* **`mets`**:
  * `originalFileGrp` (string): e.g. `ORIGINAL` or `PRESENTATION` - Which file group from the METS file should be used as master source.
  * `fulltextFileGrp` (string): e.g. `FULLTEXT` - METS file group containing OCR text files for inclusion into PDF files.
  * `workLockReduceMetsXsl` (string: path): The path to the XSLT transformation file. This file is used to create a reduced METS/MODS file when disabling a work.

* **`indexing`**:
  * `indexScriptUrl` (string: URL): The URL to Kitodo.Presentation forcing an reindexing of the work.
  * `indexScriptMetsUrlArgName` (string): The URL parameter name containing the URL to the METS/MODS XML file when triggering the reindexing process in Kitodo.Presentation.

* **`doi`**:
  * `dataCiteURL` (string: URL): The DOI registration API URL
  * `dataCiteUser` (string): DOI API username
  * `dataCitePassword` (string): DOI API password
  * `doiLandingPagePattern` (string): The URL to use as destination for DOIs. You may use "{workId}" as palceholder for your workIds. Example: "https://digital.example.com/works/{workId}"
  * `doiDataReaderXsl` (string: class resources path): Path to a XSLT file to read DOIs from METS files

* **`importer`**:
  * `hotfolderPath` (string: filesystem path): The directory where the mediaserver should check for new works to import.
  * `importingFolderPath` (string: filesystem path): /usr/local/kitodo/mediaserver/importing/
  * `tempWorkFolderPath` (string: filesystem path): /usr/local/kitodo/mediaserver/import_temp/
  * `errorFolderPath` (string: filesystem path): If there are errors importing a work they get moved to this folder.
  * `workFilesPath` (string: filesystem path): All successfully imported works are in the directory. This is the main storage of the original work files and images where the Mediaserver looks for to create derivatives.
  * `workIdRegex` (string: regex): The work IDs must match this regular expression pattern to be valid.
  * `cron` (string): For job scheduling this defines when the jobs will run. It uses [UNIX cron](https://en.wikipedia.org/wiki/Cron) format like `0 5 2 * * *`.
  * `indexWorkAfterImport` (bool): `true` or `false` - Whether to call reindexing after import.
  * `validationFileGrps` (list of strings): Valid entries: `ORIGINAL`, `PRESENTATION` and `FULLTEXT` (TODO)
  * `actionsBeforeIndexing` (list): A list of action beans to be run before indexing, e.g. 
    ```yaml
    actionsBeforeIndexing:
      - abbyyToAltoOcrConvertAction
      - addFullPdfToMetsAction
      - preproduceFullPdfFileConvertAction
      - cleanMetsTitleEntriesAction:
          pattern: "<<(.*?)>>"
      - preproduceDerivativesAction:
          fileGrp: THUMBS
          fileId: FILE_0001_THUMBS
    ```
  * `actionsAfterSuccessfulIndexing` (list): A list of action beans to be run after indexing.
  * `actionsToRequestAsynchronously` (list): A list of action beans to be run after indexing. These actions will be requested only and will run asynchronously later.

* **`cli`**:
  * `performActions`:
    * `continueOnError` (bool): `true` or `false` - Stop after an error or run further actions. Default: false
    * `cron` (string: UNIX Cron format): If scheduling is used this is the schedule in [UNIX cron](https://en.wikipedia.org/wiki/Cron) format like `0 5 2 * * *`.

* **`ui`**: Settings for UI - the web interface
  * `pagination`:
    * `elementsPerPage`:
      * `availableValues` (list of int): Number of elements that are available in the dropdown field under lists
  * `works`:
    * `searchableFields` (list of string): Which data base fields are searchable by "field:pattern" in the UI's search field
    * `reduceMets` (bool): `true` or `false` - Whether to create a reduced METS/MODS file when disabling a work. This is only the default value for the UI and can be changed by the user when disabling a work.
    * `actions` (map): A list of actions to be shown in UI. These actions can be triggered via the actions menu.
      * `<userdefined-name-of-action>` (string): A unique identifier for the action
        * `label` (string): The label of this action shown in the action menu in UI
        * `action` (string): The bean name of the action
        * `enabled` (bool): `true` or `false` - Whether to enabled or disable this action definition. Use this key to disable predefined actions.
        * `parameters` (map): A map of parameters for this action
      Example:
      ```yaml
      actions:
        thumbs:
          label: Generate Thumbnails
          action: preproduceDerivativesAction
          enabled: true
          parameters:
            fileGrp: MIN
        makeFullPDF:
          label: Generate Full PDF
          action: preproduceFullPdfFileConvertAction
          enabled: true
      ```
