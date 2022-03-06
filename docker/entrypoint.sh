#!/bin/bash

echo "Waiting for Keycloak to start"
until $(curl --output /dev/null --silent --head --fail http://keycloak:8080); do
    printf '.'
    sleep 2
done

echo "Starting apps..."
QUARKUS_JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
java -jar /deployments/pim.jar $QUARKUS_JAVA_OPTS &
java -jar /deployments/cart.jar $QUARKUS_JAVA_OPTS &
java -jar /deployments/shop.jar $QUARKUS_JAVA_OPTS &
java -jar /deployments/checkout.jar &
java -jar /deployments/msgbroker.jar &

wait -n
exit $?
