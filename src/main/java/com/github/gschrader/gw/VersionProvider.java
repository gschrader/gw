package com.github.gschrader.gw;

import picocli.CommandLine.IVersionProvider;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VersionProvider implements IVersionProvider {
    public String[] getVersion() {
        try {
            URL url = getClass().getResource("/version.txt");
            String version = new Scanner(url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();

            return new String[]{version};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}