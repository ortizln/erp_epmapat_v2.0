import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  MetricasResponse, HealthResponse, InfoResponse, DashboardResponse,
  DocumentoElectronico, Plantilla, PlantillaResponse, XmlResponse,
  MailTestResponse, XmlAutorizadoResponse
} from '../models/sri.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ========== MONITOREO ==========
  health(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${this.base}/monitoring/health`);
  }

  info(): Observable<InfoResponse> {
    return this.http.get<InfoResponse>(`${this.base}/monitoring/info`);
  }

  metrics(): Observable<MetricasResponse> {
    return this.http.get<MetricasResponse>(`${this.base}/monitoring/metrics`);
  }

  dashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.base}/monitoring/dashboard`);
  }

  // ========== DOCUMENTOS ==========
  listarDocumentos(page = 0, size = 20, tipo?: string, estado?: string, busqueda?: string): Observable<any> {
    let params = `?page=${page}&size=${size}`;
    if (tipo) params += `&tipo=${tipo}`;
    if (estado) params += `&estado=${estado}`;
    if (busqueda) params += `&busqueda=${encodeURIComponent(busqueda)}`;
    return this.http.get(`${this.base}/documentos${params}`);
  }

  consultarDocumento(uuid: string): Observable<DocumentoElectronico> {
    return this.http.get<DocumentoElectronico>(`${this.base}/documentos/${uuid}`);
  }

  enviarDocumento(xml: string): Observable<any> {
    return this.http.post(`${this.base}/documentos`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  descargarRide(uuid: string): Observable<Blob> {
    return this.http.get(`${this.base}/documentos/${uuid}/ride`, { responseType: 'blob' });
  }

  descargarXml(uuid: string): Observable<Blob> {
    return this.http.get(`${this.base}/documentos/${uuid}/xml`, { responseType: 'blob' });
  }

  descargarZip(uuid: string): Observable<Blob> {
    return this.http.get(`${this.base}/documentos/${uuid}/zip`, { responseType: 'blob' });
  }

  exportarCsv(tipo?: string, estado?: string, busqueda?: string): Observable<Blob> {
    let params = '?1=1';
    if (tipo) params += `&tipo=${tipo}`;
    if (estado) params += `&estado=${estado}`;
    if (busqueda) params += `&busqueda=${encodeURIComponent(busqueda)}`;
    return this.http.get(`${this.base}/documentos/export${params}`, { responseType: 'blob' });
  }

  // ========== AUTORIZACIÓN ==========
  obtenerXmlAutorizado(claveAcceso: string, forzarSri = false): Observable<XmlAutorizadoResponse> {
    return this.http.get<XmlAutorizadoResponse>(
      `${this.base}/autorizacion/${claveAcceso}/xml?forzarSri=${forzarSri}`
    );
  }

  consultarAutorizacion(claveAcceso: string, wait = false): Observable<any> {
    return this.http.get(`${this.base}/autorizacion/${claveAcceso}?wait=${wait}`);
  }

  // ========== PLANTILLAS ==========
  listarPlantillas(): Observable<PlantillaResponse> {
    return this.http.get<PlantillaResponse>(`${this.base}/plantillas`);
  }

  obtenerPlantilla(nombre: string): Observable<XmlResponse> {
    return this.http.get<XmlResponse>(`${this.base}/plantillas/${nombre}`);
  }

  crearPlantilla(nombre: string, contenido: string): Observable<any> {
    return this.http.post(`${this.base}/plantillas?nombre=${nombre}`, contenido, {
      headers: { 'Content-Type': 'application/xml' }
    });
  }

  actualizarPlantilla(nombre: string, contenido: string): Observable<any> {
    return this.http.put(`${this.base}/plantillas/${nombre}`, contenido, {
      headers: { 'Content-Type': 'application/xml' }
    });
  }

  eliminarPlantilla(nombre: string): Observable<any> {
    return this.http.delete(`${this.base}/plantillas/${nombre}`);
  }

  // ========== CORREO ==========
  healthCorreo(): Observable<any> {
    return this.http.get(`${this.base}/correo/health`);
  }

  testCorreo(destino: string): Observable<MailTestResponse> {
    return this.http.post<MailTestResponse>(`${this.base}/correo/test`, destino, {
      headers: { 'Content-Type': 'text/plain' }
    });
  }

  enviarCorreo(req: any): Observable<any> {
    return this.http.post(`${this.base}/correo/send`, req);
  }

  // ========== FACTURAS ==========
  crearFactura(idfactura: number): Observable<any> {
    return this.http.post(`${this.base}/facturas`, { idfactura });
  }

  consultarFactura(idfactura: number): Observable<any> {
    return this.http.get(`${this.base}/facturas/${idfactura}`);
  }

  // ========== RETENCIONES ==========
  enviarRetencion(xml: string): Observable<any> {
    return this.http.post(`${this.base}/retenciones`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  validarRetencion(xml: string): Observable<any> {
    return this.http.post(`${this.base}/retenciones/validar`, xml, {
      headers: { 'Content-Type': 'application/xml' }
    });
  }

  // ========== NOTAS CRÉDITO ==========
  enviarNotaCredito(xml: string): Observable<any> {
    return this.http.post(`${this.base}/notas-credito`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  // ========== NOTAS DÉBITO ==========
  enviarNotaDebito(xml: string): Observable<any> {
    return this.http.post(`${this.base}/notas-debito`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  // ========== LIQUIDACIONES COMPRA ==========
  enviarLiquidacionCompra(xml: string): Observable<any> {
    return this.http.post(`${this.base}/liquidaciones-compra`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  // ========== GUÍAS REMISIÓN ==========
  enviarGuiaRemision(xml: string): Observable<any> {
    return this.http.post(`${this.base}/guias-remision`, xml, {
      headers: { 'Content-Type': 'application/xml' },
      responseType: 'text' as any
    });
  }

  // ========== BATCH ==========
  ejecutarBatch(tipo: string): Observable<any> {
    return this.http.post(`${this.base}/batch/ejecutar`, { tipo });
  }

  listarBatch(): Observable<any> {
    return this.http.get(`${this.base}/batch`);
  }

  consultarBatch(uuid: string): Observable<any> {
    return this.http.get(`${this.base}/batch/${uuid}`);
  }

  // ========== ERP ==========
  crearFacturaErp(idfactura: number): Observable<any> {
    return this.http.post(`${this.base}/erp/factura`, { idfactura });
  }

  crearDocumentoErp(tipoDocumento: string, externalId: string, datos: any): Observable<any> {
    return this.http.post(`${this.base}/erp/documento`, { tipoDocumento, externalId, ...datos });
  }

  consultarEstadoErp(uuid: string): Observable<any> {
    return this.http.get(`${this.base}/erp/${uuid}/estado`);
  }

  pingErp(): Observable<any> {
    return this.http.get(`${this.base}/erp/ping`);
  }
}
