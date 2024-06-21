package org.wanja.kexi;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

@TopCommand
@Command(
        name="kexi", 
        description = "Exports k8s resources as yaml or displays a list of existing resources by namespace",        
        mixinStandardHelpOptions = true, 
        subcommands = {
            PodLister.class, 
            ServiceLister.class, 
            DeploymentLister.class,
            RouteLister.class,
            AppExporter.class,
            DeleteCommand.class
        }
)
public class APICommand implements Runnable {

    @Option(names = {"--user", "-u"}, description="The username you would like to use for the target cluster" )
    String userName;

    @Option(names = { "--password", "-p" }, description = "The password you would like to use for the target cluster")
    String password;

    @Option(names = { "--token", "-t" }, description = "The OAuth token to be used")
    String token;

    @Option(names = { "--server" }, description = "The master URL of the kubernetes cluster to use")
    String masterURL;

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Please specify a command");
    }

    
}
