package com.ontic.framework.db.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.ontic.framework.config.Config;
import com.ontic.framework.config.ConfigService;
import com.ontic.perf.aspect.Track;
import com.ontic.perf.tracker.Perf;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * A dirty impl of ES client
 *
 * @author rajesh
 * @since 28/02/25 20:58
 */
@Service
public class ESServiceImpl implements ESService {

    private final ConfigService configService;

    @Autowired
    public ESServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    @Track
    public void createIndex(String indexName) {
        try(Perf ignore = Perf.inDB("ES", "A_"+indexName)) {
            try (RestClientTransport transport = createClient()) {
                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                esClient.indices().create(c -> c.index(indexName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Track
    public void index(String index, String id, Object obj) {
        try(Perf ignore = Perf.inDB("ES", "S_"+index)) {
            try (RestClientTransport transport = createClient()) {
                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                esClient.index(i -> i.index(index).id(id).document(obj));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Track
    public <T> T read(String index, String id, Class<T> clazz) {
        try(Perf ignore = Perf.inDB("ES", "R_"+index)) {
            try (RestClientTransport transport = createClient()) {
                ElasticsearchClient esClient = new ElasticsearchClient(transport);
                GetResponse<T> response = esClient.get(g -> g.index(index).id(id), clazz);
                return response.source();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Track
    private RestClientTransport createClient() {
        Config es = configService.getConfig("ES");

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(es.get("user"), es.get("password")));
        RestClient rest = RestClient.builder(HttpHost.create(es.get("url")))
                .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider)).build();
        return new RestClientTransport(rest, new JacksonJsonpMapper());
    }
}
