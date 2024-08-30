import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-back-icon',
  standalone: true,
  imports: [],
  templateUrl: './back-icon.component.html'
})
export class BackIconComponent {
  constructor (private router: Router) {}

  onBack() {
    this.router.navigate(['']);
  }
}
