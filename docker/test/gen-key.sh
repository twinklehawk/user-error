#!/usr/bin/env bash
set -euo pipefail

keytool -genkeypair -keystore test-auth.jks -storepass test-pass -storetype pkcs12 -alias test-auth -keyalg EC -keysize 256 -keypass test-pass -validity 365 -dname "CN=test, OU=test, O=test, C=US"
