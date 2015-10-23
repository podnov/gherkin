package com.evanzeimet.gherkin.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.evanzeimet.gherkin.formatter.GherkinFileFormatter;
import com.evanzeimet.gherkin.formatter.GherkinFormatterException;

@Mojo(name = "format-features")
public class FormatFeaturesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.testResources}")
    private List<Resource> featureResources;

    @Parameter(defaultValue = "*.feature")
    private String filenameWildcard;

    @Override
    public void execute() throws MojoExecutionException,
            MojoFailureException {
        List<File> featureFiles = new ArrayList<File>();
        WildcardFileFilter fileFilter = new WildcardFileFilter(filenameWildcard);

        for (Resource featureResource : featureResources) {
            List<File> resourceFeatureFiles = getResourceFeatureFiles(featureResource, fileFilter);
            featureFiles.addAll(resourceFeatureFiles);
        }

        formatFiles(featureFiles);
    }

    protected void formatFiles(List<File> files) throws MojoExecutionException {
        GherkinFileFormatter fileFormatter = new GherkinFileFormatter();

        for (File file : files) {
            try {
                fileFormatter.formatFile(file);
            } catch (GherkinFormatterException e) {
                String filename = file.getAbsolutePath();
                String message = String.format("Could not format [%s]", filename);
                throw new MojoExecutionException(message, e);
            }
        }
    }

    protected List<File> getResourceFeatureFiles(Resource featureResource,
            WildcardFileFilter fileFilter) {
        List<File> result = new ArrayList<File>();

        String directoryPath = featureResource.getDirectory();
        File directory = new File(directoryPath);

        Iterator<File> files = FileUtils.iterateFiles(directory,
                fileFilter,
                TrueFileFilter.INSTANCE);

        while (files.hasNext()) {
            File file = files.next();
            result.add(file);
        }

        return result;
    }

}
