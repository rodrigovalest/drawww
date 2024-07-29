import { Component } from '@angular/core';
import { TitleComponent } from "../../components/title/title.component";
import { InputComponent } from "../../components/input/input.component";
import { ButtonComponent } from "../../components/button/button.component";
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { IUser } from '../../interfaces/user.interface';
import { LinkComponent } from "../../components/link/link.component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    TitleComponent,
    InputComponent,
    ButtonComponent,
    LinkComponent
],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  signUpForm: FormGroup<{ 
    username: FormControl<string>; 
    password: FormControl<string>; 
  }>;

  constructor(private authService: AuthService, private router: Router) {
    this.signUpForm = new FormGroup({
      username: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
      password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn())
      this.router.navigate(['']);
  }

  onLoginButtonClicked(): void {
    if (!this.signUpForm.valid) {
      console.error('Invalid form');
      return;
    }

    const user: IUser = this.signUpForm.getRawValue();
    this.authService.doSignUp(user);
  }
}
