import { Component } from '@angular/core';
import { ButtonComponent } from "../../components/button/button.component";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ButtonComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  constructor (private authService: AuthService, private router: Router) {}

  onLogout(): void {
    this.authService.logout()
    this.router.navigate(['login']);
  }
}
