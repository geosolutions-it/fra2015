#!/bin/bash
#
#

FILES=$1
HOST=$2
PASSWORD=$3

if [ ! -d "${FILES}" ]; then
	echo "The provided directory path is not a directory!"
    echo "USAGE: bulkImport.sh <fraApplicationUrl> <absolute path of the directory which contains XML files> <admin password>"
	exit 1
fi

if [ "${HOST}" == "" ]; then
	echo "Host not found! please provide the usr of the fra application (f.e http://localhost:9191/fra2015 with NO trailing slash..."
    echo "USAGE: bulkImport.sh <fraApplicationUrl> <absolute path of the directory which contains XML files> <admin password>"
	exit 1
fi

if [ "${PASSWORD}" == "" ]; then
	echo "Password not found! please provide the password for user admin!"
	echo "USAGE: bulkImport.sh <fraApplicationUrl> <absolute path of the directory which contains XML files> <admin password>"
	exit 1
fi

curl -d j_username=admin -d j_password=${PASSWORD} -L $HOST/j_spring_security_check > /dev/null 2>&1

for f in $FILES/*
do
  country=`basename ${f} .xml`
  echo ""
  echo "--------------------------------------------------------------------------"
  echo "Importing file '${f}'..."
  curl -XPOST -F"countryForImport=${country}" -F"fileData=@${f};type=text/xml" $HOST/importXml > /dev/null 2>&1
  errorCode=$?
  if [ $errorCode -eq 0 ]; then 
	echo "File '${f}' imported!" 
  else 
	echo "cURL Error during import... cURL Error code: '$errorCode'"
  fi
  echo "--------------------------------------------------------------------------"
  echo ""
done

echo "Import process terminated"