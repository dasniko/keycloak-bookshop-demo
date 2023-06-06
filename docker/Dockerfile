FROM registry.access.redhat.com/ubi8/ubi-minimal:8.5

ARG JAVA_PACKAGE=java-11-openjdk-headless
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Install java
# Also set up permissions for user `1001`
RUN microdnf install curl ca-certificates ${JAVA_PACKAGE} \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/conf/security/java.security

COPY ./docker/wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh
COPY ./docker/entrypoint.sh /deployments/entrypoint.sh
RUN chmod +x /deployments/entrypoint.sh

COPY ../shop/target/keycloak-bookshop-demo-shop-0.0.1-SNAPSHOT-runner.jar /deployments/shop.jar
COPY ../pim/target/keycloak-bookshop-demo-pim-0.0.1-SNAPSHOT-runner.jar /deployments/pim.jar
COPY ../cart/target/keycloak-bookshop-demo-cart-0.0.1-SNAPSHOT-runner.jar /deployments/cart.jar
COPY ../checkout/target/keycloak-bookshop-demo-checkout-0.0.1-SNAPSHOT.jar /deployments/checkout.jar
COPY ../msgbroker/target/keycloak-bookshop-demo-msgbroker-0.0.1-SNAPSHOT.jar /deployments/msgbroker.jar

EXPOSE 8081-8085
USER 1001

CMD [ "/deployments/entrypoint.sh" ]
