import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css'
})
export class ButtonComponent {
  @Input() class: string = '';
  @Input({ required: true }) text!: string;
  @Output() buttonClicked = new EventEmitter<void>();

  onClick(): void {
    this.buttonClicked.emit();
  }
}
