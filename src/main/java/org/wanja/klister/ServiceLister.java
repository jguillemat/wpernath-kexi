package org.wanja.klister;

import java.time.Instant;
import java.util.List;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.utils.Serialization;
import picocli.CommandLine.Command;

@Command(name = "services", mixinStandardHelpOptions = true)
public class ServiceLister extends AbstractBaseLister {

    public void runCommand() {
        if( resourceName != null ) {
            Service svc = client.services().inNamespace(namespace)
                .withName(resourceName)
                .get();
        
            if( svc != null ) {
                svc.getMetadata().getManagedFields().clear();
                svc.getMetadata().getOwnerReferences().clear();
                svc.getMetadata().setResourceVersion(null);
                svc.getMetadata().setUid(null);
                svc.getSpec().setClusterIP(null);
                svc.getSpec().setClusterIPs(null);
                svc.setStatus(null);
                System.out.println(Serialization.asYaml(
                    svc
                ));
            }
        }
        else {
            ServiceList l = client.services().inNamespace(namespace).list();
            List<Service> svc = l.getItems();
            String fmt = "%-40.38s %-12.12s %-15.15s %-12.12s %-10s";

            System.out.println(String.format(fmt, "NAME", "TYPE", "CLUSTER-IP", "PORT", "AGE"));
            for( Service p : svc ) {
                
                System.out.println(
                    String.format(fmt, 
                        p.getMetadata().getName(),
                        p.getSpec().getType(),
                        p.getSpec().getClusterIP(),
                        p.getSpec().getPorts().get(0).getName(),
                        formatAge(Instant.parse(p.getMetadata().getCreationTimestamp()))
                    )                           
                );
            }
            System.out.println();
        }
    }
    
}
