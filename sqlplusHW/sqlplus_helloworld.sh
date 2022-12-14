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

# Set enviroment variable
. $1
export MYPATH=`dirname ${0}`
export LOGFILE="${MYPATH}/sqlplus_helloworld_sql_`date +'%Y%m%d_%H%M%S'`.log"

# Run the SQL with sqlplus. 
sqlplus /nolog << EOF > ${LOGFILE} 2>&1
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
WHENEVER OSERROR EXIT FAILURE ROLLBACK
CONNECT ${DB_USER}/${DB_PASSWORD}@${DB_CONNECT_STR}
@${MYPATH}/sqlplus_helloworld.sql
EXIT 0;
EOF

# Error check
RET=$?
ERROR_CNT=`egrep -i "^ORA-[0-9][0-9][0-9][0-9][0-9]|^SP2-[0-9][0-9][0-9][0-9][0-9]" ${LOGFILE} | wc -l`
if [ "${RET}" -eq 0 ] && [ "${ERROR_CNT}" -eq 0 ]; then
  echo "`date +'%Y/%m/%d %H:%M:%S'` SQL completed successfully.";
  exit 0;
else
  echo "`date +'%Y/%m/%d %H:%M:%S'` SQL terminated abnormally.";
  exit 1;
fi
