import { Component } from '@angular/core';
import { TitleComponent } from "../title/title.component";
import { ButtonComponent } from "../button/button.component";
import { UserIconComponent } from "../user-icon/user-icon.component";

@Component({
  selector: 'app-waiting',
  standalone: true,
  imports: [TitleComponent, ButtonComponent, UserIconComponent],
  templateUrl: './waiting.component.html',
})
export class WaitingComponent {
  roomId: string = '';
  
  onReady() {

  }
}
