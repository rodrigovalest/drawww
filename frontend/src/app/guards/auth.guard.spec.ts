import { TestBed } from '@angular/core/testing';
import { CanMatchFn, Router } from '@angular/router';

import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  const executeGuard: CanMatchFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => authGuard(...guardParameters));

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('should allow access if user is logged in', () => {
    authService.isLoggedIn.and.returnValue(true);

    const result = executeGuard({ path: 'test' } as any, [] as any);

    expect(result).toBe(true);
  });

  it('should navigate to login if user is not logged in', () => {
    authService.isLoggedIn.and.returnValue(false);

    const result = executeGuard({ path: 'test' } as any, [] as any);

    expect(result).toBeUndefined();
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });
});
