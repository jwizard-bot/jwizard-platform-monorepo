FROM dpage/pgadmin4:latest

USER root

ENV MOUNTPOINT="/tmp/mountpoint.pgpass"
ENV STARTUP_SCRIPT="/usr/local/bin/entrypoint-wrapper"

RUN touch $MOUNTPOINT && chmod 600 $MOUNTPOINT

COPY entrypoint-wrapper $STARTUP_SCRIPT
RUN chmod +x $STARTUP_SCRIPT

USER pgadmin
