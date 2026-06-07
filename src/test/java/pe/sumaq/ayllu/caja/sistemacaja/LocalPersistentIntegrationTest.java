package pe.sumaq.ayllu.caja.sistemacaja;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.CreateOperationalContextUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.CreateOperationalContextRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local-persistent"})
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-integration.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LocalPersistentIntegrationTest {

    private static final String PERSISTENT_CONTEXT_CODE = "PERSIST-TEST-001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateOperationalContextUseCase createOperationalContextUseCase;

    @Autowired
    private JpaOperationalContextRepository operationalContextRepository;

    @Test
    void shouldKeepOperationalDataAndRejectResetInLocalPersistentProfile() throws Exception {
        createOperationalContextUseCase.execute(new CreateOperationalContextRequest(
                PERSISTENT_CONTEXT_CODE,
                "Contexto persistente de prueba",
                OperationalContextType.NEGOCIO,
                OperationalContextStatus.EN_CURSO,
                LocalDate.now(),
                null,
                "Contexto creado para validar persistencia local"
        ));

        assertThat(operationalContextRepository.findByCode(PERSISTENT_CONTEXT_CODE)).isPresent();

        String token = loginAsAdmin();
        String response = perform(
                post("/api/v1/system/operational-data/reset")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token),
                "",
                HttpStatus.FORBIDDEN.value()
        );

        JsonNode payload = read(response);
        JsonNode error = payload.path("error");
        assertThat(error.path("code").asText()).isEqualTo("FORBIDDEN_OPERATION");
        assertThat(payload.path("message").asText()).contains("deshabilitado");
        assertThat(operationalContextRepository.findByCode(PERSISTENT_CONTEXT_CODE)).isPresent();
    }

    @Test
    void shouldNotSeedOperationalBaseAutomaticallyInLocalPersistentProfile() throws Exception {
        String token = loginAsAdmin();
        JsonNode contexts = read(perform(get("/api/v1/contextos-operativos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token), null)).path("data");

        assertThat(contexts.isArray()).isTrue();
        assertThat(contexts).isEmpty();
    }

    private String loginAsAdmin() throws Exception {
        String response = perform(post("/api/v1/auth/login"), """
                {
                  "username": "admin",
                  "password": "Admin123*"
                }
                """);
        return read(response).path("data").path("token").asText();
    }

    private String perform(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder, String body)
            throws Exception {
        return perform(requestBuilder, body, HttpStatus.OK.value());
    }

    private String perform(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder,
            String body,
            int expectedStatus
    )
            throws Exception {
        MvcResult result = mockMvc.perform(
                        requestBuilder
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(body == null ? "" : body)
                )
                .andExpect(status().is(expectedStatus))
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private JsonNode read(String response) throws Exception {
        return objectMapper.readTree(response);
    }
}
