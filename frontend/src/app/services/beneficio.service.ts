import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio } from '../models/beneficio.model';
import { Transferencia } from '../models/transferencia.model';
import { Page } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  listar(page: number = 0, size: number = 10): Observable<Page<Beneficio>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<Beneficio>>(this.apiUrl, { params });
  }

  buscarPorId(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`);
  }

  inserir(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio);
  }

  atualizar(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  transferir(transferencia: Transferencia): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transferir`, transferencia);
  }
}
