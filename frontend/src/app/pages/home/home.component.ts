import { Component } from '@angular/core';
import { ButtonComponent } from "../../components/button/button.component";
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { TitleComponent } from "../../components/title/title.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ButtonComponent, TitleComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  constructor (private authService: AuthService, private router: Router) {}

  onPrivateMatch(): void {
    this.router.navigate(['private']);
  }
}
