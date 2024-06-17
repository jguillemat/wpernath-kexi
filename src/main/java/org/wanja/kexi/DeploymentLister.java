package org.wanja.kexi;


import java.time.Instant;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.utils.Serialization;
import picocli.CommandLine.Command;

@Command(name = "deployments", mixinStandardHelpOptions = true)
public class DeploymentLister extends AbstractBaseLister {

    @Override
    public void runCommand() {
        if( resourceName != null ) {
            Deployment svc = client.apps()
                .deployments()
                .inNamespace(namespace)
                .withName(resourceName)
                .get();
            if( svc != null ) {
                cleanMeta(svc.getMetadata());
                svc.setStatus(null);
                System.out.println(Serialization.asYaml(
                    svc
                ));
            }
        }
        else {
            String fmt = "%-40.38s %-8.8s %-12.12s %-12.12s %-10s";
            System.out.println(String.format(fmt, "NAME", "READY", "UP-TO-DATE", "AVAIL", "AGE"));

            for( Deployment d : client.apps().deployments().inNamespace(namespace).list().getItems()) {                
                System.out.println(
                    String.format(fmt, 
                        d.getMetadata().getName(),
                        d.getStatus().getReadyReplicas() + "/" + d.getStatus().getReplicas(),
                        d.getStatus().getUpdatedReplicas(),
                        d.getStatus().getAvailableReplicas(),
                        formatAge(Instant.parse(d.getMetadata().getCreationTimestamp()))
                    )
                );                
            }
            System.out.println();
        }
        
    }
    
}
