import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { IUser } from '../interfaces/user.interface';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { ILoginResponse } from '../interfaces/login-response.interface';

describe('AuthService', () => {
  let apiUrl = environment.httpApiUrl;
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  describe('doLogin', () => {
    it('should doLogin and returns token', () => {
      const user: IUser = { username: 'testuser', password: 'testpass' };
      const mockResponseData: ILoginResponse = { token: 'fake-jwt-token' };
  
      authService
        .doLogin(user)
        .subscribe(data => expect(data).toBe(mockResponseData));

      const req = httpMock.expectOne(`${apiUrl}/api/v1/users/login`);
      req.flush(mockResponseData);

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
    });

    it('should doLogin and returns 401 Unauthorized', () => {
      const user: IUser = { username: 'testuser', password: 'testpass' };
      const mockResponseError = { message: 'password do not match' };
      
      authService
        .doLogin(user)
        .subscribe({
          error: (error: HttpErrorResponse) => {
            expect(error.status).toBe(401);
            expect(error.error).toBe(mockResponseError);
          }
        });

      const req = httpMock.expectOne(`${apiUrl}/api/v1/users/login`);
      req.flush(mockResponseError, { status: 401, statusText: 'Unauthorized' });

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
    });
  });

  describe('doSignUp', () => {
    it('should doSignUp and return 201 created', () => {
      const user: IUser = { username: 'testuser', password: 'testpass' };

      authService
        .doSignUp(user)
        .subscribe(response => expect(response).toBeNull());

      const req = httpMock.expectOne(`${apiUrl}/api/v1/users/register`);
      req.flush(null, { status: 201, statusText: 'Created' });

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
    });

    it('should doSignUp, with invalid username and return 422 Unprocessable entity', () => {
      const user: IUser = { username: 'testuser', password: '' };
      const mockResponseError = { message: 'invalid fields', errors: [{'password': 'password must not be empty'}] };

      authService
        .doSignUp(user)
        .subscribe({
          error: (error) => {
            expect(error.status).toBe(422);
            expect(error.error).toBe(mockResponseError);
          }
        });

      const req = httpMock.expectOne(`${apiUrl}/api/v1/users/register`);
      req.flush(mockResponseError, { status: 422, statusText: 'Unprocessable Entity' });

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
    });
    
    it('should doSignUp, with invalid username and return 409 Conflict', () => {
      const user: IUser = { username: 'testuser', password: 'testpass' };
      const mockResponseError = { message: 'username {testuser} is invalid' };

      authService
        .doSignUp(user)
        .subscribe({
          error: (error) => {
            expect(error.status).toBe(409);
            expect(error.error).toBe(mockResponseError);
          }
        });

      const req = httpMock.expectOne(`${apiUrl}/api/v1/users/register`);
      req.flush(mockResponseError, { status: 409, statusText: 'Conflict' });

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(user);
    });
  });

  describe('setToken', () => {
    it('should set the token in localStorage', () => {
      const token = 'fake-jwt-token';
      authService.setToken(token);
      expect(localStorage.getItem('token')).toBe(token);
    });
  });

  describe('getToken', () => {
    it('should return the token from localStorage', () => {
      const token = 'fake-jwt-token';
      localStorage.setItem('token', token);
      expect(authService.getToken()).toBe(token);
    });
  
    it('should return null if no token is set in localStorage', () => {
      localStorage.removeItem('token');
      expect(authService.getToken()).toBeNull();
    });
  });

  describe('logout', () => {
    it('should remove the token from localStorage', () => {
      const token = 'fake-jwt-token';
      localStorage.setItem('token', token);
      authService.logout();
      expect(localStorage.getItem('token')).toBeNull();
    });
  });

  describe('isLoggedIn', () => {
    it('should return true if token is set in localStorage', () => {
      const token = 'fake-jwt-token';
      localStorage.setItem('token', token);
      expect(authService.isLoggedIn()).toBe(true);
    });
  
    it('should return false if no token is set in localStorage', () => {
      localStorage.removeItem('token');
      expect(authService.isLoggedIn()).toBe(false);
    });
  });
});
