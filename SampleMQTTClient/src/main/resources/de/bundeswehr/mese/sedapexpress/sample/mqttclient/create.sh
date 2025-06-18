#!/bin/bash

IP="127.0.0.1"
SUBJECT_CA="/C=DE/L=Germany/O=MESE/OU=CA/CN=$IP"
SUBJECT_SERVER="/C=DE/L=Germany/O=MESE/OU=Server/CN=$IP"
SUBJECT_CLIENT="/C=DE/L=Germany/O=MESE/OU=Client/CN=MESE"

   echo "$SUBJECT_CA"
   openssl req -x509 -nodes -sha256 -newkey rsa:2048 -subj "$SUBJECT_CA" -days 3650 -keyout ca.key -out ca.crt

   echo "$SUBJECT_SERVER"
   openssl req -nodes -sha256 -new -subj "$SUBJECT_SERVER" -keyout server.key -out server.csr
   openssl x509 -req -sha256 -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 1825 -extfile <(cat /etc/ssl/openssl.cnf <(printf "[SAN]\nsubjectAltName=IP:127.0.0.1,DNS:MESE11")) -extensions SAN

   echo "$SUBJECT_CLIENT"
   openssl req -new -nodes -sha256 -subj "$SUBJECT_CLIENT" -out client.csr -keyout client.key 
   openssl x509 -req -sha256 -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client.crt -days 1825

