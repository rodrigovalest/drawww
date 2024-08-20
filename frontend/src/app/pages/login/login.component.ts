import { Component } from '@angular/core';
import { TitleComponent } from "../../components/title/title.component";
import { InputComponent } from "../../components/input/input.component";
import { ButtonComponent } from "../../components/button/button.component";
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { IUser } from '../../interfaces/user.interface';
import { LinkComponent } from "../../components/link/link.component";
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { InputErrorComponent } from "../../components/input-error/input-error.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    TitleComponent,
    InputComponent,
    ButtonComponent,
    LinkComponent,
    InputErrorComponent
],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginForm: FormGroup<{ 
    username: FormControl<string>; 
    password: FormControl<string>; 
  }>;

  constructor(private authService: AuthService, private router: Router) {
    this.loginForm = new FormGroup({
      username: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
      password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn())
      this.router.navigate(['']);
  }

  onLoginButtonClicked(): void {
    if (!this.loginForm.valid) 
      return;

    const user: IUser = this.loginForm.getRawValue();
    this.authService.doLogin(user).subscribe({
      next: (data) => {
        this.authService.setToken(data.token);
        this.router.navigate(['']);
      },
      error: (httpError: HttpErrorResponse) => console.log(httpError)
    });
  }
}
