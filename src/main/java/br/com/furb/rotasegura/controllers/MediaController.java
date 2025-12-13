package br.com.furb.rotasegura.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import br.com.furb.rotasegura.domain.records.MediaRecord;
import br.com.furb.rotasegura.services.MediaService;
import br.com.furb.rotasegura.services.impl.MediaData;

@RequestMapping("/v1/media")
@RestController
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping
    public ResponseEntity<List<MediaRecord>> list(@RequestParam String occurrenceId) {
        return ResponseEntity.ok(mediaService.list(UUID.fromString(occurrenceId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaRecord> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mediaService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaRecord> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(required = false) UUID occurrenceId) throws IOException {
        return ResponseEntity.ok(mediaService.create(occurrenceId, file.getContentType(), file.getBytes()));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) {
        MediaData media = mediaService.load(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.getMediaType()))
                .body(media.getImageData());
    }
}
