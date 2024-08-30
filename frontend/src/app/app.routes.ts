import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';
import { PageNotFoundComponent } from './pages/page-not-found/page-not-found.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './guards/auth.guard';
import { LogoutComponent } from './pages/logout/logout.component';
import { PrivateRoomComponent } from './pages/private-room/private-room.component';
import { GameComponent } from './pages/game/game.component';
import { CreatePrivateRoomComponent } from './components/create-private-room/create-private-room.component';
import { EnterInPrivateRoomComponent } from './components/enter-in-private-room/enter-in-private-room.component';

export const routes: Routes = [
    { path: "", component: HomeComponent, canActivate: [authGuard] },
    { path: "logout", component: LogoutComponent },
    { path: "login", component: LoginComponent },
    { path: "signup", component: SignupComponent },
    { path: "private", component: PrivateRoomComponent, canActivate: [authGuard] },
    { path: "private/game", component: GameComponent, canActivate: [authGuard] },
    { path: "private/create", component: CreatePrivateRoomComponent, canActivate: [authGuard] },
    { path: "private/enter", component: EnterInPrivateRoomComponent, canActivate: [authGuard] },
    { path: '**', component: PageNotFoundComponent }
];
