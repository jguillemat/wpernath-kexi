package org.wanja.kexi;


import java.time.Instant;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.utils.Serialization;
import picocli.CommandLine.Command;

@Command(name = "pods", mixinStandardHelpOptions = true)
public class PodLister extends AbstractBaseLister {
    
    public void runCommand() {

        if( resourceName != null ) {
            Pod p = client.pods().inNamespace(namespace).withName(resourceName).get();
            if( p != null ) {
                p.getMetadata().getManagedFields().clear();
                p.getMetadata().getOwnerReferences().clear();
                p.getMetadata().setResourceVersion(null);
                p.getMetadata().setUid(null);
                p.setStatus(null);
                System.out.println(Serialization.asYaml(p));
            }
        }
        else {
            String fmt = "%-40.38s %-15.15s %-8.8s %-12.12s %-10s";
            PodList l = client.pods().inNamespace(namespace).list();
            System.out.println(String.format(fmt, "POD", "IP", "READY", "STATUS", "AGE"));
            
            for( Pod p : l.getItems() ) {
                String line;
                int numContainers = p.getStatus().getContainerStatuses().size();
                int numReady      = 0;
                for(ContainerStatus pc : p.getStatus().getContainerStatuses()){
                    numReady += pc.getReady().booleanValue() ? 1 : 0;
                }
                line = String.format(fmt, 
                    p.getMetadata().getName(),
                    p.getStatus().getPodIP() != null ? p.getStatus().getPodIP() : "N/A",
                    numReady + "/" + numContainers,
                    p.getStatus().getPhase(),
                    formatAge(Instant.parse(p.getMetadata().getCreationTimestamp()))
                );
                System.out.println(line);
                
            }
        }    
    }
}
