import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { IRefreshToken } from '../interfaces/refresh-token.interface';
import { JwtService } from '../services/jwt.service';

export const authGuard: CanMatchFn = (route, segments) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isLoggedIn()) {
    return true;
  } else {
    let isLoggedIn: boolean = false;

    authService.refreshToken().subscribe({
      next: (data: IRefreshToken) => {
        const bearerToken = data.token;
        const username = JwtService.getUsernameByToken(bearerToken);
        console.log(username);

        if (username) {
          authService.login(bearerToken, username);
          isLoggedIn = true;
        } else {
          authService.logout();
          router.navigate(['login']);
          isLoggedIn = false;
        }
      },
      error: (httpError: HttpErrorResponse) => {
        authService.logout();
        router.navigate(['login']);
        isLoggedIn = false;
      }
    });

    return isLoggedIn;
  }
};
