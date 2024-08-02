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
})
export class SignupComponent {
  signUpForm: FormGroup<{ 
    username: FormControl<string>; 
    password: FormControl<string>; 
  }>;

  constructor(private authService: AuthService, private router: Router) {
    this.signUpForm = new FormGroup({
      username: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.minLength(1)] }),
      password: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.minLength(1)] }),
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn())
      this.router.navigate(['']);
  }

  onSignupButtonClicked(): void {
    if (!this.signUpForm.valid)
      return;

    const user: IUser = this.signUpForm.getRawValue();
    this.authService.doSignUp(user).subscribe({
      next: () => this.router.navigate(['login']),
      error: (error: HttpErrorResponse) => {
        if (error.status === 409)
          console.error(error)
        else if (error.status === 500)
          console.error("something went wrong")
      }
    });
  }
}
