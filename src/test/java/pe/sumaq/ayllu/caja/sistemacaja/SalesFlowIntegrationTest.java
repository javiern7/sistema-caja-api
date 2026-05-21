package pe.sumaq.ayllu.caja.sistemacaja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
@ActiveProfiles("integration")
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SalesFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCompleteSalesFlowWithRealDatabaseAndExcludeCancelledSaleFromReports() throws Exception {
        String token = loginAsAdmin();
        long operationalContextId = createOperationalContext(token);
        long productId = createProduct(token);
        long providerId = createProvider(token);

        createPurchase(token, operationalContextId, providerId, productId);
        assertThat(fetchCurrentStock(token, productId)).isEqualByComparingTo("10.00");

        long cashBoxId = openCashBox(token, operationalContextId);
        long saleId = createSale(token, operationalContextId, cashBoxId, productId);

        assertThat(fetchCurrentStock(token, productId)).isEqualByComparingTo("8.00");
        assertThat(fetchSalesReportTotal(token)).isEqualTo(1);

        cancelSale(token, saleId);

        assertThat(fetchCurrentStock(token, productId)).isEqualByComparingTo("10.00");
        assertThat(fetchSalesReportTotal(token)).isZero();
        assertThat(fetchReportHistoryCount(token)).isGreaterThanOrEqualTo(2);
    }

    private String loginAsAdmin() throws Exception {
        String response = performJson(
                post("/api/v1/auth/login"),
                null,
                """
                        {
                          "username": "admin",
                          "password": "Admin123*"
                        }
                        """
        );
        return read(response).path("data").path("token").asText();
    }

    private long createOperationalContext(String token) throws Exception {
        String response = performJson(
                post("/api/v1/negocios-eventos"),
                token,
                """
                        {
                          "code": "NEG-IT-001",
                          "name": "Negocio Integracion",
                          "type": "NEGOCIO",
                          "status": "EN_CURSO",
                          "startDate": "%s",
                          "description": "Contexto para prueba de integracion"
                        }
                        """.formatted(LocalDate.now())
        );
        return read(response).path("data").path("id").asLong();
    }

    private long createProduct(String token) throws Exception {
        String response = performJson(
                post("/api/v1/productos"),
                token,
                """
                        {
                          "code": "PROD-IT-001",
                          "name": "Producto Integracion",
                          "unitOfMeasure": "UND",
                          "salePrice": 15.00,
                          "referenceCost": 10.00,
                          "minimumStock": 2.00,
                          "stockControlled": true,
                          "active": true,
                          "description": "Producto para pruebas"
                        }
                        """
        );
        return read(response).path("data").path("id").asLong();
    }

    private long createProvider(String token) throws Exception {
        String response = performJson(
                post("/api/v1/proveedores"),
                token,
                """
                        {
                          "name": "Proveedor Integracion",
                          "documentNumber": "20123456789",
                          "contactName": "QA Backend",
                          "phone": "999999999",
                          "email": "qa@example.com",
                          "active": true
                        }
                        """
        );
        return read(response).path("data").path("id").asLong();
    }

    private void createPurchase(String token, long operationalContextId, long providerId, long productId) throws Exception {
        performJson(
                post("/api/v1/compras"),
                token,
                """
                        {
                          "operationalContextId": %d,
                          "providerId": %d,
                          "purchaseDate": "%s",
                          "documentType": "FACTURA",
                          "documentNumber": "F001-1",
                          "paymentMethod": "EFECTIVO",
                          "items": [
                            {
                              "productId": %d,
                              "quantity": 10.00,
                              "unitCost": 10.00
                            }
                          ],
                          "observation": "Compra para abastecer stock"
                        }
                        """.formatted(operationalContextId, providerId, LocalDate.now(), productId)
        );
    }

    private long openCashBox(String token, long operationalContextId) throws Exception {
        String response = performJson(
                post("/api/v1/cajas/aperturas"),
                token,
                """
                        {
                          "operationalContextId": %d,
                          "openingAmount": 100.00,
                          "observation": "Apertura integracion"
                        }
                        """.formatted(operationalContextId)
        );
        return read(response).path("data").path("id").asLong();
    }

    private long createSale(String token, long operationalContextId, long cashBoxId, long productId) throws Exception {
        String response = performJson(
                post("/api/v1/ventas"),
                token,
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
                          "observation": "Venta integracion"
                        }
                        """.formatted(operationalContextId, cashBoxId, productId)
        );
        return read(response).path("data").path("id").asLong();
    }

    private void cancelSale(String token, long saleId) throws Exception {
        performJson(
                post("/api/v1/ventas/%d/anulacion".formatted(saleId)),
                token,
                """
                        {
                          "reason": "Anulacion de prueba de integracion"
                        }
                        """
        );
    }

    private BigDecimal fetchCurrentStock(String token, long productId) throws Exception {
        String response = performGet("/api/v1/stock", token);
        JsonNode items = read(response).path("data");
        for (JsonNode item : items) {
            if (item.path("productId").asLong() == productId) {
                return new BigDecimal(item.path("currentStock").asText());
            }
        }
        throw new IllegalStateException("No se encontro el producto en el stock actual.");
    }

    private int fetchSalesReportTotal(String token) throws Exception {
        String response = performGet("/api/v1/reportes/ventas", token);
        return read(response).path("data").path("totalSales").asInt();
    }

    private int fetchReportHistoryCount(String token) throws Exception {
        String response = performGet("/api/v1/reportes/historial", token);
        JsonNode items = read(response).path("data");
        return items.path("items").isArray() ? items.path("items").size() : 0;
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

    private String performJson(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder, String token, String body)
            throws Exception {
        if (Objects.nonNull(token)) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

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
