FROM flink:1.15.2

ENV CHAOS_MONKEY_JAR=flink-chaos-monkey-java-job-*.jar
ENV JOBS_DIR=/opt/flink/jobs/

RUN mkdir -p $JOBS_DIR && \
    chmod 777 $JOBS_DIR
COPY target/$CHAOS_MONKEY_JAR /opt/flink/jobs/
