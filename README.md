# flink-chaos-monkey-java-job

Flink chaos monkey job for k8s operator.

## How to build
In order to build the project one needs maven and java.
Please make sure to set Flink version in
* `pom.xml` file with `flink.version` parameter
* `Dockerfile` file with `FROM` parameter
* `chaos-monkey.yaml` file with `flinkVersion` parameter
```
mvn clean install

# Build the docker image into minikube
eval $(minikube docker-env)
docker build -t chaos-monkey:latest .
```

## How to prepare minikube
```
minikube ssh
mkdir -p /tmp/flink
chmod 777 /tmp/flink
```

## How to deploy
```
kubectl apply -f chaos-monkey.yaml
```

## How to delete
```
kubectl delete -f chaos-monkey.yaml
```

## How to make chaos
Chaos type | Chaos node  | Action needed
-----------|-------------|--------------
Throw exception in UDF| TaskManager | Create local file `/tmp/throwExceptionInUDF`
