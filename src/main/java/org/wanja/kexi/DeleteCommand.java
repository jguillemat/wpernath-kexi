package org.wanja.kexi;

import io.fabric8.openshift.api.model.config.v1.Project;
import io.fabric8.openshift.client.OpenShiftClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "delete", mixinStandardHelpOptions = true, description = "Deletes all of the projects / namespaces listed after the command.")
public class DeleteCommand extends AbstractBaseLister {

    @Parameters
    String[] projectNames;

    @Override
    public void runCommand() {
        if (Boolean.FALSE.equals(client.supports(Project.class))) {
            System.out.println("Target cluster is not an OpenShift cluster.");
            return;
        }

        OpenShiftClient oClient = client.adapt(OpenShiftClient.class);

        for(String p : projectNames ) {
            System.out.println("Deleting project " + p + "...");
            
            oClient.projects().withName(p).delete();
        }
    }
    
}
