package org.wanja.kexi;

import java.io.PrintStream;
import java.util.List;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvFromSource;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.config.v1.Project;
import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.logging.Log;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "app", 
    mixinStandardHelpOptions = true,
    description="Tries to export a whole app bundle based on the selector label=value"
)
public class AppExporter extends AbstractBaseCommand {

    @Option(names = {"--output", "-o"}, description="Where to write the generated manifests to", converter = PrintStreamConverter.class)
    PrintStream output = System.out;

    @Option(names = {"--selector", "-s"}, description="The label selector to use to scan for resources. It's in the form of label=value", defaultValue="app=my-app", required=true)
    String selector;
    String label;
    String value;

    @Override 
    public void runCommand() {
        if (Boolean.FALSE.equals(client.supports(Project.class))) {
            System.out.println("Target cluster is not an OpenShift cluster.");
            return;
        }

        OpenShiftClient oClient = client.adapt(OpenShiftClient.class);

        if( selector != null && !selector.isEmpty() && selector.contains("=") ) {

            label = selector.substring(0, selector.indexOf('='));
            value = selector.substring(selector.indexOf('=')+1);

            Log.info("Looking for resources with label=" + label + " and value=" + value);

            List<Deployment> dl = client.apps()
                .deployments()
                .inNamespace(namespace)
                .withLabel(label, value)
                .list()
                .getItems();
            
            List<Service> sl = client.services()
                .inNamespace(namespace)
                .withLabel(label, value)
                .list()
                .getItems();

            List<Route> rl = oClient.routes()
                .inNamespace(namespace)
                .withLabel(label, value)
                .list()
                .getItems();

            for( Deployment d : dl ) {
               cleanMeta(d.getMetadata());
               d.setStatus(null);
               output.print(Serialization.asYaml(d));

               // looking for any configMapRefs and secretRefs
               List<Container> containers = d.getSpec().getTemplate().getSpec().getContainers();
               for( Container c : containers ) {
                    if( c.getEnvFrom() != null && !c.getEnvFrom().isEmpty()) {
                        Log.info("Scanning container " + c.getName() );
                        for( EnvFromSource e : c.getEnvFrom()) {
                            if( e.getConfigMapRef() != null ) {
                                Log.info("Exporting ConfigMap " + e.getConfigMapRef().getName());
                                
                                ConfigMap cm = client.configMaps().inNamespace(namespace).withName(e.getConfigMapRef().getName()).get();
                                cleanMeta(cm.getMetadata());
                                output.println(Serialization.asYaml(cm));
                            }
                            if( e.getSecretRef() != null ) {
                                Log.info("Exporting Secret " + e.getSecretRef().getName());
                                
                                Secret s = client.secrets().inNamespace(namespace).withName(e.getSecretRef().getName()).get();
                                cleanMeta(s.getMetadata());
                                output.println(Serialization.asYaml(s));
                            }
                        }
                   }
               }
            }    

            for( Service s : sl) {
                cleanMeta(s.getMetadata());
                s.getSpec().setClusterIP(null);
                s.getSpec().setClusterIPs(null);
                s.setStatus(null);
                output.print(Serialization.asYaml(s));
            }

            for( Route r : rl ) {
                cleanMeta(r.getMetadata());
                r.setStatus(null);
                output.print(Serialization.asYaml(r));
            }
        }
    }
    
    
}
