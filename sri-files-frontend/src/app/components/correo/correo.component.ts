import { Component } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-correo',
  templateUrl: './correo.component.html',
  styleUrls: ['./correo.component.scss']
})
export class CorreoComponent {
  verificando = false;
  smtpStatus: any = null;
  correoDestino = '';
  enviando = false;
  resultado: any = null;

  constructor(private api: ApiService) {}

  verificarSmtp() {
    this.verificando = true;
    this.api.healthCorreo().subscribe({
      next: (r) => { this.smtpStatus = r; this.verificando = false; },
      error: () => { this.smtpStatus = { smtp: 'DOWN' }; this.verificando = false; }
    });
  }

  enviarTest() {
    if (!this.correoDestino.trim()) return;
    this.enviando = true;
    this.resultado = null;
    this.api.testCorreo(this.correoDestino).subscribe({
      next: (r) => { this.resultado = r; this.enviando = false; },
      error: (e) => { this.resultado = { exito: false, mensaje: e.error?.error || e.message }; this.enviando = false; }
    });
  }
}
