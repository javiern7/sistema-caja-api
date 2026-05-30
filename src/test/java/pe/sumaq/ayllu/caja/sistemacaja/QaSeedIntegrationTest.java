package pe.sumaq.ayllu.caja.sistemacaja;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@ActiveProfiles({"integration", "qa"})
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class QaSeedIntegrationTest {

    private static final ApplicationArguments EMPTY_ARGS = new DefaultApplicationArguments(new String[0]);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("seedQaData")
    private ApplicationRunner qaSeedRunner;

    @Test
    void shouldSeedQaDataWithMinimalOperationalBaseAndNoConsumedCashFlow() throws Exception {
        qaSeedRunner.run(EMPTY_ARGS);

        String token = loginAsAdmin();
        JsonNode contexts = read(performGet("/api/v1/contextos-operativos", token)).path("data");
        JsonNode products = read(performGet("/api/v1/productos", token)).path("data").path("items");
        JsonNode providers = read(performGet("/api/v1/proveedores", token)).path("data").path("items");
        long operationalContextId = findOperationalContextId(token, "QA-BASE-001");
        JsonNode currentStock = read(performGet("/api/v1/stock?operationalContextId=" + operationalContextId, token)).path("data").path("items");
        JsonNode purchases = read(performGet("/api/v1/compras", token)).path("data").path("items");
        JsonNode sales = read(performGet("/api/v1/ventas", token)).path("data").path("items");
        JsonNode expenses = read(performGet("/api/v1/egresos", token)).path("data").path("items");
        JsonNode cashBoxes = read(performGet("/api/v1/cajas", token)).path("data").path("items");

        assertThat(contexts.isArray()).isTrue();
        assertThat(products.isArray()).isTrue();
        assertThat(providers.isArray()).isTrue();
        assertThat(currentStock.isArray()).isTrue();
        assertThat(purchases.isArray()).isTrue();
        assertThat(sales.isArray()).isTrue();
        assertThat(expenses.isArray()).isTrue();
        assertThat(cashBoxes.isArray()).isTrue();

        assertThat(contexts).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("QA-BASE-001"));
        assertThat(products).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("PROD-QA-001"));
        assertThat(providers).anySatisfy(item -> assertThat(item.path("documentNumber").asText()).isEqualTo("20990000001"));
        assertThat(currentStock).anySatisfy(item -> {
            assertThat(item.path("productCode").asText()).isEqualTo("PROD-QA-001");
            assertThat(item.path("currentStock").decimalValue()).isEqualByComparingTo("10.00");
        });
        assertThat(purchases).hasSize(1);
        assertThat(sales).isEmpty();
        assertThat(expenses).isEmpty();
        assertThat(cashBoxes).isEmpty();
    }

    @Test
    void shouldResetOperationalDataAndReseedQaBase() throws Exception {
        qaSeedRunner.run(EMPTY_ARGS);

        String token = loginAsAdmin();
        long operationalContextId = findOperationalContextId(token, "QA-BASE-001");
        long productId = findProductId(token, "PROD-QA-001");
        long cashBoxId = openCashBox(token, operationalContextId);
        createSale(token, operationalContextId, cashBoxId, productId);

        JsonNode salesBeforeReset = read(performGet("/api/v1/ventas", token)).path("data").path("items");
        assertThat(salesBeforeReset).hasSize(1);

        JsonNode resetResult = read(performJson(
                post("/api/v1/system/operational-data/reset").header(HttpHeaders.AUTHORIZATION, "Bearer " + token),
                ""
        )).path("data");
        assertThat(resetResult.path("appliedSeeds")).anySatisfy(item -> assertThat(item.asText()).isEqualTo("qa"));

        JsonNode contextsAfterReset = read(performGet("/api/v1/contextos-operativos", token)).path("data");
        JsonNode stockAfterReset = read(performGet("/api/v1/stock?operationalContextId=" + operationalContextId, token)).path("data").path("items");
        JsonNode salesAfterReset = read(performGet("/api/v1/ventas", token)).path("data").path("items");
        JsonNode cashBoxesAfterReset = read(performGet("/api/v1/cajas", token)).path("data").path("items");

        assertThat(contextsAfterReset).anySatisfy(item -> assertThat(item.path("code").asText()).isEqualTo("QA-BASE-001"));
        assertThat(stockAfterReset).anySatisfy(item -> {
            assertThat(item.path("productCode").asText()).isEqualTo("PROD-QA-001");
            assertThat(item.path("currentStock").decimalValue()).isEqualByComparingTo("10.00");
        });
        assertThat(salesAfterReset).isEmpty();
        assertThat(cashBoxesAfterReset).isEmpty();
    }

    @Test
    void shouldFilterStockReportByOperationalContext() throws Exception {
        qaSeedRunner.run(EMPTY_ARGS);

        String token = loginAsAdmin();
        long operationalContextId = findOperationalContextId(token, "QA-BASE-001");
        JsonNode stockReport = read(performGet("/api/v1/reportes/stock?operationalContextId=" + operationalContextId, token)).path("data");

        assertThat(stockReport.path("stockScope").asText()).isEqualTo("OPERATIONAL_CONTEXT");
        assertThat(stockReport.path("requestedOperationalContextId").asLong()).isEqualTo(operationalContextId);
        assertThat(stockReport.path("operationalContextFilterApplied").asBoolean()).isTrue();
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

    private long findOperationalContextId(String token, String code) throws Exception {
        JsonNode contexts = read(performGet("/api/v1/contextos-operativos", token)).path("data");
        for (JsonNode item : contexts) {
            if (code.equals(item.path("code").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new IllegalStateException("No se encontro el contexto operativo esperado.");
    }

    private long findProductId(String token, String code) throws Exception {
        JsonNode products = read(performGet("/api/v1/productos", token)).path("data").path("items");
        for (JsonNode item : products) {
            if (code.equals(item.path("code").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new IllegalStateException("No se encontro el producto esperado.");
    }

    private long openCashBox(String token, long operationalContextId) throws Exception {
        String response = performJson(
                post("/api/v1/cajas/aperturas").header(HttpHeaders.AUTHORIZATION, "Bearer " + token),
                """
                        {
                          "operationalContextId": %d,
                          "openingAmount": 100.00,
                          "observation": "Apertura QA para reseed"
                        }
                        """.formatted(operationalContextId)
        );
        return read(response).path("data").path("id").asLong();
    }

    private void createSale(String token, long operationalContextId, long cashBoxId, long productId) throws Exception {
        performJson(
                post("/api/v1/ventas").header(HttpHeaders.AUTHORIZATION, "Bearer " + token),
                """
                        {
                          "operationalContextId": %d,
                          "cashBoxId": %d,
                          "items": [
                            {
                              "productId": %d,
                              "quantity": 2.00,
                              "unitPrice": 15.00
                            }
                          ],
                          "payments": [
                            {
                              "paymentMethod": "EFECTIVO",
                              "amount": 30.00
                            }
                          ],
                          "observation": "Venta QA antes de reseed"
                        }
                        """.formatted(operationalContextId, cashBoxId, productId)
        );
    }
}
