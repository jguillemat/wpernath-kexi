# kexi

kexi is a tool to export all manifest files of an app deployed on Kubernetes / OpenShift based on a label selector. 

```bash
$ mvn clean package
$ java -jar ./target/quarkus-run.jar app -n <namespace> -s app=cat-server -o ./test.yaml
```

kexi was mainly created for testing kubernetes-client extension and to create a Quarkus command line app with piccocli extension.


