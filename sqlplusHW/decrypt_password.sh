#!/bin/sh
# This script decrypt the encrypted password by machine-id.
# The machine-id is unique for each virtual machine, and only that virtual machine can correctly decrypt the password.
# Therefore, even if this script is compromised, the original password cannot be decrypted.
# Write the encrypted password by the encrypted_password.sh in the customer_encrypted_password of this script.
# This script was referenced in the following article. Thanks!
# https://qiita.com/kazuhidet/items/122c9986ca0edd5284ff

# Decrypt Function
function decrypt_password() {
  encrypted_password="$1"
  system_uuid=$(cat /etc/machine-id)
  plain_password=$(echo "${encrypted_password}" | openssl enc -d -des -base64 -k "${system_uuid}")
  echo "${plain_password}"
}

# Usage:
# echo $(decrypt_password "$1")

