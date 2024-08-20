import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TitleComponent } from "../title/title.component";
import { InputComponent } from "../input/input.component";
import { ButtonComponent } from "../button/button.component";
import { InputErrorComponent } from "../input-error/input-error.component";

@Component({
  selector: 'app-create-private-room',
  standalone: true,
  imports: [TitleComponent, InputComponent, ButtonComponent, InputErrorComponent],
  templateUrl: './create-private-room.component.html',
})
export class CreatePrivateRoomComponent {
  roomForm: FormGroup<{ 
    password: FormControl<string>; 
  }>;

  constructor() {
    this.roomForm = new FormGroup({
      password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }

  onCreate() {

  }
}
