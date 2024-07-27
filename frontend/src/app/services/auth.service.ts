import { Injectable } from '@angular/core';
import { IUser } from '../interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor() {}

  doLogin(user: IUser): void {
    console.log(user);
  }
  
  doSignUp(user: IUser): void {
    console.log(user);
  }
}
