package com.evanzeimet.gherkin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class GherkinTestUtils {

    protected static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    @SuppressWarnings("deprecation")
    public static ObjectWriter createObjectWriter() {
        ObjectMapper objectMapper = createObjectMapper();

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(new DefaultPrettyPrinter.Lf2SpacesIndenter());

        return objectMapper.writer(prettyPrinter);
    }

    public static String dosToUnix(String dos) {
        String result;

        if (dos == null) {
            result = null;
        } else {
            result = dos.replaceAll("\\r\\n", "\n");
        }

        return result;
    }

    public static File getRelativeResource(Class<?> clazz, String relativePath) {
        String packageName = clazz.getPackage().getName();
        String packagePath = packageName.replaceAll("\\.", "/");
        String wholePath = packagePath + "/" + relativePath;

        URL url = Thread.currentThread().getContextClassLoader().getResource(wholePath);

        if (url == null) {
            String message = String.format("File [%s] not found", wholePath);
            throw new IllegalArgumentException(message);
        }

        return new File(url.getPath());
    }

    public static <T> T objectify(String json, Class<T> clazz) throws JsonParseException,
            JsonMappingException,
            IOException {
        ObjectMapper objectMapper = createObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    public static String readFile(File file) {
        String result;

        try {
            result = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return result;
    }

    public static String readRelativeResource(Class<?> clazz, String relativePath) {
        File file = getRelativeResource(clazz, relativePath);
        return readFile(file);
    }

    public static String stringify(Object object) throws JsonProcessingException {
        ObjectWriter objectWriter = createObjectWriter();
        return objectWriter.writeValueAsString(object);
    }
}
