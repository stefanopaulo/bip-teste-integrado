import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Transferencia } from '../../models/transferencia.model';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-transferencia-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="fechar.emit(false)">
      <div class="modal-content glass-panel" (click)="$event.stopPropagation()">
        <h3 style="margin-bottom: 1.5rem;">
          <i class="fa-solid fa-arrow-right-arrow-left" style="color: var(--secondary)"></i>
          Transferir Saldo
        </h3>

        <form (ngSubmit)="transferir()" #form="ngForm">
          <div class="form-group">
            <label class="form-label">Benefício de Origem</label>
            <select [(ngModel)]="model.fromId" name="fromId" class="form-control" required>
              <option [ngValue]="null" disabled selected>Selecione o benefício</option>
              @for (b of beneficios; track b.id) {
                <option [ngValue]="b.id">
                  {{ b.nome }}
                </option>
              }
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Benefício de Destino</label>
            <select [(ngModel)]="model.toId" name="toId" class="form-control" required>
              <option [ngValue]="null" disabled selected>Selecione o benefício</option>
              @for (b of beneficios; track b.id) {
                <option [ngValue]="b.id" [disabled]="b.id === model.fromId">
                  {{ b.nome }}
                </option>
              }
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Valor a Transferir (R$)</label>
            <input
              type="number"
              step="0.01"
              class="form-control"
              name="amount"
              [(ngModel)]="model.amount"
              required
            />
          </div>

          <div style="display: flex; gap: 1rem; justify-content: flex-end; margin-top: 2rem;">
            <button type="button" class="btn btn-ghost" (click)="fechar.emit(false)">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary" [disabled]="!form.valid || loading">
              {{ loading ? 'Transferindo...' : 'Confirmar Transferência' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
})
export class TransferenciaFormComponent {
  @Input() beneficios: Beneficio[] = [];
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
        alert('Erro ao transferir. Verifique os benefícios e se há saldo suficiente.');
        this.loading = false;
      },
    });
  }
}
