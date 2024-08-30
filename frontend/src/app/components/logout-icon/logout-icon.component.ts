import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout-icon',
  standalone: true,
  imports: [],
  templateUrl: './logout-icon.component.html'
})
export class LogoutIconComponent {
  constructor (private router: Router) {}
  
  onLogout() {
    this.router.navigate(['/logout']);
  }
}
