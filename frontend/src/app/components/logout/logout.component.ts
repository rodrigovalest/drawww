import { Component, Input } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [],
  templateUrl: './logout.component.html',
})
export class LogoutComponent {
  @Input() class: string = '';

  constructor (private authService: AuthService, private router: Router) {}
  
  onClick() {
    this.authService.logout();
    this.router.navigate(['login']);
  }
}
