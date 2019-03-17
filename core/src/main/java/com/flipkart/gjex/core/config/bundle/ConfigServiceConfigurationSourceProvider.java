package com.flipkart.gjex.core.config.bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.config.ConfigurationSourceProvider;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.flattener.PrintMode;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ConfigServiceConfigurationSourceProvider implements ConfigurationSourceProvider {

    private static final String CONFIG_SVC_URI_SCHEME = "config-svc";
    private static final int CONFIG_SVC_API_VERSION = 1;

    private final ObjectMapper jsonObjectMapper;
    private final ObjectMapper ymlObjectMapper;

    public ConfigServiceConfigurationSourceProvider(ObjectMapper jsonObjectMapper, ObjectMapper ymlObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.ymlObjectMapper = ymlObjectMapper;
    }

    @Override
    public InputStream open(String path) throws IOException {
        URI uri = URI.create(path);
        if (CONFIG_SVC_URI_SCHEME.equals(uri.getScheme())) {
            // sample path: "config-svc://10.47.0.101:80/<bucket_name>"
            return getInputStreamFromConfigService(uri);
        } else if (path.endsWith(".yml") || path.endsWith(".yaml")) {
            return getInputStreamFromYml(path);
        } else {
            throw new RuntimeException("Invalid path.");
        }
    }

    private InputStream getInputStreamFromConfigService(final URI uri) throws IOException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
        URL url;
        try {
            String fullPath = String.format("/v%d/buckets%s", CONFIG_SVC_API_VERSION, uri.getPath());
            URI httpURI = new URI("http", uri.getAuthority(), fullPath, null, null);
            url = httpURI.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException("This won't happen");
        }
        JsonNode jsonNode = jsonObjectMapper.readValue(url, JsonNode.class);
        return IOUtils.toInputStream(jsonNode.get("keys").toString(), Charset.defaultCharset());
    }

    private InputStream getInputStreamFromYml(String ymlPath) throws IOException {
        // Flatten this yaml into string using JSON_FLATTEN_SEPARATOR as separator.
        // Return inputStream of bytes of this flattened json.
        FileReader reader = new FileReader(ymlPath);
        JsonNode node = ymlObjectMapper.readTree(reader);
        String flattenedJson = new JsonFlattener(jsonObjectMapper.writeValueAsString(node))
                .withSeparator(ConfigServiceBundle.JSON_FLATTEN_SEPARATOR)
                .withPrintMode(PrintMode.PRETTY)
                .flatten();
        return new ByteArrayInputStream(flattenedJson.getBytes(StandardCharsets.UTF_8));
    }


}
