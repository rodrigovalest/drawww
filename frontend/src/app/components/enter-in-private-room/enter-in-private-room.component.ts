import { Component } from '@angular/core';
import { TitleComponent } from '../title/title.component';
import { ButtonComponent } from '../button/button.component';
import { InputErrorComponent } from '../input-error/input-error.component';
import { InputComponent } from '../input/input.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-enter-in-private-room',
  standalone: true,
  imports: [TitleComponent, InputErrorComponent, InputComponent, ButtonComponent],
  templateUrl: './enter-in-private-room.component.html'
})
export class EnterInPrivateRoomComponent {
  roomForm: FormGroup<{ 
    roomId: FormControl<string>; 
    password: FormControl<string>; 
  }>;

  constructor() {
    this.roomForm = new FormGroup({
      roomId: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
      password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }
  
  onEnter() {

  }
}
