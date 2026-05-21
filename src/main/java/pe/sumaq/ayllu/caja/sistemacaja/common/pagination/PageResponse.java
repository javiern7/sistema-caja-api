package pe.sumaq.ayllu.caja.sistemacaja.common.pagination;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        List<String> sort
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSort().stream()
                        .map(PageResponse::formatSortOrder)
                        .toList()
        );
    }

    private static String formatSortOrder(Sort.Order order) {
        return order.getProperty() + "," + order.getDirection().name().toLowerCase();
    }
}
