import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-documentos',
  template: `
    <div class="page-header">
      <h4><i class="bi bi-file-earmark-text"></i> Documentos Electrónicos</h4>
      <div>
        <button class="btn btn-success btn-sm rounded-pill me-2" (click)="exportarCsv()">
          <i class="bi bi-file-earmark-excel"></i> Exportar CSV
        </button>
        <button class="btn btn-primary btn-sm rounded-pill" (click)="mostrarFormulario = true">
          <i class="bi bi-upload"></i> Enviar XML
        </button>
        <button class="btn btn-outline-primary btn-sm rounded-pill ms-2" (click)="cargarDocumentos()">
          <i class="bi bi-arrow-clockwise"></i>
        </button>
      </div>
    </div>

    <!-- Formulario envío -->
    <div class="card mb-4" *ngIf="mostrarFormulario">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span><i class="bi bi-cloud-upload text-primary"></i> Enviar Documento Electrónico</span>
        <button class="btn btn-sm btn-outline-secondary rounded-circle" (click)="mostrarFormulario = false">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      <div class="card-body">
        <!-- Tabs: Pegar XML / Subir archivo -->
        <ul class="nav nav-pills mb-4">
          <li class="nav-item">
            <a class="nav-link" [class.active]="modoInput === 'texto'" (click)="modoInput = 'texto'" style="cursor:pointer">
              <i class="bi bi-code-slash"></i> Pegar XML
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" [class.active]="modoInput === 'archivo'" (click)="modoInput = 'archivo'" style="cursor:pointer">
              <i class="bi bi-upload"></i> Subir archivo
            </a>
          </li>
        </ul>

        <!-- Modo texto -->
        <div *ngIf="modoInput === 'texto'" class="mb-3">
          <label class="form-label fw-semibold">XML del comprobante</label>
          <textarea class="form-control xml-input" rows="12" [(ngModel)]="xmlInput"
            (ngModelChange)="onXmlChange($event)"
            placeholder="Pega aquí el XML del comprobante electrónico..."></textarea>
        </div>

        <!-- Modo archivo -->
        <div *ngIf="modoInput === 'archivo'" class="mb-3">
          <label class="form-label fw-semibold">Seleccionar archivo XML</label>
          <div class="upload-zone" (click)="fileInput.click()" (dragover)="onDragOver($event)"
            (dragleave)="arrastrando = false" (drop)="onDrop($event)"
            [class.dragging]="arrastrando">
            <input #fileInput type="file" accept=".xml" (change)="onFileSelected($event)" style="display:none">
            <div *ngIf="!nombreArchivo" class="text-center py-4">
              <i class="bi bi-cloud-arrow-up" style="font-size: 3rem; color: #4361ee;"></i>
              <p class="mt-2 mb-1">Arrastra un archivo XML aquí</p>
              <small class="text-muted">o haz clic para seleccionar</small>
            </div>
            <div *ngIf="nombreArchivo" class="text-center py-3">
              <i class="bi bi-file-earmark-check text-success" style="font-size: 2rem;"></i>
              <p class="mt-2 mb-1 fw-semibold">{{ nombreArchivo }}</p>
              <small class="text-muted">{{ tamanoArchivo }}</small>
              <button class="btn btn-sm btn-outline-danger mt-2" (click)="limpiarArchivo(); $event.stopPropagation()">
                <i class="bi bi-trash"></i> Quitar
              </button>
            </div>
          </div>
        </div>

        <!-- Resultado validación -->
        <div *ngIf="resultadoValidacion" class="alert" [ngClass]="resultadoValidacion.valido ? 'alert-success' : 'alert-warning'">
          <div class="d-flex align-items-start gap-2">
            <i class="bi" [ngClass]="resultadoValidacion.valido ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'"
              style="font-size: 1.2rem;"></i>
            <div class="flex-grow-1">
              <strong>{{ resultadoValidacion.valido ? 'XML Válido' : 'XML con observaciones' }}</strong>
              <div *ngIf="resultadoValidacion.tipoDetectado" class="mt-1">
                <span class="badge bg-info">Tipo detectado: {{ resultadoValidacion.tipoDetectado }}</span>
              </div>
              <div *ngIf="resultadoValidacion.errores?.length > 0" class="mt-2">
                <div *ngFor="let err of resultadoValidacion.errores" class="small">
                  <i class="bi bi-x-circle text-danger"></i> {{ err }}
                </div>
              </div>
              <div *ngIf="resultadoValidacion.warnings?.length > 0" class="mt-2">
                <div *ngFor="let warn of resultadoValidacion.warnings" class="small">
                  <i class="bi bi-exclamation-circle text-warning"></i> {{ warn }}
                </div>
              </div>
              <div *ngIf="resultadoValidacion.campos" class="mt-2 small">
                <span class="text-muted">RUC:</span> {{ resultadoValidacion.campos.ruc }}
                <span class="text-muted ms-2">Clave:</span> {{ resultadoValidacion.campos.claveAcceso | slice:0:25 }}...
                <span class="text-muted ms-2">Ambiente:</span> {{ resultadoValidacion.campos.ambiente === '2' ? 'PRODUCCIÓN' : 'PRUEBAS' }}
              </div>
            </div>
          </div>
        </div>

        <!-- Tipo documento -->
        <div class="mb-3">
          <label class="form-label fw-semibold">Tipo de documento</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-file-earmark"></i></span>
            <select class="form-select" [(ngModel)]="tipoDocumentoSeleccionado">
              <option value="auto">Auto-detectar desde XML</option>
              <option value="FACTURA">Factura</option>
              <option value="RETENCION">Retención</option>
              <option value="NOTA_CREDITO">Nota Crédito</option>
              <option value="NOTA_DEBITO">Nota Débito</option>
              <option value="LIQUIDACION_COMPRA">Liquidación Compra</option>
              <option value="GUIA_REMISION">Guía Remisión</option>
            </select>
          </div>
        </div>

        <div class="d-flex gap-2">
          <button class="btn btn-outline-primary" (click)="validarXml()" [disabled]="!xmlInput.trim()">
            <i class="bi bi-check2-circle me-1"></i> Validar XML
          </button>
          <button class="btn btn-primary" (click)="enviarDocumento()" [disabled]="enviando || !xmlInput.trim()">
            <span *ngIf="enviando" class="spinner-border spinner-border-sm me-1"></span>
            <i *ngIf="!enviando" class="bi bi-send me-1"></i>
            {{ enviando ? 'Procesando...' : 'Enviar al SRI' }}
          </button>
          <button class="btn btn-outline-secondary" (click)="mostrarFormulario = false">Cancelar</button>
        </div>
        <div *ngIf="resultadoEnvio" class="alert mt-3" [ngClass]="resultadoEnvio.exito ? 'alert-success' : 'alert-danger'">
          <i class="bi" [ngClass]="resultadoEnvio.exito ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'"></i>
          {{ resultadoEnvio.mensaje }}
        </div>
      </div>
    </div>

    <!-- Búsqueda y Filtros -->
    <div class="card mb-4">
      <div class="card-body py-3">
        <div class="row g-3 align-items-end">
          <div class="col-md-4">
            <label class="form-label small text-muted">Búsqueda</label>
            <div class="input-group input-group-sm">
              <span class="input-group-text"><i class="bi bi-search"></i></span>
              <input type="text" class="form-control" [(ngModel)]="busqueda"
                placeholder="Clave acceso, UUID, Nro autorización..."
                (keyup.enter)="cargarDocumentos()">
            </div>
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Tipo</label>
            <select class="form-select form-select-sm" [(ngModel)]="filtroTipo" (change)="cargarDocumentos()">
              <option value="">Todos</option>
              <option value="FACTURA">Factura</option>
              <option value="RETENCION">Retención</option>
              <option value="NOTA_CREDITO">Nota Crédito</option>
              <option value="NOTA_DEBITO">Nota Débito</option>
              <option value="LIQUIDACION_COMPRA">Liq. Compra</option>
              <option value="GUIA_REMISION">Guía Remisión</option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Estado</label>
            <select class="form-select form-select-sm" [(ngModel)]="filtroEstado" (change)="cargarDocumentos()">
              <option value="">Todos</option>
              <option value="AUTORIZADO">Autorizado</option>
              <option value="PENDIENTE_AUTORIZACION">Pendiente</option>
              <option value="ERROR">Error</option>
              <option value="NO_AUTORIZADO">No Autorizado</option>
            </select>
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-secondary btn-sm w-100" (click)="limpiarFiltros()">
              <i class="bi bi-x-circle"></i> Limpiar
            </button>
          </div>
          <div class="col-md-2">
            <button class="btn btn-primary btn-sm w-100" (click)="cargarDocumentos()">
              <i class="bi bi-search"></i> Buscar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Paginación superior -->
    <div class="d-flex justify-content-between align-items-center mb-3" *ngIf="totalElementos > 0">
      <small class="text-muted">
        Mostrando {{ documentos.length }} de {{ totalElementos }} documentos
        (Página {{ paginaActual + 1 }} de {{ totalPaginas }})
      </small>
      <div class="btn-group btn-group-sm">
        <button class="btn btn-outline-secondary" [disabled]="paginaActual === 0" (click)="paginaAnterior()">
          <i class="bi bi-chevron-left"></i>
        </button>
        <button class="btn btn-outline-secondary" [disabled]="paginaActual >= totalPaginas - 1" (click)="paginaSiguiente()">
          <i class="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- Lista documentos -->
    <div class="table-modern">
      <table class="table table-hover">
        <thead>
          <tr>
            <th><i class="bi bi-hash"></i> UUID</th>
            <th><i class="bi bi-file"></i> Tipo</th>
            <th><i class="bi bi-flag"></i> Estado</th>
            <th><i class="bi bi-key"></i> Clave Acceso</th>
            <th><i class="bi bi-123"></i> Nro Autorización</th>
            <th><i class="bi bi-calendar"></i> Fecha</th>
            <th><i class="bi bi-gear"></i> Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let doc of documentos">
            <td><code class="text-muted small">{{ doc.uuid | slice:0:8 }}...</code></td>
            <td><span class="badge bg-secondary">{{ doc.tipoDocumento }}</span></td>
            <td>
              <span class="badge" [ngClass]="getBadgeClass(doc.estado)">
                <i class="bi me-1" [ngClass]="{
                  'bi-check-circle': doc.estado === 'AUTORIZADO',
                  'bi-clock': doc.estado === 'PENDIENTE_AUTORIZACION',
                  'bi-x-circle': doc.estado === 'ERROR',
                  'bi-info-circle': doc.estado !== 'AUTORIZADO' && doc.estado !== 'PENDIENTE_AUTORIZACION' && doc.estado !== 'ERROR'
                }"></i>
                {{ doc.estado }}
              </span>
            </td>
            <td><code *ngIf="doc.claveAcceso" class="text-muted small">{{ doc.claveAcceso | slice:0:20 }}...</code></td>
            <td><span *ngIf="doc.numeroAutorizacion" class="fw-semibold">{{ doc.numeroAutorizacion }}</span></td>
            <td><small class="text-muted">{{ doc.fechaRecepcion }}</small></td>
            <td>
              <div class="btn-group btn-group-sm">
                <button class="btn btn-outline-primary" title="Ver detalle" (click)="verDetalle(doc)">
                  <i class="bi bi-eye"></i>
                </button>
                <button class="btn btn-outline-success" title="Descargar RIDE" (click)="descargarRide(doc)"
                  [disabled]="doc.estado !== 'AUTORIZADO' && doc.estado !== 'FINALIZADO'">
                  <i class="bi bi-file-earmark-pdf"></i>
                </button>
                <button class="btn btn-outline-info" title="Descargar XML" (click)="descargarXml(doc)"
                  [disabled]="doc.estado !== 'AUTORIZADO' && doc.estado !== 'FINALIZADO'">
                  <i class="bi bi-file-earmark-code"></i>
                </button>
                <button class="btn btn-outline-secondary" title="Descargar ZIP" (click)="descargarZip(doc)"
                  [disabled]="doc.estado !== 'AUTORIZADO' && doc.estado !== 'FINALIZADO'">
                  <i class="bi bi-file-earmark-zip"></i>
                </button>
              </div>
            </td>
          </tr>
          <tr *ngIf="documentos.length === 0">
            <td colspan="7" class="text-center text-muted py-5">
              <i class="bi bi-inbox" style="font-size: 2.5rem;"></i>
              <p class="mt-2 mb-0">No se encontraron documentos</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Paginación inferior -->
    <div class="d-flex justify-content-center mt-3" *ngIf="totalPaginas > 1">
      <nav>
        <ul class="pagination pagination-sm">
          <li class="page-item" [class.disabled]="paginaActual === 0">
            <a class="page-link" (click)="irAPagina(0)"><i class="bi bi-chevrons-left"></i></a>
          </li>
          <li class="page-item" [class.disabled]="paginaActual === 0">
            <a class="page-link" (click)="paginaAnterior()"><i class="bi bi-chevron-left"></i></a>
          </li>
          <li class="page-item" *ngFor="let p of paginasVisibles"
            [class.active]="p === paginaActual">
            <a class="page-link" (click)="irAPagina(p)">{{ p + 1 }}</a>
          </li>
          <li class="page-item" [class.disabled]="paginaActual >= totalPaginas - 1">
            <a class="page-link" (click)="paginaSiguiente()"><i class="bi bi-chevron-right"></i></a>
          </li>
          <li class="page-item" [class.disabled]="paginaActual >= totalPaginas - 1">
            <a class="page-link" (click)="irAPagina(totalPaginas - 1)"><i class="bi bi-chevrons-right"></i></a>
          </li>
        </ul>
      </nav>
    </div>

    <!-- Modal detalle -->
    <div class="modal-overlay" *ngIf="documentoDetalle" (click)="documentoDetalle = null">
      <div class="modal-custom" (click)="$event.stopPropagation()">
        <div class="modal-custom-header">
          <h5 class="mb-0"><i class="bi bi-file-earmark-text text-primary"></i> Detalle del Documento</h5>
          <button class="btn btn-sm btn-outline-secondary rounded-circle" (click)="documentoDetalle = null">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        <div class="modal-custom-body">
          <div class="info-grid">
            <div class="info-row">
              <span class="label">UUID</span>
              <span><code>{{ documentoDetalle.uuid }}</code></span>
            </div>
            <div class="info-row">
              <span class="label">Tipo</span>
              <span class="badge bg-secondary">{{ documentoDetalle.tipoDocumento }}</span>
            </div>
            <div class="info-row">
              <span class="label">Estado</span>
              <span class="badge" [ngClass]="getBadgeClass(documentoDetalle.estado)">{{ documentoDetalle.estado }}</span>
            </div>
            <div class="info-row" *ngIf="documentoDetalle.subestado">
              <span class="label">Subestado</span>
              <span>{{ documentoDetalle.subestado }}</span>
            </div>
            <div class="info-row" *ngIf="documentoDetalle.claveAcceso">
              <span class="label">Clave Acceso</span>
              <span><code>{{ documentoDetalle.claveAcceso }}</code></span>
            </div>
            <div class="info-row" *ngIf="documentoDetalle.numeroAutorizacion">
              <span class="label">Nro Autorización</span>
              <span><code>{{ documentoDetalle.numeroAutorizacion }}</code></span>
            </div>
            <div class="info-row">
              <span class="label">Ambiente</span>
              <span class="badge" [ngClass]="documentoDetalle.ambiente === 1 ? 'bg-warning text-dark' : 'bg-success'">
                {{ documentoDetalle.ambiente === 1 ? 'PRUEBAS' : 'PRODUCCIÓN' }}
              </span>
            </div>
          </div>
          <div class="mt-3 d-flex gap-2">
            <button class="btn btn-success btn-sm" (click)="descargarRide(documentoDetalle)"
              [disabled]="documentoDetalle.estado !== 'AUTORIZADO'">
              <i class="bi bi-file-earmark-pdf me-1"></i> RIDE
            </button>
            <button class="btn btn-info btn-sm" (click)="descargarXml(documentoDetalle)"
              [disabled]="documentoDetalle.estado !== 'AUTORIZADO'">
              <i class="bi bi-file-earmark-code me-1"></i> XML
            </button>
            <button class="btn btn-secondary btn-sm" (click)="descargarZip(documentoDetalle)"
              [disabled]="documentoDetalle.estado !== 'AUTORIZADO'">
              <i class="bi bi-file-earmark-zip me-1"></i> ZIP
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DocumentosComponent implements OnInit {
  documentos: any[] = [];
  filtroTipo = '';
  filtroEstado = '';
  busqueda = '';
  mostrarFormulario = false;
  xmlInput = '';
  tipoDocumentoSeleccionado = 'auto';
  enviando = false;
  resultadoEnvio: any = null;
  documentoDetalle: any = null;

  modoInput: 'texto' | 'archivo' = 'texto';
  nombreArchivo = '';
  tamanoArchivo = '';
  arrastrando = false;
  resultadoValidacion: any = null;

  paginaActual = 0;
  totalPaginas = 0;
  totalElementos = 0;
  tamanoPagina = 20;
  paginasVisibles: number[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargarDocumentos(); }

  cargarDocumentos() {
    this.api.listarDocumentos(this.paginaActual, this.tamanoPagina,
      this.filtroTipo || undefined, this.filtroEstado || undefined,
      this.busqueda || undefined)
      .subscribe((r: any) => {
        this.documentos = r.content || [];
        this.totalElementos = r.totalElements || 0;
        this.totalPaginas = r.totalPages || 0;
        this.calcularPaginasVisibles();
      });
  }

  calcularPaginasVisibles() {
    const inicio = Math.max(0, this.paginaActual - 2);
    const fin = Math.min(this.totalPaginas, this.paginaActual + 3);
    this.paginasVisibles = [];
    for (let i = inicio; i < fin; i++) {
      this.paginasVisibles.push(i);
    }
  }

  irAPagina(pagina: number) {
    this.paginaActual = pagina;
    this.cargarDocumentos();
  }

  paginaAnterior() {
    if (this.paginaActual > 0) {
      this.paginaActual--;
      this.cargarDocumentos();
    }
  }

  paginaSiguiente() {
    if (this.paginaActual < this.totalPaginas - 1) {
      this.paginaActual++;
      this.cargarDocumentos();
    }
  }

  enviarDocumento() {
    if (!this.xmlInput.trim()) return;
    this.enviando = true;
    this.resultadoEnvio = null;
    this.api.enviarDocumento(this.xmlInput).subscribe({
      next: (r: any) => {
        let parsed = r;
        if (typeof r === 'string') {
          try { parsed = JSON.parse(r); } catch(e) { parsed = { estado: r }; }
        }
        this.resultadoEnvio = {
          exito: true,
          mensaje: `Estado: ${parsed.estado || parsed.tipoDocumento || 'OK'}`,
          uuid: parsed.uuid || parsed.requestId
        };
        this.enviando = false;
        this.xmlInput = '';
        this.nombreArchivo = '';
        this.resultadoValidacion = null;
        this.cargarDocumentos();
      },
      error: (e) => {
        this.resultadoEnvio = { exito: false, mensaje: e.error?.error || e.message };
        this.enviando = false;
      }
    });
  }

  // ========== VALIDACIÓN XML ==========
  onXmlChange(xml: string) {
    this.resultadoValidacion = null;
    if (xml.trim().length > 50) {
      this.validarXml();
    }
  }

  validarXml() {
    this.resultadoValidacion = null;
    const xml = this.xmlInput.trim();
    if (!xml) return;

    const errores: string[] = [];
    const warnings: string[] = [];
    let tipoDetectado = '';
    const campos: any = {};

    // 1. Verificar que sea XML válido
    if (!xml.startsWith('<?xml') && !xml.startsWith('<')) {
      errores.push('El contenido no parece ser XML válido');
      this.resultadoValidacion = { valido: false, tipoDetectado: '', errores, warnings, campos: null };
      return;
    }

    // 2. Detectar tipo de documento
    if (xml.includes('<factura')) tipoDetectado = 'FACTURA';
    else if (xml.includes('<retencion')) tipoDetectado = 'RETENCION';
    else if (xml.includes('<notaCredito')) tipoDetectado = 'NOTA_CREDITO';
    else if (xml.includes('<notaDebito')) tipoDetectado = 'NOTA_DEBITO';
    else if (xml.includes('<liquidacionCompra')) tipoDetectado = 'LIQUIDACION_COMPRA';
    else if (xml.includes('<guiaRemision')) tipoDetectado = 'GUIA_REMISION';
    else errores.push('No se detectó tipo de documento (factura, retención, notaCredito, notaDebito, liquidacionCompra, guiaRemision)');

    // 3. Extraer y validar campos comunes
    const extractor = (tag: string): string => {
      const regex = new RegExp(`<${tag}>([^<]*)</${tag}>`);
      const match = xml.match(regex);
      return match ? match[1].trim() : '';
    };

    campos.ruc = extractor('ruc');
    campos.claveAcceso = extractor('claveAcceso');
    campos.ambiente = extractor('ambiente');
    campos.codDoc = extractor('codDoc');
    campos.fechaEmision = extractor('fechaEmision');

    // 4. Validaciones
    if (!campos.ruc) {
      errores.push('Falta campo <ruc>');
    } else if (campos.ruc.length !== 13) {
      errores.push(`RUC inválido: debe tener 13 dígitos (tiene ${campos.ruc.length})`);
    }

    if (!campos.claveAcceso) {
      errores.push('Falta campo <claveAcceso>');
    } else if (campos.claveAcceso.length !== 49) {
      errores.push(`Clave de acceso inválida: debe tener 49 dígitos (tiene ${campos.claveAcceso.length})`);
    }

    if (!campos.ambiente) {
      warnings.push('No se especificó ambiente (1=pruebas, 2=producción)');
    } else if (campos.ambiente !== '1' && campos.ambiente !== '2') {
      errores.push(`Ambiente inválido: debe ser 1 (pruebas) o 2 (producción), actual: ${campos.ambiente}`);
    }

    if (!campos.fechaEmision) {
      warnings.push('No se encontró campo <fechaEmision>');
    }

    // 5. Validar codDoc según tipo
    const codDocEsperado: Record<string, string> = {
      'FACTURA': '01', 'RETENCION': '07', 'NOTA_CREDITO': '04',
      'NOTA_DEBITO': '05', 'LIQUIDACION_COMPRA': '03', 'GUIA_REMISION': '06'
    };
    if (tipoDetectado && campos.codDoc) {
      const esperado = codDocEsperado[tipoDetectado];
      if (esperado && campos.codDoc !== esperado) {
        errores.push(`codDoc inválido para ${tipoDetectado}: esperado ${esperado}, actual ${campos.codDoc}`);
      }
    }

    // 6. Verificar info adicional
    if (!xml.includes('<campoAdicional')) {
      warnings.push('No se encontraron campos adicionales');
    }

    const valido = errores.length === 0;

    if (tipoDetectado && this.tipoDocumentoSeleccionado === 'auto') {
      this.tipoDocumentoSeleccionado = tipoDetectado;
    }

    this.resultadoValidacion = { valido, tipoDetectado, errores, warnings, campos };
  }

  // ========== ARCHIVO ==========
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) this.procesarArchivo(file);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.arrastrando = true;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.arrastrando = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.procesarArchivo(files[0]);
    }
  }

  procesarArchivo(file: File) {
    if (!file.name.endsWith('.xml')) {
      alert('Solo se permiten archivos .xml');
      return;
    }
    this.nombreArchivo = file.name;
    this.tamanoArchivo = this.formatSize(file.size);

    const reader = new FileReader();
    reader.onload = (e) => {
      this.xmlInput = e.target?.result as string;
      this.validarXml();
    };
    reader.readAsText(file);
  }

  limpiarArchivo() {
    this.nombreArchivo = '';
    this.tamanoArchivo = '';
    this.xmlInput = '';
    this.resultadoValidacion = null;
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  exportarCsv() {
    this.api.exportarCsv(this.filtroTipo || undefined, this.filtroEstado || undefined,
      this.busqueda || undefined)
      .subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'documentos.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  verDetalle(doc: any) { this.documentoDetalle = doc; }

  descargarRide(doc: any) {
    this.api.descargarRide(doc.uuid).subscribe(blob => this.descargarBlob(blob, `ride_${doc.uuid}.pdf`));
  }

  descargarXml(doc: any) {
    this.api.descargarXml(doc.uuid).subscribe(blob => this.descargarBlob(blob, `xml_${doc.uuid}.xml`));
  }

  descargarZip(doc: any) {
    this.api.descargarZip(doc.uuid).subscribe(blob => this.descargarBlob(blob, `doc_${doc.uuid}.zip`));
  }

  private descargarBlob(blob: Blob, nombre: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombre;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  getBadgeClass(estado: string): string {
    switch (estado) {
      case 'AUTORIZADO': case 'FINALIZADO': return 'bg-success';
      case 'PENDIENTE_AUTORIZACION': case 'RECIBIDO_SRI': return 'bg-warning text-dark';
      case 'ERROR': case 'NO_AUTORIZADO': return 'bg-danger';
      default: return 'bg-info';
    }
  }

  limpiarFiltros() {
    this.filtroTipo = '';
    this.filtroEstado = '';
    this.busqueda = '';
    this.paginaActual = 0;
    this.cargarDocumentos();
  }
}
