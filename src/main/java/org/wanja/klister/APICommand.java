package org.wanja.klister;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;

@TopCommand
@Command(
        name="kexi - k8s exporter", 
        description = "Exports k8s resources as yaml or displays a list of existing resources by namespace",        
        mixinStandardHelpOptions = true, 
        subcommands = {
            PodLister.class, 
            ServiceLister.class, 
            DeploymentLister.class
        }
)
public class APICommand extends AbstractBaseLister {

    @Spec
    CommandSpec spec;

    @Override
    public void runCommand() {
        throw new ParameterException(spec.commandLine(), "Please specify a command");
    }

    
}
