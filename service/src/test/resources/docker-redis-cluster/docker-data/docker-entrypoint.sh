#!/bin/sh

DEFAULT_IP="0.0.0.0"
DEFAULT_PORTS="30000:30005"

if [ "$1" = 'redis-cluster' ]; then
    # Allow passing in cluster IP by environmental variable
    IP=$(echo "${IP:-${DEFAULT_IP}}")
    echo "Using IP ${IP}"

    if [ "${IP}" != "${DEFAULT_IP}" ]; then
      CLUSTER="yes"
    else
      echo "================================================================================"
      echo "If you want to have a fully functional Redis Cluster, please inform the host IP."
      echo "================================================================================"
    fi

    # Default to ports 30000-30006
    PORTS="${PORTS:-${DEFAULT_PORTS}}"
    PORT_MIN=$(echo $PORTS | cut -f 1 -d ":")
    PORT_MAX=$(echo $PORTS | cut -f 2 -d ":")
    echo "From ports ${PORT_MIN} to ${PORT_MAX}"

    # + 10000 - redis bus port interval from the data port
    CLUSTER_PORT=$((PORT_MIN + 10000))

    for port in `seq ${PORT_MIN} ${PORT_MAX}`; do

      # Use docker to get host and bus ports
      hport=$(docker port $(hostname) ${port} | cut -f2 -d":")
      bport=$(docker port $(hostname) ${CLUSTER_PORT} | cut -f2 -d":")

      echo "Docker mapped container port ${port} to host port ${hport}"
      echo "Docker mapped bus port ${CLUSTER_PORT} to host port ${bport}"

      mkdir -p /redis-conf/${port}
      mkdir -p /redis-data/${port}

      if [ -e /redis-data/${port}/nodes.conf ]; then
        rm /redis-data/${port}/nodes.conf
      fi

      if [ -n "${CLUSTER}" ]; then

        # When a different IP than 0.0.0.0 is used, the host and bus ports are written to the configuration
        # file to be announced as cluster-announce-port and cluster-announce-bus-port
        # It is expected that this IP is the host's IP
        PORT=${port} IP=${IP} HPORT=${hport} BPORT=${bport} envsubst < /redis-conf/redis-cluster.tmpl > /redis-conf/${port}/redis.conf
      else

        # Don't announce port and bus ports when no host IP is used
        PORT=${port} HPORT=${hport} envsubst < /redis-conf/redis.tmpl > /redis-conf/${port}/redis.conf
      fi
      cat /redis-conf/${port}/redis.conf

      REPLICA_PARAM="${REPLICA_PARAM} 0.0.0.0:${port}"
      CONTAINER_PORTS=$(echo "${CONTAINER_PORTS} ${port}")

      CLUSTER_PORT=$(($CLUSTER_PORT + 1))
    done

    bash /generate-supervisor-conf.sh ${CONTAINER_PORTS} > /etc/supervisor/supervisord.conf

    supervisord -c /etc/supervisor/supervisord.conf
    sleep 3

    REDIS_TRIP_CMD="echo \"yes\" | ruby /redis-trib.rb create --password OxygeniumTest123 --replicas 1 0 0 ${REPLICA_PARAM}"
    eval ${REDIS_TRIP_CMD}
    tail -f /var/log/supervisor/redis*.log
else
  exec "$@"
fi