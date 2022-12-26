#!/bin/sh
# This script encrypt strings by machine-id.
# Set the string to be encrypted as the 1st parameter of this shell.
# To prevent the pre-encrypt strings from remaining in the history, 
# it is recommended to put the strings in a separate file and run as follows.
# encrypt_password.sh `cat xxxxxxxx.txt`
# This script was referenced in the following article. Thanks!
# https://qiita.com/kazuhidet/items/122c9986ca0edd5284ff

#Encrypt Fnction
encrypt_password() {
  plain_password="$1"
  system_uuid=$(cat /etc/machine-id)
  encrypted_password=$(echo "${plain_password}" | openssl enc -e -des -base64 -k "${system_uuid}")
  echo "${encrypted_password}"
}

# Usage
echo "$(encrypt_password "$1")"

