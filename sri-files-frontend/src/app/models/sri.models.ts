export interface MetricasResponse {
  totalRecibidos: number;
  totalAutorizados: number;
  totalFallidos: number;
  totalEnviosSri: number;
  documentosPorTipo: { [key: string]: number };
  uptime: string;
}

export interface HealthResponse {
  status: string;
  service: string;
  ambiente: string;
  database: string;
  timestamp: string;
}

export interface InfoResponse {
  service: string;
  version: string;
  ambiente: string;
  profile: string;
  javaVersion: string;
  availableProcessors: number;
  maxMemoryMB: number;
  totalMemoryMB: number;
  freeMemoryMB: number;
}

export interface DashboardResponse {
  documentoElectronico: { [key: string]: number };
  facturas: { total: number };
  timestamp: string;
}

export interface DocumentoElectronico {
  id?: number;
  uuid: string;
  tipoDocumento: string;
  estado: string;
  subestado?: string;
  claveAcceso?: string;
  numeroAutorizacion?: string;
  ambiente?: number;
  fechaRecepcion?: string;
  fechaAutorizacion?: string;
  requestId?: string;
}

export interface Plantilla {
  nombre: string;
  tamanio: number;
  ultimaModificacion?: string;
}

export interface PlantillaResponse {
  directorio: string;
  plantillas: Plantilla[];
  total: number;
}

export interface XmlResponse {
  nombre: string;
  contenido: string;
  tamanio: number;
}

export interface MailTestResponse {
  exito: boolean;
  mensaje: string;
  timestamp: string;
}

export interface XmlAutorizadoResponse {
  fuente: string;
  claveAcceso: string;
  estado?: string;
  numeroAutorizacion?: string;
  xmlAutorizado: string;
}
