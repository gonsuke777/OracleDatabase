#!/bin/sh
# This script decrypt the encrypted password by /etc/machine-id.
# The machine-id is unique for each virtual machine, and only that virtual machine can correctly decrypt the password.
# Therefore, even if encrypted password is compromised, the original password cannot be decrypted.
# Store the encrypted password in a env file, environment variable, or others...
# This script was referenced in the following article. Thanks!
# https://qiita.com/kazuhidet/items/122c9986ca0edd5284ff

# Decrypt Function
decrypt_password() {
  encrypted_password="$1"
  system_uuid=$(cat /etc/machine-id)
  plain_password=$(echo "${encrypted_password}" | openssl enc -d -des -base64 -k "${system_uuid}")
  echo "${plain_password}"
}

# Usage:
# echo $(decrypt_password "$1")

