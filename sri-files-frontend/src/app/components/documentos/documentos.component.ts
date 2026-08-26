import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { TableColumn, TableAction } from '../shared/data-table/data-table.component';

@Component({
  selector: 'app-documentos',
  templateUrl: './documentos.component.html',
  styleUrls: ['./documentos.component.scss']
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
  sortKey = 'id';
  sortDirection: 'asc' | 'desc' = 'desc';
  cargando = false;

  columnas: TableColumn[] = [
    { key: 'uuid', label: 'UUID', sortable: true, type: 'code', truncate: 10 },
    { key: 'tipoDocumento', label: 'Tipo', sortable: true, type: 'badge',
      badgeClass: () => 'badge-secondary' },
    { key: 'estado', label: 'Estado', sortable: true, type: 'icon',
      badgeClass: (v) => this.getBadgeClass(v),
      iconClass: (v) => this.getIconClass(v) },
    { key: 'claveAcceso', label: 'Clave Acceso', sortable: true, type: 'code', truncate: 22 },
    { key: 'numeroAutorizacion', label: 'Nro Autorización', sortable: true, type: 'text' },
    { key: 'fechaRecepcion', label: 'Fecha', sortable: true, type: 'date' },
  ];

  acciones: TableAction[] = [
    { icon: 'bi-eye', label: 'Ver detalle', class: 'btn-outline-primary', click: (r) => this.verDetalle(r) },
    { icon: 'bi-file-earmark-pdf', label: 'RIDE', class: 'btn-outline-success',
      disabled: (r) => r.estado !== 'AUTORIZADO' && r.estado !== 'FINALIZADO',
      click: (r) => this.descargarRide(r) },
    { icon: 'bi-file-earmark-code', label: 'XML', class: 'btn-outline-info',
      disabled: (r) => r.estado !== 'AUTORIZADO' && r.estado !== 'FINALIZADO',
      click: (r) => this.descargarXml(r) },
    { icon: 'bi-file-earmark-zip', label: 'ZIP', class: 'btn-outline-secondary',
      disabled: (r) => r.estado !== 'AUTORIZADO' && r.estado !== 'FINALIZADO',
      click: (r) => this.descargarZip(r) },
  ];

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargarDocumentos(); }

  cargarDocumentos() {
    this.cargando = true;
    this.api.listarDocumentos(this.paginaActual, this.tamanoPagina,
      this.filtroTipo || undefined, this.filtroEstado || undefined,
      this.busqueda || undefined)
      .subscribe({
        next: (r: any) => {
          this.documentos = r.content || [];
          this.totalElementos = r.totalElements || 0;
          this.totalPaginas = r.totalPages || 0;
          this.cargando = false;
        },
        error: () => { this.cargando = false; }
      });
  }

  onPageChange(page: number) {
    this.paginaActual = page;
    this.cargarDocumentos();
  }

  onPageSizeChange(size: number) {
    this.tamanoPagina = size;
    this.paginaActual = 0;
    this.cargarDocumentos();
  }

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }) {
    this.sortKey = event.key;
    this.sortDirection = event.direction;
    // Sorting client-side for now
    this.documentos.sort((a, b) => {
      const va = a[event.key] ?? '';
      const vb = b[event.key] ?? '';
      const cmp = String(va).localeCompare(String(vb), 'es');
      return event.direction === 'asc' ? cmp : -cmp;
    });
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

  onXmlChange(xml: string) {
    this.resultadoValidacion = null;
    if (xml.trim().length > 50) this.validarXml();
  }

  validarXml() {
    this.resultadoValidacion = null;
    const xml = this.xmlInput.trim();
    if (!xml) return;

    const errores: string[] = [];
    const warnings: string[] = [];
    let tipoDetectado = '';
    const campos: any = {};

    if (!xml.startsWith('<?xml') && !xml.startsWith('<')) {
      errores.push('El contenido no parece ser XML válido');
      this.resultadoValidacion = { valido: false, tipoDetectado: '', errores, warnings, campos: null };
      return;
    }

    if (xml.includes('<factura')) tipoDetectado = 'FACTURA';
    else if (xml.includes('<retencion')) tipoDetectado = 'RETENCION';
    else if (xml.includes('<notaCredito')) tipoDetectado = 'NOTA_CREDITO';
    else if (xml.includes('<notaDebito')) tipoDetectado = 'NOTA_DEBITO';
    else if (xml.includes('<liquidacionCompra')) tipoDetectado = 'LIQUIDACION_COMPRA';
    else if (xml.includes('<guiaRemision')) tipoDetectado = 'GUIA_REMISION';
    else errores.push('No se detectó tipo de documento');

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

    if (!campos.ruc) errores.push('Falta campo <ruc>');
    else if (campos.ruc.length !== 13) errores.push(`RUC inválido: debe tener 13 dígitos (tiene ${campos.ruc.length})`);
    if (!campos.claveAcceso) errores.push('Falta campo <claveAcceso>');
    else if (campos.claveAcceso.length !== 49) errores.push(`Clave inválida: debe tener 49 dígitos (tiene ${campos.claveAcceso.length})`);
    if (!campos.ambiente) warnings.push('No se especificó ambiente');
    else if (campos.ambiente !== '1' && campos.ambiente !== '2') errores.push(`Ambiente inválido: ${campos.ambiente}`);
    if (!campos.fechaEmision) warnings.push('No se encontró <fechaEmision>');

    const codDocEsperado: Record<string, string> = {
      'FACTURA': '01', 'RETENCION': '07', 'NOTA_CREDITO': '04',
      'NOTA_DEBITO': '05', 'LIQUIDACION_COMPRA': '03', 'GUIA_REMISION': '06'
    };
    if (tipoDetectado && campos.codDoc) {
      const esperado = codDocEsperado[tipoDetectado];
      if (esperado && campos.codDoc !== esperado) errores.push(`codDoc inválido: esperado ${esperado}, actual ${campos.codDoc}`);
    }

    const valido = errores.length === 0;
    if (tipoDetectado && this.tipoDocumentoSeleccionado === 'auto') this.tipoDocumentoSeleccionado = tipoDetectado;
    this.resultadoValidacion = { valido, tipoDetectado, errores, warnings, campos };
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) this.procesarArchivo(file);
  }

  onDragOver(event: DragEvent) { event.preventDefault(); event.stopPropagation(); this.arrastrando = true; }

  onDrop(event: DragEvent) {
    event.preventDefault(); event.stopPropagation(); this.arrastrando = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) this.procesarArchivo(files[0]);
  }

  procesarArchivo(file: File) {
    if (!file.name.endsWith('.xml')) { alert('Solo se permiten archivos .xml'); return; }
    this.nombreArchivo = file.name;
    this.tamanoArchivo = this.formatSize(file.size);
    const reader = new FileReader();
    reader.onload = (e) => { this.xmlInput = e.target?.result as string; this.validarXml(); };
    reader.readAsText(file);
  }

  limpiarArchivo() { this.nombreArchivo = ''; this.tamanoArchivo = ''; this.xmlInput = ''; this.resultadoValidacion = null; }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  }

  exportarCsv() {
    this.api.exportarCsv(this.filtroTipo || undefined, this.filtroEstado || undefined, this.busqueda || undefined)
      .subscribe(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a'); a.href = url; a.download = 'documentos.csv'; a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  verDetalle(doc: any) { this.documentoDetalle = doc; }

  descargarRide(doc: any) { this.api.descargarRide(doc.uuid).subscribe(b => this.downloadBlob(b, `ride_${doc.uuid}.pdf`)); }
  descargarXml(doc: any) { this.api.descargarXml(doc.uuid).subscribe(b => this.downloadBlob(b, `xml_${doc.uuid}.xml`)); }
  descargarZip(doc: any) { this.api.descargarZip(doc.uuid).subscribe(b => this.downloadBlob(b, `doc_${doc.uuid}.zip`)); }

  private downloadBlob(blob: Blob, name: string) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = name; a.click();
    window.URL.revokeObjectURL(url);
  }

  getBadgeClass(estado: string): string {
    switch (estado) {
      case 'AUTORIZADO': case 'FINALIZADO': return 'badge-success';
      case 'PENDIENTE_AUTORIZACION': case 'RECIBIDO_SRI': return 'badge-warning';
      case 'ERROR': case 'NO_AUTORIZADO': return 'badge-danger';
      default: return 'badge-info';
    }
  }

  getIconClass(estado: string): string {
    switch (estado) {
      case 'AUTORIZADO': case 'FINALIZADO': return 'bi-check-circle-fill';
      case 'PENDIENTE_AUTORIZACION': case 'RECIBIDO_SRI': return 'bi-clock-fill';
      case 'ERROR': case 'NO_AUTORIZADO': return 'bi-x-circle-fill';
      default: return 'bi-info-circle-fill';
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
