package org.wanja.kexi;

import java.time.Duration;
import java.time.Instant;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.inject.Inject;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

public abstract class AbstractBaseLister implements Runnable {

    @Option(names = {"--namespace", "-n"}, description="The namespace whose pods should be listed", defaultValue = "default", scope = ScopeType.INHERIT)
    String namespace;

    @Option(names = {"--resource-name", "-r"}, description="The resource you would like to list", scope = ScopeType.INHERIT)
    String resourceName;

    @Inject
    KubernetesClient client;

    public void run() {
        System.out.println("Using " + client.getMasterUrl() + " - " + client.getNamespace());
        System.out.println("  " + client.getKubernetesVersion().getPlatform());
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
