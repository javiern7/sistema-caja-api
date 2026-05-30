package pe.sumaq.ayllu.caja.sistemacaja.common.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Service
public class OperationalDataResetService {

    private static final ApplicationArguments EMPTY_ARGS = new DefaultApplicationArguments(new String[0]);

    private static final String RESET_OPERATIONAL_DATA_SQL = """
            TRUNCATE TABLE
                report_history,
                audit_operations,
                sale_payments,
                sale_items,
                sales,
                cash_movements,
                cash_boxes,
                purchase_items,
                purchases,
                expenses,
                stock_movements,
                stock_current,
                products,
                providers,
                operational_contexts
            RESTART IDENTITY CASCADE
            """;

    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;
    private final ApplicationContext applicationContext;

    public OperationalDataResetService(
            ObjectProvider<DataSource> dataSourceProvider,
            Environment environment,
            ApplicationContext applicationContext
    ) {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        this.jdbcTemplate = dataSource == null ? null : new JdbcTemplate(dataSource);
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    @Transactional
    @PreAuthorize("hasAuthority('usuario.gestionar')")
    public ResetResult resetAndReseed() {
        if (jdbcTemplate == null) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_OPERATION,
                    HttpStatus.FORBIDDEN,
                    "El reinicio operativo no se encuentra disponible sin una base de datos activa."
            );
        }

        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        boolean supportedProfile = activeProfiles.contains("local")
                || activeProfiles.contains("qa")
                || activeProfiles.contains("demo");

        if (!supportedProfile) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN_OPERATION,
                    HttpStatus.FORBIDDEN,
                    "El reinicio operativo solo esta habilitado para perfiles locales, qa o demo."
            );
        }

        jdbcTemplate.execute(RESET_OPERATIONAL_DATA_SQL);

        List<String> appliedSeeds = new ArrayList<>();
        if (activeProfiles.contains("qa")) {
            runSeedIfPresent("seedQaData");
            appliedSeeds.add("qa");
        }
        if (activeProfiles.contains("demo")) {
            runSeedIfPresent("seedDemoData");
            appliedSeeds.add("demo");
        }

        if (appliedSeeds.isEmpty()) {
            appliedSeeds.add("local-clean");
        }

        return new ResetResult(activeProfiles, appliedSeeds);
    }

    private void runSeedIfPresent(String beanName) {
        if (!applicationContext.containsBean(beanName)) {
            return;
        }

        ApplicationRunner runner = applicationContext.getBean(beanName, ApplicationRunner.class);
        try {
            runner.run(EMPTY_ARGS);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo reejecutar la semilla operativa " + beanName + ".", exception);
        }
    }

    public record ResetResult(
            List<String> activeProfiles,
            List<String> appliedSeeds
    ) {
    }
}
