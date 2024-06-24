package org.wanja.kexi;

import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.inject.Inject;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.Spec;

public abstract class AbstractBaseCommand implements Runnable {

    @Option(names = {"--namespace", "-n"}, description="The namespace whose pods should be listed", scope = ScopeType.INHERIT)
    String namespace;

    @Option(names = {"--resource-name", "-r"}, description="The resource you would like to list", scope = ScopeType.INHERIT)
    String resourceName;

    @Option(names = {"--user", "-u"}, description="The username you would like to use for the target cluster" )
    String userName;

    @Option(names = { "--password", "-p" }, description = "The password you would like to use for the target cluster")
    String password;

    @Option(names = { "--token", "-t" }, description = "The OAuth token to be used")
    String token;

    @Option(names = { "--server" }, description = "The master URL of the kubernetes cluster to use")
    String masterURL;

    @Option(names = {"--output", "-o"}, description="Where to write the generated manifest(s) to", converter = PrintStreamConverter.class)
    PrintStream output = System.out;

    @Inject
    KubernetesClient client;

    @Spec
    CommandSpec spec;

    public void run() {
        if( masterURL != null && (userName != null || password != null || token != null) ) {
            if( password == null && token == null ) throw new ParameterException(spec.commandLine(), "You have to provide either username and password or a token to connect to Kubernetes");
            if( token != null && (userName != null || password != null) ) {
                throw new ParameterException(
                    spec.commandLine(),
                    "If you're providing a valid token there is no need to provide username and/or password."
                );
            }
            
            System.out.println("Creating new connection to target kubernetes cluster");
            client = new KubernetesClientBuilder().withConfig(
                new ConfigBuilder()
                    .withMasterUrl(masterURL)
                    .withUsername(userName)
                    .withPassword(password)
                    .withOauthToken(token)
                    .build()
            ).build();
        }

        System.out.println("Using " + client.getMasterUrl() + " - v" + client.getKubernetesVersion().getMajor() + "." + client.getKubernetesVersion().getMinor() + " - " + client.getKubernetesVersion().getPlatform());
        if (namespace == null) {
            namespace = client.getNamespace();
        }

        runCommand();
    }

    protected String formatAge(Instant timeStarted) {
        Duration dur = Duration.between(timeStarted, Instant.now());
        StringBuilder sb = new StringBuilder();
        long days = dur.toDaysPart();
        long hours = dur.toHoursPart();
        long minutes = dur.toMinutesPart();
        long seconds = dur.toSecondsPart();

        if( days != 0 ) {
            sb.append(days + "d");
        }
        if( hours != 0 ) {
            sb.append(hours + "h");
        }
        if( minutes != 0 ) {
            sb.append(minutes + "m");
        }
        if( seconds != 0 ) {
            sb.append(seconds + "s");
        }
        return sb.toString();
        
    }

    protected void cleanMeta(ObjectMeta meta) {
        meta.getManagedFields().clear();
        meta.getOwnerReferences().clear();
        meta.setResourceVersion(null);
        meta.setUid(null);
        meta.setCreationTimestamp(null);
        meta.setGeneration(null);
    }
    
    public abstract void runCommand();
}
