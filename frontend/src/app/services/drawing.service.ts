import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { ISendUserDraw } from '../interfaces/send-draw.interface';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DrawingService {

  constructor(private httpClient: HttpClient, private authService: AuthService) {}

  sendUserDraw(data: ISendUserDraw): Observable<void> {
    const bearerToken = this.authService.getToken();

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${bearerToken}`,
      'Content-Type': 'application/json'
    });

    return this.httpClient.post<void>(`${environment.httpApiUrl}/api/v1/draws/upload`, JSON.stringify(data), { headers: headers });
  }
}
