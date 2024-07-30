import { Injectable } from '@angular/core';
import { IUser } from '../interfaces/user.interface';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ILoginResponse } from '../interfaces/login-response.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  doLogin(user: IUser): Observable<ILoginResponse> {
    return this.httpClient.post<ILoginResponse>(`${this.apiUrl}/api/v1/users/login`, user);
  }
  
  doSignUp(user: IUser): Observable<null> {
    return this.httpClient.post<null>(`${this.apiUrl}/api/v1/users/register`, user);
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
