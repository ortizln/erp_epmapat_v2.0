import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-monitoreo',
  templateUrl: './monitoreo.component.html',
  styleUrls: ['./monitoreo.component.scss']
})
export class MonitoreoComponent implements OnInit {
  health: any = null;
  info: any = null;
  metricas: any = null;
  claveAcceso = '';
  consultando = false;
  resultadoSri: any = null;
  loading = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() {
    this.loading = true;
    this.api.health().subscribe(r => { this.health = r; this.loading = false; });
    this.api.info().subscribe(r => this.info = r);
    this.api.metrics().subscribe(r => this.metricas = r);
  }

  consultarSri() {
    if (!this.claveAcceso.trim()) return;
    this.consultando = true;
    this.api.consultarAutorizacion(this.claveAcceso).subscribe({
      next: (r) => { this.resultadoSri = r; this.consultando = false; },
      error: (e) => { this.resultadoSri = { error: e.error?.error || e.message }; this.consultando = false; }
    });
  }

  obtenerXml() {
    if (!this.claveAcceso.trim()) return;
    this.consultando = true;
    this.api.obtenerXmlAutorizado(this.claveAcceso).subscribe({
      next: (r) => { this.resultadoSri = r; this.consultando = false; },
      error: (e) => { this.resultadoSri = { error: e.error?.error || e.message }; this.consultando = false; }
    });
  }
}
