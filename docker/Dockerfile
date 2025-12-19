FROM registry.access.redhat.com/ubi9/ubi-minimal:9.7

ARG VERSION=0.0.10

LABEL org.opencontainers.image.authors="Niko Köbler, https://www.n-k.de"
LABEL org.opencontainers.image.title="Keycloak Bookshop Demo"
LABEL org.opencontainers.image.version="${VERSION}"
LABEL org.opencontainers.image.description="All-in-one demo container"

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Install java and nginx
RUN microdnf install ca-certificates java-21-openjdk-headless nginx -y \
    && microdnf update -y \
    && microdnf clean all -y \
    && mkdir /deployments \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/conf/security/java.security

COPY --chmod=755 ./docker/wait-for-it.sh /usr/local/bin/
COPY --chmod=755 ./docker/entrypoint.sh /deployments/

COPY ../shop/target/shop-${VERSION}-runner.jar /deployments/shop.jar
COPY ../pim/target/pim-${VERSION}-runner.jar /deployments/pim.jar
COPY ../cart/target/cart-${VERSION}.jar /deployments/cart.jar
COPY ../checkout/.tls/ /etc/tls/
COPY ../checkout/deployment/nginx.conf /etc/nginx/nginx.conf
COPY ../checkout/dist /usr/share/nginx/html

EXPOSE 3000
EXPOSE 8081-8083

CMD [ "/deployments/entrypoint.sh" ]
