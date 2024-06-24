package org.wanja.kexi;

import io.fabric8.openshift.client.OpenShiftClient;
import picocli.CommandLine.Command;

import java.util.List;

import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.config.v1.Project;

@Command(
    name = "routes", 
    mixinStandardHelpOptions = true,
    description = "Lists all routes in the given namespace. Replacement for oc get routes"
)
public class RouteLister extends AbstractBaseCommand {

    @Override
    public void runCommand() {
        if (Boolean.FALSE.equals(client.supports(Project.class))) {
            System.out.println("Target cluster is not an OpenShift cluster.");
            return;
        }
        OpenShiftClient oClient = client.adapt(OpenShiftClient.class);

        if( resourceName != null ) {
            Route r = oClient.routes().inNamespace(namespace).withName(resourceName).get();
            if( r != null ) {
                cleanMeta(r.getMetadata());
                r.setStatus(null);
                output.println(Serialization.asYaml(r));
            }
        }
        else {
            List<Route> routes =  oClient.routes().inNamespace(namespace).list().getItems();
            
            String fmt = "%-25.25s %-50.50s %-15.15s %-25.25s %-10s";
            System.out.println(String.format(fmt, "NAME", "HOST/PORT", "PORT", "SERVICE", "TERMINATION"));
        
            for( Route r : routes) {
                System.out.println(String.format(fmt, 
                    r.getMetadata().getName(),
                    r.getSpec().getHost() + " / " + r.getSpec().getPort().getTargetPort().getStrVal(),
                    r.getSpec().getPort().getTargetPort().getStrVal(),
                    r.getSpec().getTo().getKind() + "/" + r.getSpec().getTo().getName(),
                    r.getSpec().getTls() != null ? r.getSpec().getTls().getTermination() : "None"
                ));
            }
        }
        oClient.close();
    }
    
    
}
