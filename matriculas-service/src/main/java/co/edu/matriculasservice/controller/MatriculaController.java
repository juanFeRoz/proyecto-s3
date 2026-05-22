package co.edu.matriculasservice.controller;

import co.edu.matriculasservice.api.ApiResponse;
import co.edu.matriculasservice.api.ResponseBuilder;
import co.edu.matriculasservice.dto.MatriculaCreateDTO;
import co.edu.matriculasservice.dto.MatriculaDTO;
import co.edu.matriculasservice.handler.MatriculaHandler;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaHandler handler;

    public MatriculaController(MatriculaHandler handler) {
        this.handler = handler;
    }

    @GetMapping
    public ApiResponse<List<MatriculaDTO>> listar() {
        return ResponseBuilder.success("Consulta exitosa", handler.listar());
    }

    @GetMapping("/{id}")
    public ApiResponse<MatriculaDTO> buscarPorId(
            @Parameter(description = "Id de la matrícula", required = true) @PathVariable("id") Long id) {
        return ResponseBuilder.success("Consulta exitosa", handler.buscarPorId(id));
    }

    @PostMapping
    public ApiResponse<MatriculaDTO> registrar(@Valid @RequestBody MatriculaCreateDTO dto) {
        return ResponseBuilder.success("Matrícula registrada", handler.registrar(dto));
    }

    @PutMapping("/{id}/anular")
    public ApiResponse<MatriculaDTO> anular(
            @Parameter(description = "Id de la matrícula", required = true) @PathVariable("id") Long id) {
        return ResponseBuilder.success("Matrícula anulada", handler.anular(id));
    }
}
