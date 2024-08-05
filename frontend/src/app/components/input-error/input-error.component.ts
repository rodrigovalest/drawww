import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-input-error',
  standalone: true,
  imports: [],
  templateUrl: './input-error.component.html',
})
export class InputErrorComponent {
  @Input() class: string = '';
  @Input({ required: true }) text!: string;
}
