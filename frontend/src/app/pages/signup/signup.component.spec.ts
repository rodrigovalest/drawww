import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignupComponent } from './signup.component';
import { AuthService } from '../../services/auth.service';
import { provideRouter, Router } from '@angular/router';
import { HomeComponent } from '../home/home.component';
import { LoginComponent } from '../login/login.component';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['doSignUp', 'isLoggedIn']);

    await TestBed.configureTestingModule({
      imports: [SignupComponent],
      providers: [
        provideRouter([{ path: '', component: HomeComponent }, { path: 'login', component: LoginComponent }]),
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('sign up logic', () => {
    it('should successfully do sign up logic and navigate to login', () => {
      const mockedUsername = 'mockedUsername';
      const mockedPassword = 'mockedPassword';
      component.signUpForm.controls.username.setValue(mockedUsername);
      component.signUpForm.controls.password.setValue(mockedPassword);
      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      
      authService.doSignUp.and.returnValue(of(null));
      spyOn(router, 'navigate').and.callThrough();
  
      fixture.detectChanges();
      button.click();
  
      expect(authService.doSignUp).toHaveBeenCalledTimes(1);
      expect(authService.doSignUp).toHaveBeenCalledWith({ 'username': mockedUsername, 'password': mockedPassword });
      expect(router.navigate).toHaveBeenCalledWith(['login']);
    });

    it('should do sign up logic and authService returns a HttpErrorResponse with 409 HTTP status code', () => {
      const mockedUsername = 'mockedUsername';
      const mockedPassword = 'mockedPassword';
      component.signUpForm.controls.username.setValue(mockedUsername);
      component.signUpForm.controls.password.setValue(mockedPassword);
      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      
      authService.doSignUp.and.returnValue(throwError(() => new HttpErrorResponse({
        error: { message: `username ${mockedUsername} is invalid` },
        status: 409,
        statusText: 'Conflict'
      })));

      spyOn(router, 'navigate').and.callThrough();
  
      fixture.detectChanges();
      button.click();
  
      expect(authService.doSignUp).toHaveBeenCalledTimes(1);
      expect(authService.doSignUp).toHaveBeenCalledWith({ 'username': mockedUsername, 'password': mockedPassword });
      expect(router.navigate).toHaveBeenCalledTimes(0);
    });
  });

  it('should navigate to home if user is already logged in', () => {
    authService.isLoggedIn.and.returnValue(true);
    spyOn(router, 'navigate').and.callThrough();

    component.ngOnInit();

    expect(router.navigate).toHaveBeenCalledWith(['']);
    expect(router.url).toBe('/');
  });

  describe('validator', () => {
    it('should bind errors in template when validators shows it', () => {
      component.signUpForm.controls.username.setValue('');
      component.signUpForm.controls.password.setValue('');

      component.signUpForm.controls.username.markAsTouched();
      component.signUpForm.controls.username.markAsDirty();
      component.signUpForm.controls.password.markAsTouched();
      component.signUpForm.controls.password.markAsDirty();

      fixture.detectChanges();

      const usernameError = fixture.debugElement.query(By.css('#username-error')).nativeElement;
      const passwordError = fixture.debugElement.query(By.css('#password-error')).nativeElement;

      expect(usernameError.textContent).toContain('Username must be at least 1 character long.');
      expect(passwordError.textContent).toContain('Password must be at least 1 character long.');
    });

    it('should not permit do sign up if input data is not valid', () => {
      component.signUpForm.controls.username.setValue('');
      component.signUpForm.controls.password.setValue('');
      component.signUpForm.controls.username.markAsTouched();
      component.signUpForm.controls.username.markAsDirty();
      component.signUpForm.controls.password.markAsTouched();
      component.signUpForm.controls.password.markAsDirty();
      fixture.detectChanges();

      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      button.click();

      expect(authService.doSignUp).toHaveBeenCalledTimes(0);
    });
  });
});
