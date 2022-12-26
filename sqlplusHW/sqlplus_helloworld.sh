#!/bin/bash
################################################################
#
# Overview: This shell outputs "Hello, world!" by the sqlplus.
# Pre-requirement: Put sqlplus_helloworld.sql in the same directory.
# Usage: sqlplus_helloworld.sh $1
# $1 ... Environment variable file.
#
# Return Value ... 0(Success) or 1(Error)
#
################################################################

set -eu

# Set enviroment variable
. "$1"
mypath=$(dirname ${0})
logname=$(date +'sqlplus_helloworld_sql_%Y%m%d_%H%M%S.log')
logfile="${mypath}/${logname}"

date +"%Y-%m-%dT%H:%M:%S%:z Script start. This script is logging to ${logfile}."
date +"%Y-%m-%dT%H:%M:%S%:z Script start. This script is logging to ${logfile}." >> ${logfile}

# Decrypt password
. ${mypath}/decrypt_password.sh
DB_PASSWORD_ENC=$(decrypt_password "${DB_PASSWORD}")

# Run the SQL with sqlplus. 
sqlplus /nolog << EOF >> ${logfile} 2>&1 && ret=0 || ret=$?
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
WHENEVER OSERROR EXIT FAILURE ROLLBACK
CONNECT ${DB_USER}/${DB_PASSWORD_ENC}@${DB_CONNECT_STR}
@${mypath}/sqlplus_helloworld.sql
EXIT SUCCESS;
EOF

# Error check
error_cnt=$(grep -Eic "^ORA-[0-9]+|SP2-[0-9]+" "${logfile}") || :
if [ "${ret}" -eq 0 ] && [ "${error_cnt}" -eq 0 ]; then
  date +'%Y-%m-%dT%H:%M:%S%:z SQL completed successfully.' >> ${logfile}
  date +'%Y-%m-%dT%H:%M:%S%:z SQL completed successfully.'
  exit 0
else
  date +'%Y-%m-%dT%H:%M:%S%:z SQL terminated abnormally.' >> ${logfile}
  date +'%Y-%m-%dT%H:%M:%S%:z SQL terminated abnormally.'
  exit 1
fi
