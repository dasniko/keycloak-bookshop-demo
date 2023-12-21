#!/bin/bash

echo "Starting apps..."
QUARKUS_JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
java -jar /deployments/pim.jar $QUARKUS_JAVA_OPTS &
java -jar /deployments/shop.jar $QUARKUS_JAVA_OPTS &
java -jar /deployments/cart.jar &
nginx -g 'daemon off;' &

wait -n
exit $?
