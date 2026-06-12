package pe.sumaq.ayllu.caja.sistemacaja.common.presentation;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BinaryFileResponseFactory {

    public ResponseEntity<byte[]> attachment(String fileName, MediaType mediaType, byte[] content) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(mediaType)
                .body(content);
    }
}
