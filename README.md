# kexi

kexi is a tool to export all manifest files of an app deployed on Kubernetes / OpenShift based on a label selector. 

It also exports all used ConfigMaps and Secrets which are *mounted* to a deployment.

kexi was mainly created for testing kubernetes-client extension and to create a Quarkus command line app with piccocli extension. And to test out Gradle as build tool.

## Building
To build kexi, use the embedded gradle by issuing the following command:

```bash
$ ./gradlew build
```

You can of course also build a native executable if you have current (GraalVM-ce installed)[https://quarkus.io/guides/building-native-image] by issuing the following command:

```bash
$ ./gradlew build -Dquarkus.native.enabled=true
```

## Executing
```bash
$ java -jar ./build/quarkus-app/quarkus-run.jar app [-n <namespace>] -s app=cat-server -o ./test.yaml
```


