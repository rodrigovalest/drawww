import { Component } from '@angular/core';
import { TitleComponent } from "../../components/title/title.component";
import { ButtonComponent } from "../../components/button/button.component";
import { Router } from '@angular/router';
import { BackIconComponent } from "../../components/back-icon/back-icon.component";

@Component({
  selector: 'app-private-room',
  standalone: true,
  imports: [TitleComponent, ButtonComponent, BackIconComponent],
  templateUrl: './private-room.component.html'
})
export class PrivateRoomComponent {

  constructor(private router: Router) {}

  onEnterInPrivateRoom() {
    this.router.navigate(['private/enter']);
  }

  onCreateAPrivateRoom() {
    this.router.navigate(['private/create']);
  }
}
