#!/usr/bin/env bash

echo "generate root CA key"
openssl genrsa -out root_ca.key

echo "generate root CA certificate"
openssl req -new -key root_ca.key -out root_ca.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=FoodIST CA/OU=IT Department/CN=ca.foodist.com"
openssl x509 -req -days 365 -in root_ca.csr -signkey root_ca.key -out root_ca.crt

echo "create database file (.srl)"
echo 01 > root_ca.srl

echo "generate server key and certificate"
# openssl genrsa -out server.key
openssl genpkey -out server.key -algorithm RSA -pkeyopt rsa_keygen_bits:2048
openssl req -new -key server.key -out server.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=FoodIST gRPC/OU=IT Department/CN=server.foodist.com"
openssl x509 -req -days 365 -in server.csr -CA root_ca.crt -CAkey root_ca.key -out server.crt
