package pe.sumaq.ayllu.caja.sistemacaja.common.pagination;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Component
public class PageableFactory {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public Pageable create(
            Integer page,
            Integer size,
            List<String> sortParams,
            Sort defaultSort,
            Set<String> allowedSorts
    ) {
        int resolvedPage = page == null ? DEFAULT_PAGE : page;
        int resolvedSize = size == null ? DEFAULT_SIZE : size;

        validatePage(resolvedPage);
        validateSize(resolvedSize);

        Sort sort = buildSort(sortParams, defaultSort, allowedSorts);
        return PageRequest.of(resolvedPage, resolvedSize, sort);
    }

    private void validatePage(int page) {
        if (page < 0) {
            throw validationError("El parametro 'page' debe ser mayor o igual a 0.");
        }
    }

    private void validateSize(int size) {
        if (size < 1 || size > MAX_SIZE) {
            throw validationError("El parametro 'size' debe estar entre 1 y " + MAX_SIZE + ".");
        }
    }

    private Sort buildSort(List<String> sortParams, Sort defaultSort, Set<String> allowedSorts) {
        if (sortParams == null || sortParams.isEmpty()) {
            return defaultSort;
        }

        Sort sort = Sort.unsorted();
        for (int index = 0; index < sortParams.size(); index++) {
            String sortParam = sortParams.get(index);
            if (sortParam == null || sortParam.isBlank()) {
                continue;
            }

            String property;
            String direction;
            if (sortParam.contains(",")) {
                String[] parts = sortParam.split(",");
                property = parts[0].trim();
                direction = parts.length > 1 ? parts[1].trim() : "asc";
            } else {
                property = sortParam.trim();
                String nextToken = index + 1 < sortParams.size() ? sortParams.get(index + 1) : null;
                if (nextToken != null && isDirectionToken(nextToken)) {
                    direction = nextToken.trim();
                    index++;
                } else {
                    direction = "asc";
                }
            }

            if (!allowedSorts.contains(property)) {
                throw validationError("El campo de ordenamiento '" + property + "' no esta permitido.");
            }

            Sort.Direction parsedDirection;
            try {
                parsedDirection = Sort.Direction.fromString(direction);
            } catch (IllegalArgumentException exception) {
                throw validationError("La direccion de ordenamiento '" + direction + "' no es valida.");
            }

            sort = sort.and(Sort.by(parsedDirection, property));
        }

        return sort.isSorted() ? sort : defaultSort;
    }

    private boolean isDirectionToken(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalizedValue = value.trim();
        return "asc".equalsIgnoreCase(normalizedValue) || "desc".equalsIgnoreCase(normalizedValue);
    }

    private BusinessException validationError(String message) {
        return new BusinessException(ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, message);
    }
}
