import { jwtDecode } from 'jwt-decode';

export class JwtService {
  static isTokenValid(token: string): boolean {
    try {
        const decodedToken: any = jwtDecode(token);
        const currentTime = Date.now() / 1000;
        
        if (decodedToken.exp < currentTime)
          return false;
        
        return true;
    } catch (error) {
        return false;
    }
  }
}
