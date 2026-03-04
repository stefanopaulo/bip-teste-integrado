import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="fechar.emit(false)">
      <div class="modal-content glass-panel" (click)="$event.stopPropagation()">
        <h3 style="margin-bottom: 1.5rem;"><i class="fa-solid fa-pen-to-square"></i> {{ isEdicao ? 'Editar' : 'Novo' }} Benefício</h3>
        
        <form (ngSubmit)="salvar()" #form="ngForm">
          <div class="form-group">
            <label class="form-label">Nome</label>
            <input type="text" class="form-control" name="nome" [(ngModel)]="model.nome" required>
          </div>
          
          <div class="form-group">
            <label class="form-label">Descrição</label>
            <input type="text" class="form-control" name="descricao" [(ngModel)]="model.descricao" required>
          </div>
          
          <div class="form-group">
            <label class="form-label">Valor (R$)</label>
            <input type="number" step="0.01" class="form-control" name="valor" [(ngModel)]="model.valor" required>
          </div>
          
          <div style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
            <button type="button" class="btn btn-ghost" (click)="fechar.emit(false)">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="!form.valid || loading">
              <i class="fa-solid fa-save"></i> {{ loading ? 'Salvando...' : 'Salvar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class BeneficioFormComponent implements OnInit {
  @Input() beneficio?: Beneficio;
  @Output('onClose') fechar = new EventEmitter<boolean>();

  model: Beneficio = { nome: '', descricao: '', valor: null as any };
  isEdicao = false;
  loading = false;

  constructor(private service: BeneficioService) {}

  ngOnInit() {
    if (this.beneficio) {
      this.isEdicao = true;
      this.model = { ...this.beneficio };
    }
  }

  salvar() {
    this.loading = true;
    if (this.isEdicao) {
      this.service.atualizar(this.model.id!, this.model).subscribe({
        next: () => this.fechar.emit(true),
        error: (err) => { console.error(err); this.loading = false; }
      });
    } else {
      this.service.inserir(this.model).subscribe({
        next: () => this.fechar.emit(true),
        error: (err) => { console.error(err); this.loading = false; }
      });
    }
  }
}
