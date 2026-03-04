import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';
import { Page } from '../../models/page.model';
import { BeneficioFormComponent } from '../beneficio-form/beneficio-form.component';
import { TransferenciaFormComponent } from '../transferencia-form/transferencia-form.component';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule, BeneficioFormComponent, TransferenciaFormComponent],
  templateUrl: './beneficio-list.component.html',
  styleUrls: ['./beneficio-list.component.css'],
})
export class BeneficioListComponent implements OnInit {
  page: Page<Beneficio> | null = null;
  currentPage = 0;
  pageSize = 5;
  loading = true;

  showForm = false;
  showTransfer = false;
  selectedBeneficio?: Beneficio;

  constructor(
    private service: BeneficioService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.carregar(this.currentPage);
  }

  carregar(pageNumber: number) {
    this.loading = true;
    this.service.listar(pageNumber, this.pageSize).subscribe({
      next: (res) => {
        this.page = res;
        this.currentPage = res.number;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('API Error:', err);
        this.loading = false;
        // Mock data if API is down just for UI presentation (temporary fallback)
        this.page = {
          content: [
            { id: 1, nome: 'Benefício VT', descricao: 'Vale Transporte', valor: 250.0 },
            { id: 2, nome: 'Benefício VR', descricao: 'Vale Refeição', valor: 600.0 },
          ],
          totalElements: 2,
          totalPages: 1,
          number: 0,
          size: 5,
          last: true,
          first: true,
        } as Page<Beneficio>;
      },
    });
  }

  nextPage() {
    if (this.page && !this.page.last) {
      this.carregar(this.currentPage + 1);
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.carregar(this.currentPage - 1);
    }
  }

  abrirFormulario(beneficio?: Beneficio) {
    this.selectedBeneficio = beneficio;
    this.showForm = true;
  }

  fecharFormulario(recarregar: boolean = false) {
    this.showForm = false;
    this.selectedBeneficio = undefined;
    if (recarregar) {
      this.carregar(this.currentPage);
    }
  }

  abrirTransferencia() {
    this.showTransfer = true;
  }

  fecharTransferencia(recarregar: boolean = false) {
    this.showTransfer = false;
    if (recarregar) {
      this.carregar(this.currentPage);
    }
  }

  deletar(id: number) {
    if (confirm('Tem certeza que deseja excluir este benefício?')) {
      this.service.deletar(id).subscribe(() => this.carregar(this.currentPage));
    }
  }
}
