import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { PageNotFoundComponent } from './pages/page-not-found/page-not-found.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './guards/auth.guard';
import { CreatePrivateRoomComponent } from './components/create-private-room/create-private-room.component';
import { PrivateRoomComponent } from './pages/private-room/private-room.component';
import { LogoutComponent } from './pages/logout/logout.component';

export const routes: Routes = [
    { path: "", component: HomeComponent, canActivate: [authGuard] },
    { path: "private", component: PrivateRoomComponent },
    { path: "logout", component: LogoutComponent },
    { path: "test", component: CreatePrivateRoomComponent },
    { path: "login", component: LoginComponent },
    { path: "signup", component: SignupComponent },
    { path: '**', component: PageNotFoundComponent }
];
