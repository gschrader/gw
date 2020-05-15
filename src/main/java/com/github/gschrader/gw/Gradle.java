package com.github.gschrader.gw;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class Gradle implements Function<App, Integer> {
    private static final String GRADLE_BUILDFILE = "build.gradle";
    private static final String GRADLE_KTS_BUILDFILE = "build.gradle.kts";

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private String[] getPaths() {
        return Optional.ofNullable(System.getenv("Path"))
                .orElse(System.getenv("PATH"))
                .split(File.pathSeparator);
    }

    private String getWorkingDir() {
        return Paths.get("").toAbsolutePath().toString();
    }

    private String getGradleExec() {
        if (isWindows()) {
            return "gradle.bat";
        } else {
            return "gradle";
        }
    }

    private String getGradleWExec() {
        if (isWindows()) {
            return "gradlew.bat";
        } else {
            return "gradlew";
        }
    }

    private Optional<String> findGradleExec() {
        String exec = getGradleExec();

        return Stream.of(getPaths())
                .filter(path -> new File(path + File.pathSeparator + exec).exists())
                .findFirst();
    }

    private String findGradleWExec(String dir) {
        return findFile(dir, Set.of(getGradleWExec()));
    }

    private String findGradle() {
        String pwd = getWorkingDir();

        String exec = findGradleWExec(pwd);
        if (exec == null) {
            printNoGradleWNotice();
            return findGradleExec().orElseGet(() -> {
                printNoGradleError();
                return null;
            });
        }

        return exec;
    }

    private String findGradleBuild(String dir) {
        return findFile(dir, Set.of(GRADLE_BUILDFILE, GRADLE_KTS_BUILDFILE));
    }

    private String findFile(String dir, Set<String> names) {
        File current = new File(dir);
        String found = null;

        for (String name : names) {
            File file = new File(current, name);

            if (file.exists()) {
                found = file.getAbsolutePath();
            }
        }

        if (found == null) {
            String parent = current.getParent();
            if (parent != null) {
                return findFile(parent, names);
            }
        }

        return found;
    }

    private void printNoGradleWNotice() {
        System.err.println(String.format("No %s set up for this project; consider setting one up:", getGradleWExec()));
        System.err.println("(http://gradle.org/docs/current/userguide/gradle_wrapper.html)");
    }

    private void printNoGradleError() {
        System.err.println(String.format("%s not installed or not available in your PATH. Please install gradle.", getGradleExec()));
        System.err.println("(http://gradle.org/docs/current/userguide/installation.html)");
    }

    @Override
    public Integer apply(App app) {
        try {
            String gradleExec = findGradle();
            String buildFile = findGradleBuild(getWorkingDir());

            File workingDir;
            if (!app.currentDir) {
                workingDir = new File(buildFile).getParentFile();
            } else {
                workingDir = new File(getWorkingDir());
            }

            if (buildFile == null) {
                System.out.println(String.format("Unable to find a gradle build file named %s or %s.", GRADLE_BUILDFILE, GRADLE_KTS_BUILDFILE));
                return -1;
            }

            if (app.verbose) {
                System.out.println("Using gradle at '" + gradleExec + "' to run buildfile '" + buildFile + " from dir: " + workingDir.getAbsolutePath() + "':");
            }

            Stream<String> command;
            if (isWindows()) {
                command = Stream.of("cmd", "/c", gradleExec);
            } else {
                command = Stream.of(gradleExec);
            }

            ProcessBuilder builder = new ProcessBuilder(Stream.concat(command, Stream.of(app.gradleArgs)).toArray(String[]::new));
            builder.inheritIO();
            builder.directory(workingDir);
            Process process = builder.start();

            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
