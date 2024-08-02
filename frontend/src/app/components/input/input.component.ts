import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './input.component.html',
})
export class InputComponent {
  @Input() class: string = '';
  @Input({ required: true }) text!: string;
  @Input({ required: true }) control!: FormControl<string>;

  private _type: 'text' | 'password' = 'text';

  @Input()
  set type(value: string) {
    if (value === 'text' || value === 'password') {
      this._type = value;
    } else {
      console.warn(`Invalid input type: ${value}. Defaulting to 'text'.`);
      this._type = 'text';
    }
  }

  get type(): string {
    return this._type;
  }
}
