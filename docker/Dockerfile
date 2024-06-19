FROM registry.access.redhat.com/ubi9/ubi-minimal:9.4

ARG JAVA_PACKAGE=java-21-openjdk-headless
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Install java and nginx
RUN microdnf install ca-certificates ${JAVA_PACKAGE} nginx -y \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/conf/security/java.security

COPY ./docker/wait-for-it.sh /usr/local/bin/wait-for-it.sh
RUN chmod +x /usr/local/bin/wait-for-it.sh
COPY ./docker/entrypoint.sh /deployments/entrypoint.sh
RUN chmod +x /deployments/entrypoint.sh

COPY ../shop/target/shop-0.0.7-SNAPSHOT-runner.jar /deployments/shop.jar
COPY ../pim/target/pim-0.0.7-SNAPSHOT-runner.jar /deployments/pim.jar
COPY ../cart/target/cart-0.0.7-SNAPSHOT.jar /deployments/cart.jar
COPY ../checkout/deployment/nginx.conf /etc/nginx/nginx.conf
COPY ../checkout/dist /usr/share/nginx/html

EXPOSE 3000
EXPOSE 8081-8083

CMD [ "/deployments/entrypoint.sh" ]
