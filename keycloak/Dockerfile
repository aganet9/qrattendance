FROM quay.io/keycloak/keycloak:26.1.0

COPY realm.json /opt/keycloak/data/import/realm.json

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--import-realm"]