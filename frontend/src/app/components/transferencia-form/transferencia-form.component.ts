import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Transferencia } from '../../models/transferencia.model';

@Component({
  selector: 'app-transferencia-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="fechar.emit(false)">
      <div class="modal-content glass-panel" (click)="$event.stopPropagation()">
        <h3 style="margin-bottom: 1.5rem;"><i class="fa-solid fa-arrow-right-arrow-left" style="color: var(--secondary)"></i> Transferir Saldo</h3>
        
        <form (ngSubmit)="transferir()" #form="ngForm">
          <div class="form-group">
            <label class="form-label">ID do Benefício de Origem</label>
            <input type="number" class="form-control" name="fromId" [(ngModel)]="model.fromId" required>
          </div>
          
          <div class="form-group">
            <label class="form-label">ID do Benefício de Destino</label>
            <input type="number" class="form-control" name="toId" [(ngModel)]="model.toId" required>
          </div>
          
          <div class="form-group">
            <label class="form-label">Valor a Transferir (R$)</label>
            <input type="number" step="0.01" class="form-control" name="amount" [(ngModel)]="model.amount" required>
          </div>
          
          <div style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
            <button type="button" class="btn btn-ghost" (click)="fechar.emit(false)">Cancelar</button>
            <button type="submit" class="btn btn-primary" [disabled]="!form.valid || loading">
               {{ loading ? 'Transferindo...' : 'Confirmar Transferência' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class TransferenciaFormComponent {
  @Output('onClose') fechar = new EventEmitter<boolean>();
  model: Transferencia = { fromId: null as any, toId: null as any, amount: null as any };
  loading = false;

  constructor(private service: BeneficioService) {}

  transferir() {
    this.loading = true;
    this.service.transferir(this.model).subscribe({
      next: () => this.fechar.emit(true),
      error: (err) => { 
        console.error(err); 
        alert('Erro ao transferir. Verifique os IDs e se há saldo suficiente.');
        this.loading = false; 
      }
    });
  }
}
