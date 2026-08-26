import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class LogService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  consultar(lines = 200, level?: string, search?: string, requestId?: string): Observable<any> {
    let params = `?lines=${lines}`;
    if (level) params += `&level=${level}`;
    if (search) params += `&search=${encodeURIComponent(search)}`;
    if (requestId) params += `&requestId=${encodeURIComponent(requestId)}`;
    return this.http.get(`${this.base}/logs${params}`);
  }

  archivos(): Observable<any> {
    return this.http.get(`${this.base}/logs/files`);
  }
}
