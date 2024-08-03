import { Component } from '@angular/core';
import { ButtonComponent } from "../../components/button/button.component";
import { Router } from '@angular/router';
import { TitleComponent } from "../../components/title/title.component";
import { LogoutComponent } from "../../components/logout/logout.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ButtonComponent, TitleComponent, LogoutComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  constructor (private router: Router) {}

  onPlayInPubicRoom() {
    
  }

  onPlayInPrivateRoom() {
    
  }
}
