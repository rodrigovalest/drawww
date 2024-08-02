import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { provideRouter, Router } from '@angular/router';
import { HomeComponent } from '../home/home.component';
import { of, throwError } from 'rxjs';
import { ILoginResponse } from '../../interfaces/login-response.interface';
import { HttpErrorResponse } from '@angular/common/http';

fdescribe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['doLogin', 'isLoggedIn', 'setToken']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([{ path: '', component: HomeComponent }]),
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to home if user is already logged in', () => {
    authService.isLoggedIn.and.returnValue(true);
    spyOn(router, 'navigate').and.callThrough();

    component.ngOnInit();

    expect(router.navigate).toHaveBeenCalledWith(['']);
    expect(router.url).toBe('/');
  });

  describe('login logic', () => {
    it('should successfully do login logic, navigate to home and set token in localStorage', () => {
      const mockedUsername = 'mockedUsername';
      const mockedPassword = 'mockedPassword';
      const mockedLoginResponse: ILoginResponse = { token: 'mockedToken' };
      component.loginForm.controls.username.setValue(mockedUsername);
      component.loginForm.controls.password.setValue(mockedPassword);
      const button: HTMLElement = fixture.nativeElement.querySelector('button');

      authService.doLogin.and.returnValue(of(mockedLoginResponse));
      spyOn(router, 'navigate').and.callThrough();

      fixture.detectChanges();
      button.click();

      expect(authService.doLogin).toHaveBeenCalledTimes(1);
      expect(authService.doLogin).toHaveBeenCalledWith({ 'username': mockedUsername, 'password': mockedPassword });
      expect(authService.setToken).toHaveBeenCalledWith(mockedLoginResponse.token);
      expect(router.navigate).toHaveBeenCalledWith(['']);
    });

    it('should do login logic and authService returns a HttpErrorResponse with 401 Unauthorized HTTP status code', () => {
      const mockedUsername = 'mockedUsername';
      const mockedPassword = 'mockedPassword';
      component.loginForm.controls.username.setValue(mockedUsername);
      component.loginForm.controls.password.setValue(mockedPassword);
      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      
      authService.doLogin.and.returnValue(throwError(() => new HttpErrorResponse({
        error: { message: `invalid password` },
        status: 401,
        statusText: 'Unauthorized'
      })));

      spyOn(router, 'navigate').and.callThrough();
  
      fixture.detectChanges();
      button.click();
  
      expect(authService.doLogin).toHaveBeenCalledTimes(1);
      expect(authService.doLogin).toHaveBeenCalledWith({ 'username': mockedUsername, 'password': mockedPassword });
      expect(authService.setToken).toHaveBeenCalledTimes(0);
      expect(router.navigate).toHaveBeenCalledTimes(0);
    });

    it('should do login logic and authService returns a HttpErrorResponse with 404 Not Found HTTP status code', () => {
      const mockedUsername = 'mockedUsername';
      const mockedPassword = 'mockedPassword';
      component.loginForm.controls.username.setValue(mockedUsername);
      component.loginForm.controls.password.setValue(mockedPassword);
      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      
      authService.doLogin.and.returnValue(throwError(() => new HttpErrorResponse({
        error: { message: `user not found` },
        status: 404,
        statusText: 'Not Found'
      })));

      spyOn(router, 'navigate').and.callThrough();
  
      fixture.detectChanges();
      button.click();
  
      expect(authService.doLogin).toHaveBeenCalledTimes(1);
      expect(authService.doLogin).toHaveBeenCalledWith({ 'username': mockedUsername, 'password': mockedPassword });
      expect(authService.setToken).toHaveBeenCalledTimes(0);
      expect(router.navigate).toHaveBeenCalledTimes(0);
    });
  });

  describe('validator', () => {
    it('should bind errors in template when validators shows it', () => {
      component.loginForm.controls.username.setValue('');
      component.loginForm.controls.password.setValue('');

      component.loginForm.controls.username.markAsTouched();
      component.loginForm.controls.username.markAsDirty();
      component.loginForm.controls.password.markAsTouched();
      component.loginForm.controls.password.markAsDirty();

      fixture.detectChanges();

      const usernameError = fixture.debugElement.query(By.css('#username-error')).nativeElement;
      const passwordError = fixture.debugElement.query(By.css('#password-error')).nativeElement;

      expect(usernameError.textContent).toContain('Username must be at least 1 character long.');
      expect(passwordError.textContent).toContain('Password must be at least 1 character long.');
    });

    it('should not permit do sign up if input data is not valid', () => {
      component.loginForm.controls.username.setValue('');
      component.loginForm.controls.password.setValue('');
      component.loginForm.controls.username.markAsTouched();
      component.loginForm.controls.username.markAsDirty();
      component.loginForm.controls.password.markAsTouched();
      component.loginForm.controls.password.markAsDirty();
      fixture.detectChanges();

      const button: HTMLElement = fixture.nativeElement.querySelector('button');
      button.click();

      expect(authService.doSignUp).toHaveBeenCalledTimes(0);
    });
  });
});
