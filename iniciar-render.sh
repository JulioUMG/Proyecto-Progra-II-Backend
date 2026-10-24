#!/bin/sh
set -eu

# Render entrega el certificado de Aiven como archivo secreto.
# Se importa para comprobar el certificado de la conexión MySQL.
rm -f /tmp/aiven-truststore.p12
keytool -importcert -noprompt -alias aiven \
  -file /etc/secrets/aiven-ca.pem \
  -keystore /tmp/aiven-truststore.p12 \
  -storetype PKCS12 -storepass changeit >/dev/null

exec java -jar /app/app.jar
