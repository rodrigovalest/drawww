import { Component } from '@angular/core';
import { ButtonComponent } from "../../components/button/button.component";
import { Router } from '@angular/router';
import { TitleComponent } from "../../components/title/title.component";
import { BackIconComponent } from "../../components/back-icon/back-icon.component";
import { LogoutIconComponent } from "../../components/logout-icon/logout-icon.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ButtonComponent, TitleComponent, BackIconComponent, LogoutIconComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  constructor (private router: Router) {}

  onPrivateMatch(): void {
    this.router.navigate(['private']);
  }
}
