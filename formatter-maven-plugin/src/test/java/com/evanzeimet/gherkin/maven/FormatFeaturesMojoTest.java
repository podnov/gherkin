package com.evanzeimet.gherkin.maven;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.Resource;
import org.junit.Before;
import org.junit.Test;

import com.evanzeimet.gherkin.GherkinTestUtils;

public class FormatFeaturesMojoTest {

    private FormatFeaturesMojo mojo;

    @Before
    public void setUp() {
        mojo = new FormatFeaturesMojo();
    }

    @Test
    public void getResourceFeatureFiles_invalidDir() {
        String givenDirectoryPath = "bad-dir";

        Resource givenFeatureResource = mock(Resource.class);
        doReturn(givenDirectoryPath).when(givenFeatureResource).getDirectory();

        WildcardFileFilter givenFileFilter = new WildcardFileFilter(FormatFeaturesMojo.DEFAULT_FILENAME_WILDCARD);

        List<File> actual = mojo.getResourceFeatureFiles(givenFeatureResource, givenFileFilter);

        int actualFileCount = actual.size();
        assertEquals(0, actualFileCount);
    }

    @Test
    public void getResourceFeatureFiles_validDir() {
        String givenRelativePath = "testResources";
        File givenDirectory = getRelativeResource(givenRelativePath);
        String givenDirectoryPath = givenDirectory.getAbsolutePath();

        Resource givenFeatureResource = mock(Resource.class);
        doReturn(givenDirectoryPath).when(givenFeatureResource).getDirectory();

        WildcardFileFilter givenFileFilter = new WildcardFileFilter(FormatFeaturesMojo.DEFAULT_FILENAME_WILDCARD);

        List<File> actual = mojo.getResourceFeatureFiles(givenFeatureResource, givenFileFilter);

        int actualFileCount = actual.size();
        assertEquals(2, actualFileCount);
    }

    protected File getRelativeResource(String givenRelativePath) {
        return GherkinTestUtils.getRelativeResource(getClass(), givenRelativePath);
    }

}
