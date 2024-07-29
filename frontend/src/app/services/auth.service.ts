import { Injectable } from '@angular/core';
import { IUser } from '../interfaces/user.interface';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  doLogin(user: IUser): Observable<{token: string}> {
    return this.httpClient.post<{token: string}>(`${this.apiUrl}/api/v1/users/login`, user);
  }
  
  doSignUp(user: IUser): Observable<IUser> {
    return this.httpClient.post<IUser>(`${this.apiUrl}/api/v1/users/register`, user);
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
