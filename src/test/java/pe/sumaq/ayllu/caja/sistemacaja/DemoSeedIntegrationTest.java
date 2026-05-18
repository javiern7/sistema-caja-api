package pe.sumaq.ayllu.caja.sistemacaja;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "demo"})
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class DemoSeedIntegrationTest {

    private static final ApplicationArguments EMPTY_ARGS = new DefaultApplicationArguments(new String[0]);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("seedDemoData")
    private ApplicationRunner demoSeedRunner;

    @Test
    void shouldSeedDemoDataProfileWithOperationalFlowReadyForManualReview() throws Exception {
        demoSeedRunner.run(EMPTY_ARGS);

        String token = loginAsAdmin();
        JsonNode contexts = read(performGet("/api/v1/contextos-operativos", token)).path("data");
        JsonNode products = read(performGet("/api/v1/productos", token)).path("data");
        JsonNode purchases = read(performGet("/api/v1/compras", token)).path("data");
        JsonNode sales = read(performGet("/api/v1/ventas", token)).path("data");
        JsonNode expenses = read(performGet("/api/v1/egresos", token)).path("data");
        JsonNode cashBoxes = read(performGet("/api/v1/cajas", token)).path("data");
        JsonNode salesReport = read(performGet("/api/v1/reportes/ventas", token)).path("data");
        JsonNode history = read(performGet("/api/v1/reportes/historial", token)).path("data");

        assertThat(contexts.isArray()).isTrue();
        assertThat(products.isArray()).isTrue();
        assertThat(purchases.isArray()).isTrue();
        assertThat(sales.isArray()).isTrue();
        assertThat(expenses.isArray()).isTrue();
        assertThat(cashBoxes.isArray()).isTrue();

        assertThat(contexts).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("DEMO-NEG-001"));
        assertThat(products).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("DEMO-PROD-CAFE"));
        assertThat(products).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("DEMO-PROD-SAND"));
        assertThat(purchases).hasSizeGreaterThanOrEqualTo(1);
        assertThat(sales).hasSizeGreaterThanOrEqualTo(1);
        assertThat(expenses).hasSizeGreaterThanOrEqualTo(1);
        assertThat(cashBoxes).anySatisfy(item -> assertThat(item.path("status").asText()).isEqualTo("CERRADA"));
        assertThat(salesReport.path("totalSales").asInt()).isGreaterThanOrEqualTo(1);
        assertThat(history.isArray() ? history.size() : 0).isGreaterThanOrEqualTo(6);
    }

    private String loginAsAdmin() throws Exception {
        String response = performJson(
                post("/api/v1/auth/login"),
                """
                        {
                          "username": "admin",
                          "password": "Admin123*"
                        }
                        """
        );
        return read(response).path("data").path("token").asText();
    }

    private String performGet(String url, String token) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(url)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private String performJson(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder, String body)
            throws Exception {
        MvcResult result = mockMvc.perform(
                        requestBuilder
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private JsonNode read(String response) throws Exception {
        return objectMapper.readTree(response);
    }
}
