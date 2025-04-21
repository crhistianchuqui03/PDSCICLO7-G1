package pe.edu.upeu.sysalmacen.control;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudinary.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.sysalmacen.dtos.report.ProdMasVendidosDTO;
import pe.edu.upeu.sysalmacen.excepciones.ReporteException;
import pe.edu.upeu.sysalmacen.modelo.MediaFile;
import pe.edu.upeu.sysalmacen.servicio.IMediaFileService;
import pe.edu.upeu.sysalmacen.servicio.IProductoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reporte")
public class ReportController {

    private final IProductoService productoService;
    private final IMediaFileService mfService;
    private final Cloudinary cloudinary;

    @GetMapping("/pmvendidos")
    public List<ProdMasVendidosDTO> getProductosMasVendidos() {
        return productoService.obtenerProductosMasVendidos();
    }

    @GetMapping(value = "/generateReport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateReport() {
        try {
            byte[] data = productoService.generateReport();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            throw new ReporteException("Error al generar el reporte", e);
        }
    }

    @GetMapping(value = "/readFile/{idFile}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> readFile(@PathVariable("idFile") Long idFile) {
        try {
            byte[] data = mfService.findById(idFile).getContent();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            throw new ReporteException("Error al leer el archivo con ID: " + idFile, e);
        }
    }

    @PostMapping(value = "/saveFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            MediaFile mf = new MediaFile();
            mf.setContent(multipartFile.getBytes());
            mf.setFileName(multipartFile.getOriginalFilename());
            mf.setFileType(multipartFile.getContentType());
            mfService.save(mf);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ReporteException("Error al guardar el archivo", e);
        }
    }

    @PostMapping(value = "/saveFileCloud", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFileCloud(@RequestParam("file") MultipartFile multipartFile) {
        try {
            File f = this.convertToFile(multipartFile);
            Map<String, Object> response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
            JSONObject json = new JSONObject(response);
            String url = json.getString("url");

            log.info("URL del archivo subido: {}", url);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ReporteException("Error al subir archivo a Cloudinary", e);
        }
    }

    private File convertToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new ReporteException("Error al convertir MultipartFile a File", e);
        }
        return file;
    }
}
