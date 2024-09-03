import { Injectable } from '@angular/core';
import { IUser } from '../interfaces/user.interface';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ILoginResponse } from '../interfaces/login-response.interface';
import { JwtService } from './jwt.service';
import { IRefreshToken } from '../interfaces/refresh-token.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient) {}

  doLogin(user: IUser): Observable<ILoginResponse> {
    return this.httpClient.post<ILoginResponse>(`${environment.httpApiUrl}/api/v1/users/login`, user);
  }
  
  doSignUp(user: IUser): Observable<null> {
    return this.httpClient.post<null>(`${environment.httpApiUrl}/api/v1/users/register`, user);
  }

  refreshToken(): Observable<IRefreshToken> {
    const bearerToken = this.getToken();
    const headers = new HttpHeaders({ 'Authorization': `Bearer ${bearerToken}` });
    return this.httpClient.get<IRefreshToken>(`${environment.httpApiUrl}/api/v1/users/refresh`, { headers: headers });
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (token === null) return false;
    if (!JwtService.isTokenValid(token)) return false;
    return true;
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
