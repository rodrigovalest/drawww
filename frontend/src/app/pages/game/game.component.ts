import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RxStompService } from '../../services/rx-stomp.service';
import { GameStateType } from '../../types/game-state.type';
import { WaitingComponent } from "../waiting/waiting.component";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [WaitingComponent],
  templateUrl: './game.component.html'
})
export class GameComponent {

  gameState: GameStateType = 'NOT_STARTED';

  constructor (private rxStompService: RxStompService, private router: Router) {}

  ngOnInit(): void {
    this.rxStompService.getGameState$().subscribe(state => {
      this.gameState = state;
      console.log('game state: ', state);

      if (state === 'NOT_STARTED')
        this.router.navigate([''])
    });
  }

  ngOnDestroy(): void {
    alert('sure you want to exit, some changes may be lost');
    this.rxStompService.disconnect();
  }
}
