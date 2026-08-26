import { Component, OnInit, OnDestroy } from '@angular/core';
import { LogService } from '../../services/log.service';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit, OnDestroy {
  lineas: string[] = [];
  total = 0;
  filtered = 0;
  archivo = '';
  timestamp = '';
  cargando = false;
  error = '';

  busqueda = '';
  filtroLevel = '';
  filtroRequestId = '';
  maxLineas = 200;
  autoRefresh = false;
  refreshInterval: any = null;

  archivos: any[] = [];

  constructor(private logService: LogService) {}

  ngOnInit() {
    this.cargarLogs();
    this.cargarArchivos();
  }

  ngOnDestroy() {
    this.detenerAutoRefresh();
  }

  cargarLogs() {
    this.cargando = true;
    this.error = '';
    this.logService.consultar(
      this.maxLineas,
      this.filtroLevel || undefined,
      this.busqueda || undefined,
      this.filtroRequestId || undefined
    ).subscribe({
      next: (r: any) => {
        this.lineas = r.lines || [];
        this.total = r.total || 0;
        this.filtered = r.filtered || 0;
        this.archivo = r.file || '';
        this.timestamp = r.timestamp || '';
        this.cargando = false;
        this.scrollAlFinal();
      },
      error: (e) => {
        this.error = e.error?.error || e.message;
        this.cargando = false;
      }
    });
  }

  cargarArchivos() {
    this.logService.archivos().subscribe({
      next: (r: any) => { this.archivos = r.files || []; },
      error: () => {}
    });
  }

  toggleAutoRefresh() {
    if (this.autoRefresh) {
      this.autoRefresh = false;
      this.detenerAutoRefresh();
    } else {
      this.autoRefresh = true;
      this.refreshInterval = setInterval(() => this.cargarLogs(), 3000);
    }
  }

  detenerAutoRefresh() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
      this.refreshInterval = null;
    }
  }

  scrollAlFinal() {
    setTimeout(() => {
      const el = document.querySelector('.log-content');
      if (el) el.scrollTop = el.scrollHeight;
    }, 100);
  }

  getLevelClass(linea: string): string {
    if (linea.includes(' ERROR ')) return 'log-error';
    if (linea.includes(' WARN ')) return 'log-warn';
    if (linea.includes(' DEBUG ')) return 'log-debug';
    return 'log-info';
  }

  getLevelBadge(linea: string): string {
    if (linea.includes(' ERROR ')) return 'badge-danger';
    if (linea.includes(' WARN ')) return 'badge-warning';
    if (linea.includes(' DEBUG ')) return 'badge-secondary';
    return 'badge-info';
  }

  getLevelIcon(linea: string): string {
    if (linea.includes(' ERROR ')) return 'bi-x-circle-fill';
    if (linea.includes(' WARN ')) return 'bi-exclamation-triangle-fill';
    if (linea.includes(' DEBUG ')) return 'bi-code-slash';
    return 'bi-info-circle-fill';
  }

  limpiarFiltros() {
    this.busqueda = '';
    this.filtroLevel = '';
    this.filtroRequestId = '';
    this.maxLineas = 200;
    this.cargarLogs();
  }

  formatSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  }

  contarPorNivel(nivel: string): number {
    return this.lineas.filter(l => l.includes(' ' + nivel + ' ')).length;
  }

  exportar() {
    const blob = new Blob([this.lineas.join('\n')], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `sri-files-logs-${new Date().toISOString().slice(0,10)}.log`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
