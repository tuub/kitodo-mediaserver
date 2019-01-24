**Kitodo.Mediaserver** is a tool for management of digitization files. Features: 

* Management of files concerning a digitized work
* Creation of derivatives for viewers on demand or by import
* Configurable caching of created files
* Files may be put in quarantine in case of copyright issues
* An open software architecture making it possible for developers to add new conversion features or other actions to be performed on works

Digitized works consisting of a METS/MODS file, images files and optionally ocr or other files are imported to the Kitodo.Mediaserver and stored there for delivery to presentation tools. The Kitodo.Mediaserver has the ability to create derivatives of the images files either by import, for permanent storage, or on the fly, converting the image as requested. A configurable caching supports the creation of files on demand. 

Using an administrator UI, you may block the access to the files of any work, a service useful in case of copyright prosecutions etc. When blocking a work, it is also possible to reduce the metadata in the METS/MODS document to a minimum.

All operations on a work are executed technically as actions implementing a simple action interface. Thus it is easy to extend the Kitodo.Mediaserver with new operations or replace existing ones with different implementations. A simple database makes it possible to either perform actions directly or just order them to be performed by another process later on.

## Help

* [Installation](Installation.md)
  * [Install manually](Install-manually.md)
  * [Install using Docker](Install-using-Docker.md)
  * [Install from source](Install-from-source.md)
  * [Configuration file](Configuration-file.md)
* Usage Guide
  * [Web UI usage guide](Web-UI-usage-guide.md)
  * [CLI usage guide](CLI-usage-guide.md)
  * [Provided actions](Actions.md) - What they do and how to use them
* [Developer Guide](Developer-Guide.md)
