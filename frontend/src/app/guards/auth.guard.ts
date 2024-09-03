import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';
import { IRefreshToken } from '../interfaces/refresh-token.interface';

export const authGuard: CanMatchFn = (route, segments) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isLoggedIn()) {
    return true;
  } else {
    let isLoggedIn: boolean = true;

    authService.refreshToken().subscribe({
      next: (data: IRefreshToken) => {
        authService.setToken(data.token);
        isLoggedIn = true;
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
