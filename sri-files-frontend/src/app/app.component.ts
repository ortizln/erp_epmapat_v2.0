import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="layout">
      <!-- Sidebar -->
      <nav class="sidebar">
        <div class="sidebar-brand">
          <h5><i class="bi bi-file-earmark-code"></i> SRI Files</h5>
          <small>Panel de Administración</small>
        </div>
        <div class="sidebar-menu">
          <a class="nav-link" routerLink="/dashboard" routerLinkActive="active">
            <i class="bi bi-speedometer2"></i> Dashboard
          </a>
          <a class="nav-link" routerLink="/documentos" routerLinkActive="active">
            <i class="bi bi-file-earmark-text"></i> Documentos
          </a>
          <a class="nav-link" routerLink="/plantillas" routerLinkActive="active">
            <i class="bi bi-filetype-jrxml"></i> Plantillas
          </a>
          <a class="nav-link" routerLink="/correo" routerLinkActive="active">
            <i class="bi bi-envelope-at"></i> Correo
          </a>
          <a class="nav-link" routerLink="/monitoreo" routerLinkActive="active">
            <i class="bi bi-activity"></i> Monitoreo
          </a>
        </div>
      </nav>

      <!-- Main -->
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: []
})
export class AppComponent { }
