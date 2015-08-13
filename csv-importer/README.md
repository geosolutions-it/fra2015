##Readme

###Requirements

* maven 2.2.1
* git
* cURL

Tested on Windows 7 with a base MinGW environment installation

###How to 

clone this repo:

```
# git clone git@github.com:Damianofds/fra2015.git
```

build the entire project, from the root of the repo:

```
# mvn clean install
```

create the XML files from the CSVs:

```
# cd ${rootRepo}/csv-importer/target
# java -jar fra2015-csvimporter.jar
```

You can see an help which describe how to pass the required parameters:
the absolute path of the 2 CSVs which compose the surveys and the absolute path of the outputdirectory.

Once the xml are created they must imported into the Application through a custom bash script:

```
# cd ${rootRepo}/csv-importer/src/bash
# bulkImport.sh <fraApplicationUrl> <absolute path of the directory which contains XML files> <admin password>
```

This import can take a long time, tailthe server side log in the meanwhile to ensure everything is working properly.
