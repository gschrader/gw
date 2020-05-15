package com.github.gschrader.gw;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Command(name = "gw", mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        description = "gw invokes gradlew on projects where one is configured, and falls back to use the gradle from the $PATH if a wrapper is not available")
class App implements Callable<Integer> {
    @Option(names = "--gw", description = "arguments to the right are passed to gw command rather than to gradle or gradlew process")
    private boolean gw;
    @Option(names = "-c", description = "use current dir instead of project directory")
    boolean currentDir;

    @Option(names = "-v", description = "verbose")
    boolean verbose;

    String[] gradleArgs;

    private static boolean isGwArg(String arg) {
        return arg.equals("--gw");
    }

    public static void main(String... args) {
        String[] gradleArgs = Stream.of(args)
                .takeWhile(Predicate.not(App::isGwArg))
                .toArray(String[]::new);

        String[] appArgs = Stream.of(args)
                .dropWhile(Predicate.not(App::isGwArg))
                .toArray(String[]::new);

        App app = new App();
        app.gradleArgs = gradleArgs;
        CommandLine commandLine = new CommandLine(app);
        commandLine.parseArgs(appArgs);

        if (gradleArgs.length > 0) {
            System.exit(new Gradle().apply(app));
        } else {
            int exitCode = commandLine.execute(appArgs);

            if (!commandLine.isUsageHelpRequested() && appArgs.length == 1) {
                commandLine.usage(System.out);
            }

            System.exit(exitCode);
        }
    }

    @Override
    public Integer call() {
        return 0;
    }
}