# Kitodo.Mediaserver

The Kitodo.Mediaserver is a tool for management of digitization files. It consists of three modules: a fileserver, a cli tool and an optional admin user interface.

## Major fields of use

* Creation of JPEG or PDF (with or without embedded OCR) derivates of master files, either on demand or preproduced on import.
* Configurable caching of created derivatives.
* Management of allowed networks (IP subnets) for the access to image files. The allowed network for a work can be set using the admin interface. Thus this feature may be used to put works temporarily in quarantine by copyright issues. There is also the option to reduce the metadata in the mets file to a minimum for this case.
* Migration support. Since the Kitodo.Mediaserver has an open architecture making it easy to configure and even implement your own actions, it can be very useful during migration, if you wish to make small changes or cleanups of the metadata or preproduce larger derivates not already at hand.

## Usage overview

Digitized works consisting of a METS/MODS file, images files, and optionally ocr or other files are imported to the Kitodo.Mediaserver and stored there for delivery to presentation tools. The Kitodo.Mediaserver has the ability to create derivatives of the images files either by import, for permanent storage, or on the fly, converting the image as requested. A configurable caching supports the creation of files on demand.

Using an administrator UI, you may block the access to the files of any work, a service useful in case of copyright prosecutions etc. When blocking a work, it is also possible to reduce the metadata in the METS/MODS document to a minimum.

All operations on a work are executed technically as actions implementing a simple action interface. Thus it is easy to extend the Kitodo.Mediaserver with new operations or replace existing ones with different implementations. A simple database makes it possible to either perform actions directly or just order them to be performed by another process later on.

### Import (using the cli tool)
* Basic work data is put in a database.
* A validation step checks that all required files are present in the import.
* Optional indexing of the work in the viewer.
* Execution of any number of actions on the work, before or after (successful) indexing.
* Requesting production of larger files to be performed by another job.
* The importer can be configured to send reports and error notifications via email.

### Fileserver
* Managing access control, creation, caching, and delivery of files.

### Admin user interface
* Search for and select works using id, words in the title, collection, or host work id.
* Set allowed network, possibly blocking all access to the image files, optionally reducing the metadata in the METS file.
* Perform any action on selected works (e.g. reindex in viewer, clean cache, register doi).
* Display of allowed networks and index date.

### CLI tool
* Process import
* Clean cache
* Perform any action on selected works (e.g. reindex in viewer, clean cache, register doi)
* Perform previously requested actions
* Setup or update database tables

## Help

* Installation
  * [Install manually](Install-manually.md)
  * [Install using Docker](Install-using-Docker.md)
  * [Install from source](Install-from-source.md)
  * [Configuration file](Configuration-file.md)
* Usage Guide
  * [Web UI usage guide](Web-UI-usage-guide.md)
  * [CLI usage guide](CLI-usage-guide.md)
  * [Provided actions](Actions.md) - What they do and how to use them
* [Developer Guide](Developer-Guide.md)
