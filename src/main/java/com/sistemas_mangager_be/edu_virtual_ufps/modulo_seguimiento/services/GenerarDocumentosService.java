package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ActasInfoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Proyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.Sustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.EstadoProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoDocumento;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.SustentacionEvaluador;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionEvaluadorRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.SustentacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.repositories.UsuarioProyectoRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GenerarDocumentosService {

    private final DocumentoService documentoService;
    private final UsuarioProyectoRepository usuarioProyectoRepository;
    private final SustentacionEvaluadorRepository sustentacionEvaluadorRepository;
    private final SustentacionRepository sustentacionRepository;

    public GenerarDocumentosService(DocumentoService documentoService,
                                    UsuarioProyectoRepository usuarioProyectoRepository,
                                    SustentacionEvaluadorRepository sustentacionEvaluadorRepository,
                                    SustentacionRepository sustentacionRepository) {
        this.documentoService = documentoService;
        this.usuarioProyectoRepository = usuarioProyectoRepository;
        this.sustentacionEvaluadorRepository = sustentacionEvaluadorRepository;
        this.sustentacionRepository = sustentacionRepository;
    }

    public void generarDocumentosSegunEstado(Proyecto proyecto) {
        ActasInfoDto actasInfoDto = new ActasInfoDto();

        List<UsuarioProyecto> usuariosProyecto = usuarioProyectoRepository.findByIdProyecto(proyecto.getId());
        String estudianteNombre = null, estudianteCodigo = null, estudianteCorreo = null, estudianteTelefono = null;
        String director = null, codirector = null;

        for (UsuarioProyecto up : usuariosProyecto) {
            String rol = up.getRol().getNombre().toLowerCase();
            Usuario usuario = up.getUsuario();
            switch (rol) {
                case "estudiante":
                    estudianteNombre = usuario.getNombreCompleto();
                    estudianteCodigo = usuario.getCodigo();
                    estudianteCorreo = usuario.getEmail();
                    estudianteTelefono = usuario.getTelefono();
                    break;
                case "director":
                    director = usuario.getNombreCompleto();
                    break;
                case "codirector":
                    codirector = usuario.getNombreCompleto();
                    break;
            }
        }

        Optional<Sustentacion> sustentacionOpt = sustentacionRepository.findByProyectoIdAndOptionalTipoSustentacion(proyecto.getId(), TipoSustentacion.TESIS);
        String fecha = null, hora = null, sala = null;
        String descripcionSustentacion = null;
        List<SustentacionEvaluador> juradosProyecto = Collections.emptyList();

        if (sustentacionOpt.isPresent()) {
            Sustentacion sustentacion = sustentacionOpt.get();
            fecha = sustentacion.getFecha().format(DateTimeFormatter.ofPattern("MMMM dd 'de' yyyy"));
            hora = sustentacion.getHora().toString();
            sala = sustentacion.getLugar();
            descripcionSustentacion = sustentacion.getDescripcion();
            juradosProyecto = sustentacionEvaluadorRepository.findByIdSustentacion(sustentacion.getId());
        }
        String juradoInternoNombre = null, juradoExternoNombre = null;
        for (SustentacionEvaluador se : juradosProyecto) {
            if (se.isJuradoExterno()) {
                juradoInternoNombre = se.getUsuario().getNombreCompleto();
            } else {
                juradoExternoNombre = se.getUsuario().getNombreCompleto();
            }
        }

        actasInfoDto.setFecha(fecha != null ? fecha : "Fecha por definir");//Formato <<Mes>> <<Dia>> de <<Año>>
        actasInfoDto.setHora(hora != null ? hora : "Hora por definir");
        actasInfoDto.setSala(sala != null ? sala : "Sala por definir");
        actasInfoDto.setDescripcionSustentacion(descripcionSustentacion != null ? descripcionSustentacion : "Sin descripcion");
        actasInfoDto.setTituloProyecto(proyecto.getTitulo());
        actasInfoDto.setEstudiante(estudianteNombre != null ? estudianteNombre : "Estudiante");
        actasInfoDto.setCodigo(estudianteCodigo != null ? estudianteCodigo : "Codigo del estudiante");
        actasInfoDto.setDirector(director != null ? director : "Nombre Director");
        actasInfoDto.setCodirector(codirector != null ? codirector : "Nombre Codirector");
        actasInfoDto.setJuradoInterno(juradoInternoNombre != null ? juradoInternoNombre : "Jurado Interno");
        actasInfoDto.setJuradoExterno(juradoExternoNombre != null ? juradoExternoNombre : "Jurado Externo");
        actasInfoDto.setObservaciones("Sin observaciones");

        actasInfoDto.setCorreo(estudianteCorreo != null ? estudianteCorreo : "Correo del estudiante");
        actasInfoDto.setTelefono(estudianteTelefono != null ? estudianteTelefono : "Telefono del estudiante");

        LocalDate hoy = LocalDate.now();
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        actasInfoDto.setDia(String.valueOf(hoy.getDayOfMonth()));
        actasInfoDto.setMes(meses[hoy.getMonthValue() - 1]);
        actasInfoDto.setYear(String.valueOf(hoy.getYear()));


        EstadoProyecto estado = proyecto.getEstadoActual();

        switch (estado) {
            case FASE_2:
                generarSolicitudEvaluacionAnteproyecto(actasInfoDto, proyecto.getId());
                break;
            case FASE_3:
                generarCartaAprobacionAnteproyecto(actasInfoDto, proyecto.getId());
                break;
            case FASE_7:
                generarActaVistoBueno(actasInfoDto, proyecto.getId());
                break;
            case FASE_8:
                generarActaBorradorOFinal(true, "docx", actasInfoDto, proyecto.getId());
                break;
            case FASE_9:
                generarActaBorradorOFinal(false, "docx", actasInfoDto, proyecto.getId());
                break;
            default:
                break;
        }
    }

    public boolean generarSolicitudEvaluacionAnteproyecto(ActasInfoDto actasInfo, Integer idProyecto){
        try {
            ClassPathResource plantillaActa;
            plantillaActa = new ClassPathResource("static/plantillas/SOLICITUD-EVALUACION-PROPUESTA.docx");

            String nombreTemporal = UUID.randomUUID() + "_acta-solicitud-evaluacion-propuesta.docx";
            String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            Map<String, String> campos = Map.of(
                    "<<Proyecto>>", actasInfo.getTituloProyecto(),
                    "<<Estudiante>>", actasInfo.getEstudiante(),
                    "<<Correo>>", actasInfo.getCorreo(),
                    "<<Telefono>>", actasInfo.getTelefono(),
                    "<<Dia>>", actasInfo.getDia(),
                    "<<Mes>>", actasInfo.getMes(),
                    "<<Año>>", actasInfo.getYear()
            );

            try (InputStream is = plantillaActa.getInputStream();
                 XWPFDocument doc = new XWPFDocument(is);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                for (XWPFParagraph p : doc.getParagraphs()) {
                    reemplazarCamposEnParrafo(p, campos);
                }
                for (XWPFTable tabla : doc.getTables()) {
                    for (XWPFTableRow fila : tabla.getRows()) {
                        for (XWPFTableCell celda : fila.getTableCells()) {
                            for (XWPFParagraph p : celda.getParagraphs()) {
                                reemplazarCamposEnParrafo(p, campos);
                            }
                        }
                    }
                }

                doc.write(baos);
                byte[] contenido = baos.toByteArray();
                documentoService.guardarDocumentoGenerado(
                        idProyecto,
                        nombreTemporal,
                        contentType,
                        contenido,
                        TipoDocumento.ACTASOLICITUD,
                        "SolicitudEvaluacion"
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generarCartaAprobacionAnteproyecto(ActasInfoDto actasInfo, Integer idProyecto){
        try {
            ClassPathResource plantillaActa;
            plantillaActa = new ClassPathResource("static/plantillas/APROBACIÓN-ANTEPROYECTO-MAESTRÍA-TIC.docx");

            String nombreTemporal = UUID.randomUUID() + "_acta-aprobacion-propuesta.docx";
            String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            Map<String, String> campos = Map.of(
                    "<<Proyecto>>", actasInfo.getTituloProyecto(),
                    "<<Estudiante>>", actasInfo.getEstudiante(),
                    "<<Docente>>", actasInfo.getDirector(),
                    "<<Dia>>", actasInfo.getDia(),
                    "<<Mes>>", actasInfo.getMes(),
                    "<<Año>>", actasInfo.getYear()
            );

            try (InputStream is = plantillaActa.getInputStream();
                 XWPFDocument doc = new XWPFDocument(is);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                for (XWPFParagraph p : doc.getParagraphs()) {
                    reemplazarCamposEnParrafo(p, campos);
                }
                for (XWPFTable tabla : doc.getTables()) {
                    for (XWPFTableRow fila : tabla.getRows()) {
                        for (XWPFTableCell celda : fila.getTableCells()) {
                            for (XWPFParagraph p : celda.getParagraphs()) {
                                reemplazarCamposEnParrafo(p, campos);
                            }
                        }
                    }
                }

                doc.write(baos);
                byte[] contenido = baos.toByteArray();
                documentoService.guardarDocumentoGenerado(
                        idProyecto,
                        nombreTemporal,
                        contentType,
                        contenido,
                        TipoDocumento.ACTAAPROBACION,
                        "AprobacionAnteproyecto"
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generarActaVistoBueno(ActasInfoDto actasInfo, Integer idProyecto) {
        try {
            ClassPathResource plantillaActa;
            plantillaActa = new ClassPathResource("static/plantillas/VISTO-BUENO-ANTEPROYECTO.docx");

            String nombreTemporal = UUID.randomUUID() + "_acta-vb.docx";
            String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            Map<String, String> campos = Map.of(
                    "<<Proyecto>>", actasInfo.getTituloProyecto(),
                    "<<Estudiante>>", actasInfo.getEstudiante(),
                    "<<Director>>", actasInfo.getDirector(),
                    "<<Codirector>>", actasInfo.getCodirector()
            );

            try (InputStream is = plantillaActa.getInputStream();
                 XWPFDocument doc = new XWPFDocument(is);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                for (XWPFParagraph p : doc.getParagraphs()) {
                    reemplazarCamposEnParrafo(p, campos);
                }
                for (XWPFTable tabla : doc.getTables()) {
                    for (XWPFTableRow fila : tabla.getRows()) {
                        for (XWPFTableCell celda : fila.getTableCells()) {
                            for (XWPFParagraph p : celda.getParagraphs()) {
                                reemplazarCamposEnParrafo(p, campos);
                            }
                        }
                    }
                }

                doc.write(baos);
                byte[] contenido = baos.toByteArray();
                documentoService.guardarDocumentoGenerado(
                        idProyecto,
                        nombreTemporal,
                        contentType,
                        contenido,
                        TipoDocumento.ACTAVB,
                        "VistoBueno"
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean generarActaBorradorOFinal(boolean borrador, String formato, ActasInfoDto actasInfo, Integer idProyecto){
        try {
            if (formato.equalsIgnoreCase("pdf")){
                ClassPathResource plantillaActa;
                if (borrador) {
                    plantillaActa = new ClassPathResource("static/plantillas/actaBorrador.xhtml");
                } else {
                    plantillaActa = new ClassPathResource("static/plantillas/actaFinal.xhtml");
                }
                InputStream inputStream = plantillaActa.getInputStream();
                String plantilla = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                String htmlCompleto = plantilla
                        .replace("&lt;&lt;Mes&gt;&gt;", actasInfo.getMes())
                        .replace("&lt;&lt;Día&gt;&gt;", actasInfo.getDia())
                        .replace("&lt;&lt;Año&gt;&gt;", actasInfo.getYear())
                        .replace("&lt;&lt;Hora&gt;&gt;", actasInfo.getHora())
                        .replace("&lt;&lt;Sala&gt;&gt;", actasInfo.getSala())
                        .replace("&lt;&lt;Proyecto&gt;&gt;", actasInfo.getTituloProyecto())
                        .replace("&lt;&lt;Director&gt;&gt;", actasInfo.getDirector())
                        .replace("&lt;&lt;Codirector&gt;&gt;", actasInfo.getCodirector())
                        .replace("&lt;&lt;Estudiante&gt;&gt;", actasInfo.getEstudiante())
                        .replace("&lt;&lt;Código&gt;&gt;", actasInfo.getCodigo())
                        .replace("&lt;&lt;Jurado Interno&gt;&gt;", actasInfo.getJuradoInterno())
                        .replace("&lt;&lt;Jurado Externo&gt;&gt;", actasInfo.getJuradoExterno())
                        .replace("&lt;&lt;Observaciones&gt;&gt;", actasInfo.getObservaciones());

                String pdfPath = "pdfGenerado.pdf";
                try (OutputStream outputStream = new FileOutputStream(pdfPath)) {
                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocumentFromString(htmlCompleto);
                    renderer.layout();
                    renderer.createPDF(outputStream);
                }
                return true;
            } else if (formato.equalsIgnoreCase("docx")) {
                ClassPathResource plantillaActa;
                String nombreTemporal;
                String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                if (borrador) {
                    plantillaActa = new ClassPathResource("static/plantillas/ACTA-BORRADOR.docx");
                    nombreTemporal = UUID.randomUUID() + "_acta-borrador.docx";
                } else {
                    plantillaActa = new ClassPathResource("static/plantillas/ACTA-ORIGINAL.docx");
                    nombreTemporal = UUID.randomUUID() + "_acta-original.docx";
                }

                Map<String, String> campos = Map.of(
                        "<<Fecha>>", actasInfo.getFecha(),
                        "<<Hora>>", actasInfo.getHora(),
                        "<<Sala>>", actasInfo.getSala(),
                        "<<Proyecto>>", actasInfo.getTituloProyecto(),
                        "<<Estudiante>>", actasInfo.getEstudiante(),
                        "<<Codigo>>", actasInfo.getCodigo(),
                        "<<Director>>", actasInfo.getDirector(),
                        "<<Codirector>>", actasInfo.getCodirector(),
                        "<<Jurado Interno>>", actasInfo.getJuradoInterno(),
                        "<<Jurado Externo>>", actasInfo.getJuradoExterno()
                );

                try (InputStream is = plantillaActa.getInputStream();
                     XWPFDocument doc = new XWPFDocument(is);
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    for (XWPFParagraph p : doc.getParagraphs()) {
                        reemplazarCamposEnParrafo(p, campos);
                    }
                    for (XWPFTable tabla : doc.getTables()) {
                        for (XWPFTableRow fila : tabla.getRows()) {
                            for (XWPFTableCell celda : fila.getTableCells()) {
                                for (XWPFParagraph p : celda.getParagraphs()) {
                                    reemplazarCamposEnParrafo(p, campos);
                                }
                            }
                        }
                    }

                    doc.write(baos);
                    byte[] contenido = baos.toByteArray();

                    TipoDocumento tipoDocumento = borrador ? TipoDocumento.ACTABORRADOR : TipoDocumento.ACTAORIGINAL;
                    String tag = borrador ? "ActaBorrador" : "ActaOriginal";

                    documentoService.guardarDocumentoGenerado(
                            idProyecto,
                            nombreTemporal,
                            contentType,
                            contenido,
                            tipoDocumento,
                            tag
                    );
                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void reemplazarCamposEnParrafo(XWPFParagraph parrafo, Map<String, String> campos) {
        for (XWPFRun run : parrafo.getRuns()) {
            String texto = run.getText(0);
            if (texto != null) {
                for (Map.Entry<String, String> entry : campos.entrySet()) {
                    if (texto.contains(entry.getKey())) {
                        texto = texto.replace(entry.getKey(), entry.getValue());
                        run.setText(texto, 0);
                    }
                }
            }
        }
    }
}
